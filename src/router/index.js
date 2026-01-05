import { createRouter, createWebHistory } from 'vue-router'
import { useTokenStore } from '@/stores/token'
import { useUserInfoStore } from '@/stores/userinfo'

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
        meta: { title: '宠物宝贝', roles: [1, 3] }  // 仅宠物主人(1)和管理员(3)
      },
      {
        path: 'gallery',
        name: 'gallery',
        component: () => import('@/views/pet/PetGallery.vue'),
        meta: { title: '宠物相册', roles: [1, 3] }  // 仅宠物主人(1)和管理员(3)
      },
      {
        path: 'health-records',
        name: 'healthRecords',
        component: () => import('@/views/health/HealthRecords.vue'),
        meta: { title: '健康记录', roles: [1, 2, 3] }  // 所有角色可访问
      },
      {
        path: 'chat',
        name: 'aiChat',
        component: () => import('@/views/chat/AIChat.vue'),
        meta: { title: 'AI助手' }  // 所有角色可访问
      },
      {
        path: 'profile',
        name: 'profile',
        component: () => import('@/views/Profile.vue'),
        meta: { title: '个人中心' }  // 所有角色可访问
      },
      {
        path: 'service-providers',
        name: 'serviceProviders',
        component: () => import('@/views/service/ServiceProviders.vue'),
        meta: { title: '服务商管理', roles: [2, 3] }  // 服务商(2)和管理员(3)
      },
      {
        path: 'admin/users',
        name: 'adminUsers',
        component: () => import('@/views/admin/Users.vue'),
        meta: { title: '用户管理', roles: [3] }  // 仅管理员(3)
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

  // 使用 Pinia store 获取 token 和用户信息
  const tokenStore = useTokenStore()
  const userInfoStore = useUserInfoStore()

  // 未登录检查
  if (!to.meta.public && !tokenStore.token) {
    next('/login')
    return
  }

  // 获取用户角色
  const userRole = userInfoStore.userInfo?.roleType

  // 检查路由是否有角色限制
  if (to.meta.roles && userRole) {
    if (!to.meta.roles.includes(userRole)) {
      // 无权限，根据角色重定向到合适的页面
      if (userRole === 2) {
        // 服务商重定向到服务商管理
        next('/service-providers')
      } else if (userRole === 3) {
        // 管理员重定向到用户管理
        next('/admin/users')
      } else {
        next('/profile')
      }
      return
    }
  }

  // 处理根路径的重定向（根据角色选择首页）
  if (to.path === '/') {
    if (userRole === 2) {
      // 服务商默认进入服务商管理页面
      next('/service-providers')
      return
    } else if (userRole === 3) {
      // 管理员默认进入用户管理页面
      next('/admin/users')
      return
    }
  }

  next()
})

export default router

