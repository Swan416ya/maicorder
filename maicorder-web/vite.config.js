// vite.config.js  (放在 maicorder-web/ 根目录)
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

export default defineConfig({
  plugins: [
    vue()   // 这行必须有，且是第一个
  ],

  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src')
    }
  },

  build: {
    target: 'esnext',
    minify: 'esbuild',
    sourcemap: false
  }
})