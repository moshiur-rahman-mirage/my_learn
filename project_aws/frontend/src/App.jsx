import { NavLink, Route, Routes } from "react-router-dom";
import Catalog from "./pages/Catalog.jsx";
import Admin from "./pages/Admin.jsx";
import Cart from "./pages/Cart.jsx";
import Orders from "./pages/Orders.jsx";
import OrderStatus from "./pages/OrderStatus.jsx";
import Lab from "./pages/Lab.jsx";
import { API_BASE } from "./api.js";

export default function App() {
  return (
    <div className="app">
      <header className="topbar">
        <span className="brand">☁ CloudMart</span>
        <nav>
          <NavLink to="/" end>Catalog</NavLink>
          <NavLink to="/cart">Cart</NavLink>
          <NavLink to="/orders">Orders</NavLink>
          <NavLink to="/admin">Admin</NavLink>
          <NavLink to="/lab">Lab</NavLink>
        </nav>
        <span className="api-base" title="VITE_API_BASE">API: {API_BASE || "same origin"}</span>
      </header>
      <main>
        <Routes>
          <Route path="/" element={<Catalog />} />
          <Route path="/cart" element={<Cart />} />
          <Route path="/orders" element={<Orders />} />
          <Route path="/orders/:id" element={<OrderStatus />} />
          <Route path="/admin" element={<Admin />} />
          <Route path="/lab" element={<Lab />} />
          <Route path="*" element={<p>Not found</p>} />
        </Routes>
      </main>
    </div>
  );
}
