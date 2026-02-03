import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src')
    }
  },
  server: {
    port: 8081, // 固定前端端口为8081
    open: true,
    // 代理配置：把前端/api请求转发到后端8080
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true, // 关键：开启跨域转发
        secure: false // 关闭HTTPS校验（开发环境）
      }
    }
  }
})