import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import { shouldEnableOffline, enableOfflineMock, setOfflineAuth } from '@/utils/offlineMock'

// 离线模式：不连后端即可查看所有页面样式（npm run dev:offline 或 ?offline=1）
if (shouldEnableOffline()) {
  enableOfflineMock()
  setOfflineAuth()
}

const app = createApp(App)
app.use(router)
app.mount('#app')

// 新增：验证挂载是否成功
console.log('===== Vue应用挂载完成 =====')
console.log('挂载的节点：', document.getElementById('app'))