import { createRouter, createWebHistory } from 'vue-router'
import LoginLayout from '@/components/LoginLayout.vue'
import MainLayout from '@/components/MainLayout.vue'
// 假设你的记录页组件是这个名字，请确保文件存在
import RecordsPage from '@/components/RecordsPage.vue' 

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: LoginLayout,
    // 【关键修改】：标记此页面进入时不需要全局白屏过渡
    // 这样你的 LoginLayout 自定义入场动画就能正常播放
    meta: { noGlobalTransition: true }
  },
  {
    path: '/main',
    name: 'Main',
    component: MainLayout,
    meta: { requiresAuth: true }
  },
  {
    path: '/records',
    name: 'Records',
    component: RecordsPage,
    meta: { requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  // 获取目标路由是否需要登录权限
  const requiresAuth = to.meta.requiresAuth

  if (requiresAuth && !token) {
    // 1. 需要登录但没 token -> 去登录页
    next('/login')
  } else if (to.path === '/login' && token) {
    // 2. 已登录但想去登录页 -> 踢回主页
    next('/main')
  } else {
    // 3. 其他情况 -> 放行
    next()
  }
})

export default router