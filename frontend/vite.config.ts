import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  build: {
    rollupOptions: {
      output: {
        manualChunks(id) {
          if (id.includes('/node_modules/element-plus/')) return 'element-plus'
          if (id.includes('/node_modules/@element-plus/')) return 'element-icons'
          if (id.includes('/node_modules/vue/') || id.includes('/node_modules/@vue/') || id.includes('/node_modules/vue-router/')) return 'vue-vendor'
        },
      },
    },
  },
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: process.env.VITE_API_TARGET || 'http://127.0.0.1:8082',
        changeOrigin: true,
        configure(proxy) {
          proxy.on('error', (_error, _request, response) => {
            const httpResponse = response as import('node:http').ServerResponse | undefined
            if (httpResponse && !httpResponse.headersSent) {
              httpResponse.writeHead(503, { 'Content-Type':'application/json', 'X-Backend-Unavailable':'true' })
              httpResponse.end(JSON.stringify({ message:'Backend is offline' }))
            }
          })
        },
      },
    },
  },
})
