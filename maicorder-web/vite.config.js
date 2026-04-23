import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

export default defineConfig(({ mode }) => ({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src')
    }
  },
  server: {
    port: 8081, // 固定前端端口为8081
    open: true,
    // 离线模式不代理 /api，避免连不上后端报错；正常 dev 时转发到 8080
    ...(mode !== 'offline' && {
      proxy: {
        '/api': {
          target: 'http://localhost:8087',
          changeOrigin: true,
          secure: false
        }
      }
    })
  }
}))