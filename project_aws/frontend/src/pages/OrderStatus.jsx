import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { api } from "../api.js";

/** Polls the order every 2 s until the background worker (local / SQS + Lambda) finishes it. */
export default function OrderStatus() {
  const { id } = useParams();
  const [order, setOrder] = useState(null);
  const [error, setError] = useState(null);

  useEffect(() => {
    let timer;
    const poll = async () => {
      try {
        const o = await api(`/api/orders/${id}`);
        setOrder(o);
        if (o.status === "NEW") timer = setTimeout(poll, 2000);
      } catch (e) {
        setError(e.message);
      }
    };
    poll();
    return () => clearTimeout(timer);
  }, [id]);

  if (error) return <p className="error">{error}</p>;
  if (!order) return <p>Loading...</p>;

  return (
    <section>
      <h1>Order #{order.id}</h1>
      <p>
        Status: <span className={`badge ${order.status.toLowerCase()}`}>{order.status}</span>
        {order.status === "NEW" && <span className="muted"> processing in the background…</span>}
      </p>
      {order.lastError && <p className="error">Last error (attempt {order.attempts}): {order.lastError}</p>}
      <p>Email: {order.customerEmail} · Total: ${Number(order.total).toFixed(2)}</p>
      <ul>
        {order.lines.map((l, i) => (
          <li key={i}>{l.quantity} × {l.name} (${Number(l.price).toFixed(2)})</li>
        ))}
      </ul>
      <p className="muted">
        Created {new Date(order.createdAt).toLocaleString()}
        {order.processedAt && ` · processed ${new Date(order.processedAt).toLocaleString()}`}
      </p>
      <Link to="/orders">All orders</Link>
    </section>
  );
}
