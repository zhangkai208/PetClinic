import { createRouter, createWebHistory } from 'vue-router'
import { useTokenStore } from '@/stores/token'

const routes = [
  {
    path: '/login',
    name: 'login',
    component: () => import('@/views/Login.vue'),
    meta: { public: true, title: '登录' }
  },
  {
    path: '/',
    component: () => import('@/views/layout/Layout.vue'),
    redirect: '/pets',
    children: [
      {
        path: 'pets',
        name: 'pets',
        component: () => import('@/views/pet/Pets.vue'),
        meta: { title: '我的宠物' }
      },
      {
        path: 'profile',
        name: 'profile',
        component: () => import('@/views/Profile.vue'),
        meta: { title: '个人中心' }
      }
    ]
  },
  { path: '/:pathMatch(.*)*', redirect: '/' }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  // 设置页面标题
  document.title = to.meta.title ? `${to.meta.title} - PetClinic` : 'PetClinic'

  // 使用 Pinia store 获取 token
  const tokenStore = useTokenStore()
  if (!to.meta.public && !tokenStore.token) {
    next('/login')
  } else {
    next()
  }
})

export default router
