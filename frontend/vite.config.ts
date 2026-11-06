import vue from '@vitejs/plugin-vue'
import tailwindcss from '@tailwindcss/vite'
import { defineConfig } from 'vite'

// The UI always calls relative /api URLs. In dev, Vite proxies them to Spring Boot;
// in production, Nginx (Docker) or Spring Boot itself (native) serves both on one origin,
// so no CORS configuration is needed anywhere.
export default defineConfig({
  plugins: [vue(), tailwindcss()],
  server: {
    proxy: {
      '/api': 'http://localhost:8080',
      '/actuator': 'http://localhost:8080',
    },
  },
})
