import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { api, sessionId, track } from "../api.js";

export default function Cart() {
  const [cart, setCart] = useState(null);
  const [email, setEmail] = useState(localStorage.getItem("cloudmart.email") || "");
  const [error, setError] = useState(null);
  const navigate = useNavigate();
  const sid = sessionId();

  useEffect(() => {
    api(`/api/cart/${sid}`).then(setCart).catch((e) => setError(e.message));
  }, [sid]);

  async function remove(productId) {
    setCart(await api(`/api/cart/${sid}/items/${productId}`, { method: "DELETE" }));
  }

  async function checkout(e) {
    e.preventDefault();
    setError(null);
    try {
      localStorage.setItem("cloudmart.email", email);
      const order = await api("/api/orders", { method: "POST", body: JSON.stringify({ sessionId: sid, email }) });
      track("checkout", null);
      navigate(`/orders/${order.id}`);
    } catch (err) {
      setError(err.message);
    }
  }

  if (!cart) return <p>Loading...</p>;
  const total = cart.items.reduce((s, i) => s + Number(i.price) * i.quantity, 0);

  return (
    <section>
      <h1>Cart</h1>
      <p className="muted">Session: <code>{sid}</code></p>
      {error && <p className="error">{error}</p>}
      {cart.items.length === 0 ? (
        <p>Your cart is empty.</p>
      ) : (
        <>
          <table className="table">
            <thead><tr><th>Product</th><th>Qty</th><th>Price</th><th>Total</th><th></th></tr></thead>
            <tbody>
              {cart.items.map((i) => (
                <tr key={i.productId}>
                  <td>{i.name}</td>
                  <td>{i.quantity}</td>
                  <td>${Number(i.price).toFixed(2)}</td>
                  <td>${(Number(i.price) * i.quantity).toFixed(2)}</td>
                  <td><button className="danger" onClick={() => remove(i.productId)}>Remove</button></td>
                </tr>
              ))}
            </tbody>
          </table>
          <p><strong>Total: ${total.toFixed(2)}</strong></p>
          <form className="panel form" onSubmit={checkout}>
            <input type="email" required placeholder="you@example.com" value={email} onChange={(e) => setEmail(e.target.value)} />
            <button type="submit">Place order</button>
            <span className="muted">Tip: an email containing "fail" simulates a failing order (DLQ practice).</span>
          </form>
        </>
      )}
    </section>
  );
}
