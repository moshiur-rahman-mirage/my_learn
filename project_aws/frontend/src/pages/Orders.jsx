import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { api } from "../api.js";

export default function Orders() {
  const [orders, setOrders] = useState([]);
  const [error, setError] = useState(null);

  useEffect(() => {
    const load = () => api("/api/orders").then(setOrders).catch((e) => setError(e.message));
    load();
    const t = setInterval(load, 3000);
    return () => clearInterval(t);
  }, []);

  return (
    <section>
      <h1>Recent orders</h1>
      {error && <p className="error">{error}</p>}
      <table className="table">
        <thead><tr><th>#</th><th>Email</th><th>Total</th><th>Status</th><th>Attempts</th><th>Created</th></tr></thead>
        <tbody>
          {orders.map((o) => (
            <tr key={o.id}>
              <td><Link to={`/orders/${o.id}`}>{o.id}</Link></td>
              <td>{o.customerEmail}</td>
              <td>${Number(o.total).toFixed(2)}</td>
              <td><span className={`badge ${o.status.toLowerCase()}`}>{o.status}</span></td>
              <td>{o.attempts}</td>
              <td>{new Date(o.createdAt).toLocaleString()}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </section>
  );
}
