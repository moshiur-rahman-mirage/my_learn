import { useEffect, useState } from "react";
import { api, assetUrl } from "../api.js";

/**
 * Create products with an image.
 * - storage=local : multipart upload through the API
 * - storage=s3    : (Phase 4) ask the API for a presigned PUT URL, upload straight to S3, then send only the key
 */
export default function Admin() {
  const [products, setProducts] = useState([]);
  const [config, setConfig] = useState(null);
  const [form, setForm] = useState({ name: "", description: "", price: "" });
  const [file, setFile] = useState(null);
  const [status, setStatus] = useState(null);
  const [error, setError] = useState(null);

  const load = () => api("/api/products").then(setProducts).catch((e) => setError(e.message));

  useEffect(() => {
    load();
    api("/api/lab/config").then(setConfig).catch(() => {});
  }, []);

  async function submit(e) {
    e.preventDefault();
    setError(null);
    setStatus("Saving...");
    try {
      if (config?.storage === "s3" && file) {
        // 1) presigned URL  2) PUT the bytes directly to S3  3) create the product with the key
        const target = await api(
          `/api/products/upload-url?filename=${encodeURIComponent(file.name)}&contentType=${encodeURIComponent(file.type)}`,
          { method: "POST" }
        );
        const put = await fetch(target.url, { method: "PUT", body: file, headers: { "Content-Type": file.type } });
        if (!put.ok) throw new Error(`S3 upload failed: ${put.status}`);
        await api("/api/products", {
          method: "POST",
          body: JSON.stringify({ ...form, price: Number(form.price), imageKey: target.key }),
        });
      } else {
        const fd = new FormData();
        fd.append("name", form.name);
        fd.append("description", form.description);
        fd.append("price", form.price);
        if (file) fd.append("image", file);
        await api("/api/products", { method: "POST", body: fd });
      }
      setForm({ name: "", description: "", price: "" });
      setFile(null);
      e.target.reset();
      setStatus("Saved");
      load();
    } catch (err) {
      setStatus(null);
      setError(err.message);
    }
  }

  async function remove(id) {
    if (!confirm("Delete this product?")) return;
    await api(`/api/products/${id}`, { method: "DELETE" }).catch((e) => setError(e.message));
    load();
  }

  return (
    <section>
      <h1>Admin</h1>
      <p className="muted">
        Upload mode: <code>{config?.storage === "s3" ? "presigned PUT to S3" : "multipart via API (local storage)"}</code>
      </p>
      <form className="panel form" onSubmit={submit}>
        <input placeholder="Name" required value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} />
        <input placeholder="Description" value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })} />
        <input placeholder="Price" type="number" step="0.01" min="0.01" required value={form.price}
               onChange={(e) => setForm({ ...form, price: e.target.value })} />
        <input type="file" accept="image/*" onChange={(e) => setFile(e.target.files[0] || null)} />
        <button type="submit">Create product</button>
        {status && <span className="ok">{status}</span>}
      </form>
      {error && <p className="error">{error}</p>}

      <table className="table">
        <thead>
          <tr><th>Image</th><th>Name</th><th>Price</th><th>Image key</th><th></th></tr>
        </thead>
        <tbody>
          {products.map((p) => (
            <tr key={p.id}>
              <td>{p.imageUrl ? <img className="thumb" src={assetUrl(p.imageUrl)} alt="" /> : "—"}</td>
              <td>{p.name}</td>
              <td>${Number(p.price).toFixed(2)}</td>
              <td><code>{p.imageKey || "—"}</code></td>
              <td><button className="danger" onClick={() => remove(p.id)}>Delete</button></td>
            </tr>
          ))}
        </tbody>
      </table>
    </section>
  );
}
