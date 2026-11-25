import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

// Dev: the browser calls /api on the Vite server, which proxies to Spring Boot -> no CORS needed locally.
// Change the target (or set VITE_API_BASE) to point the UI at an EC2 IP / ALB / CloudFront later.
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      "/api": process.env.API_TARGET || "http://localhost:8080",
    },
  },
});
