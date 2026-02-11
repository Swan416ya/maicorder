import { createApp } from 'vue'
import App from './App.vue'
import router from './router'

// 新增：验证main.js是否执行
console.log('===== main.js 开始执行 =====')
console.log('App组件：', App)
console.log('路由：', router)

const app = createApp(App)
app.use(router)
app.mount('#app')

// 新增：验证挂载是否成功
console.log('===== Vue应用挂载完成 =====')
console.log('挂载的节点：', document.getElementById('app'))