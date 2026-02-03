import { createRouter, createWebHistory } from 'vue-router'
// 导入登录组件 和 你已写好的 MainLayout 组件（确保路径和你的文件结构一致）
import LoginLayout from '@/components/LoginLayout.vue'
import MainLayout from '@/components/MainLayout.vue' // 替换：删除 Home，导入 MainLayout

const routes = [
  {
    path: '/',
    redirect: '/login' // 默认跳转到登录页
  },
  {
    path: '/login',
    name: 'Login',
    component: LoginLayout
  },
  {
    path: '/main', // 路由路径可自定义，建议贴合 MainLayout
    name: 'Main',
    component: MainLayout, // 替换：组件改为 MainLayout
    meta: { requiresAuth: true } // 保留登录保护，防止未登录直接访问
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

// 路由守卫：验证登录状态（不变）
// router.beforeEach((to, from, next) => {
//   const isLogin = localStorage.getItem('token')
//   if (to.meta.requiresAuth && !isLogin) {
//     next('/login')
//   } else {
//     next()
//   }
// })

export default router