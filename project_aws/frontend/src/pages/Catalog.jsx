import { useEffect, useState } from "react";
import { api, assetUrl, sessionId, track } from "../api.js";

export default function Catalog() {
  const [products, setProducts] = useState([]);
  const [error, setError] = useState(null);
  const [message, setMessage] = useState(null);

  useEffect(() => {
    api("/api/products").then(setProducts).catch((e) => setError(e.message));
    track("catalog_view", null);
  }, []);

  async function addToCart(p) {
    try {
      await api(`/api/cart/${sessionId()}/items`, {
        method: "POST",
        body: JSON.stringify({ productId: p.id, quantity: 1 }),
      });
      track("add_to_cart", p.id);
      setMessage(`Added "${p.name}" to cart`);
      setTimeout(() => setMessage(null), 2000);
    } catch (e) {
      setError(e.message);
    }
  }

  return (
    <section>
      <h1>Catalog</h1>
      {error && <p className="error">{error}</p>}
      {message && <p className="ok">{message}</p>}
      <div className="grid">
        {products.map((p) => (
          <article key={p.id} className="card" onMouseEnter={() => track("product_view", p.id)}>
            {p.imageUrl ? (
              <img src={assetUrl(p.imageUrl)} alt={p.name} />
            ) : (
              <div className="placeholder">{p.name.charAt(0)}</div>
            )}
            <h3>{p.name}</h3>
            <p className="muted">{p.description}</p>
            <div className="row">
              <strong>${Number(p.price).toFixed(2)}</strong>
              <button onClick={() => addToCart(p)}>Add to cart</button>
            </div>
          </article>
        ))}
      </div>
    </section>
  );
}
