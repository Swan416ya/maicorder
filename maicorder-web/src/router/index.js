import { createRouter, createWebHistory } from 'vue-router'
import LoginLayout from '@/pages/LoginLayout.vue'
import MainLayout from '@/pages/MainLayout.vue'
import RecordsPage from '@/pages/RecordsPage.vue'
import MoreToolPage from '@/pages/moreToolPage.vue'
import TestPage from '@/pages/TestPage.vue'

const routes = [
  {
    path: '/',
    redirect: '/main'
  },
  {
    path: '/login',
    name: 'Login',
    component: LoginLayout,
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
  },
  {
    // 添加记录详情页路由
    path: '/checkin/detail/:id',
    name: 'CheckInDetail',
    component: () => import('@/pages/RecordDetailPage.vue'),
    meta: { requiresAuth: true },
    props: true // 将路由参数作为props传递给组件
  },
  {
    path: '/moreToolPage',
    name: 'MoreToolPage',
    component: MoreToolPage,
    meta: { requiresAuth: false }
  },
  {
    path: '/test',
    name: 'Test',
    component: TestPage,
    meta: { requiresAuth: false }
  },
  {
    path: '/tool/WordWallPage',
    name: 'WordWallPage',
    component: () => import('@/pages/WordWallPage.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/tool/wmB50Page',
    name: 'wmB50Page',
    component: () => import('@/pages/wmB50Page.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/tool/badgePage',
    name: 'BadgePage',
    component: () => import('@/pages/badgePage.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/SegmentedButtonTest',
    name: 'SegmentedButtonTest',
    component: () => import('@/pages/SegmentedButtonTestPage.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/ToolbarTest',
    name: 'ToolbarTest',
    component: () => import('@/pages/ToolbarTest.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('@/pages/ProfilePage.vue'),
    meta: { requiresAuth: true }
  },
  {
    path:'/github',
    name: 'Github',
    component: () => import('@/pages/githubPage.vue'),
    meta: { requiresAuth: false }
  },
  {
    path:'/bindQR',
    name: 'BindQR',
    component: () => import('@/pages/bindQR.vue'),
    meta: { requiresAuth: false }
  },
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const requiresAuth = to.meta.requiresAuth

  if (requiresAuth && !token) {
    next('/login')
  } else if (to.path === '/login' && token) {
    next('/main')
  } else {
    next()
  }
})

export default router