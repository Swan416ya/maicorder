import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

export default defineConfig({
  plugins: [
    vue()   // 这行必须有，且是第一个，配置正确
  ],

  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src') // 别名配置正确，和代码中的@对应
    }
  },

  // 显式配置base，避免生产构建路径歧义（优化1）
  base: '/',

  build: {
    // 调整为更兼容的es2020，提升编译稳定性（优化2）
    target: 'es2020',
    minify: 'esbuild',
    sourcemap: false,
    // 自动清空dist目录，避免旧文件干扰（优化3）
    emptyOutDir: true
  }
})