import { createRouter, createWebHistory } from 'vue-router'
import LoginLayout from '@/components/LoginLayout.vue'
import MainLayout from '@/components/MainLayout.vue'

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: LoginLayout
  },
  {
    path: '/main',
    name: 'Main',
    component: MainLayout,
    meta: { requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

// 恢复路由守卫并优化
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const requiresAuth = to.meta.requiresAuth

  if (requiresAuth && !token) {
    // 未登录，跳转到登录页
    next('/login')
  } else if (!requiresAuth && token && to.path === '/login') {
    // 已登录，访问登录页自动跳转到主页面
    next('/main')
  } else {
    next()
  }
})

export default router