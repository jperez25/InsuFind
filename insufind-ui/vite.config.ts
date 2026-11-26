import { defineConfig, loadEnv } from 'vite'
import react from '@vitejs/plugin-react'

// The browser only talks to /api; in dev Vite forwards it to the Spring Boot backend (API_TARGET overrides it).
export default defineConfig(({ mode }) => ({
  plugins: [react()],
  server: {
    port: 5173,
    strictPort: true,
    proxy: {
      '/api': loadEnv(mode, '.', '').API_TARGET ?? 'http://localhost:8080',
    },
  },
}))
