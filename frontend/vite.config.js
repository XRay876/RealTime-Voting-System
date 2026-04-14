import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  define: {
    global: 'window',
  },
  server: {
    proxy: {
      '/api/v1/auth': { target: 'http://localhost:8080', changeOrigin: true }, 
      '/api/v1/users': { target: 'http://localhost:8080', changeOrigin: true }, 
      '/api/polls': { target: 'http://localhost:8081', changeOrigin: true },
      '/ws-voting': {
        target: 'http://localhost:8081',
        ws: true, 
        changeOrigin: true
      },
      '/api/options': { target: 'http://localhost:8081', changeOrigin: true },
      
    }
  }
});