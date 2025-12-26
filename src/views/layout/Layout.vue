<template>
  <div class="layout-container">
    <!-- 左侧菜单 -->
    <aside class="sidebar" :class="{ collapsed: isCollapsed }">
      <div class="sidebar-header">
        <span class="logo-icon">🐾</span>
        <transition name="fade">
          <span v-if="!isCollapsed" class="logo-text">PetClinic</span>
        </transition>
      </div>

      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapsed"
        :collapse-transition="false"
        background-color="transparent"
        text-color="#a3aed0"
        active-text-color="#fff"
        router
      >
        <el-menu-item index="/pets">
          <el-icon><PriceTag /></el-icon>
          <template #title>我的宠物</template>
        </el-menu-item>
        <el-menu-item index="/profile">
          <el-icon><User /></el-icon>
          <template #title>个人中心</template>
        </el-menu-item>
        <!-- 管理员菜单 -->
        <el-menu-item v-if="isAdmin" index="/admin/users">
          <el-icon><Setting /></el-icon>
          <template #title>用户管理</template>
        </el-menu-item>
      </el-menu>

      <div class="sidebar-footer">
        <el-button 
          :icon="isCollapsed ? Expand : Fold" 
          circle 
          @click="toggleCollapse"
        />
      </div>
    </aside>

    <!-- 右侧主区域 -->
    <div class="main-area">
      <!-- 顶部导航栏 -->
      <header class="header">
        <div class="header-left">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ currentTitle }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>

        <div class="header-right">
          <el-dropdown trigger="click" @command="handleCommand">
            <div class="user-info">
              <el-avatar :size="36" :src="userInfo.avatar || defaultAvatar">
                {{ userInfo.nickname?.charAt(0) || userInfo.username?.charAt(0) || 'U' }}
              </el-avatar>
              <span class="user-name">{{ userInfo.nickname || userInfo.username || '用户' }}</span>
              <el-icon><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <el-icon><User /></el-icon>个人中心
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  <el-icon><SwitchButton /></el-icon>退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>

      <!-- 主内容区域 -->
      <main class="content">
        <router-view v-slot="{ Component }">
          <transition name="fade-slide" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  PriceTag, User, ArrowDown, SwitchButton, Expand, Fold, Setting 
} from '@element-plus/icons-vue'
import { logout } from '@/api/sysuser'
import { useTokenStore } from '@/stores/token'
import { useUserInfoStore } from '@/stores/userinfo'

const route = useRoute()
const router = useRouter()
const tokenStore = useTokenStore()
const userInfoStore = useUserInfoStore()

const isCollapsed = ref(false)
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

const userInfo = computed(() => userInfoStore.userInfo || {})
const isAdmin = computed(() => userInfo.value.roleType === 3)
const activeMenu = computed(() => route.path)
const currentTitle = computed(() => route.meta?.title || '首页')

const toggleCollapse = () => {
  isCollapsed.value = !isCollapsed.value
}

const handleCommand = async (command) => {
  if (command === 'profile') {
    router.push('/profile')
  } else if (command === 'logout') {
    try {
      await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
      await logout()
      tokenStore.removeToken()
      userInfoStore.removeUserInfo()
      ElMessage.success('已退出登录')
      router.push('/login')
    } catch (e) {
      if (e !== 'cancel') {
        console.error('退出登录失败:', e)
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.layout-container {
  display: flex;
  min-height: 100vh;
  background: #f4f7fe;
}

// 侧边栏
.sidebar {
  width: 260px;
  background: linear-gradient(180deg, #1e293b 0%, #0f172a 100%);
  display: flex;
  flex-direction: column;
  transition: width 0.3s ease;
  position: fixed;
  left: 0;
  top: 0;
  bottom: 0;
  z-index: 100;

  &.collapsed {
    width: 80px;

    .sidebar-header {
      padding: 20px 0;
      justify-content: center;
    }
  }
}

.sidebar-header {
  display: flex;
  align-items: center;
  padding: 24px 20px;
  gap: 12px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);

  .logo-icon {
    font-size: 32px;
  }

  .logo-text {
    font-size: 22px;
    font-weight: 700;
    color: white;
    letter-spacing: 1px;
  }
}

.sidebar :deep(.el-menu) {
  border: none;
  padding: 16px 12px;
  flex: 1;

  .el-menu-item {
    height: 48px;
    line-height: 48px;
    margin-bottom: 4px;
    border-radius: 10px;
    font-size: 15px;

    &:hover {
      background: rgba(255, 255, 255, 0.08);
    }

    &.is-active {
      background: linear-gradient(135deg, #1890ff 0%, #36cfc9 100%);
      color: white;
    }

    .el-icon {
      font-size: 20px;
      margin-right: 12px;
    }
  }
}

.sidebar-footer {
  padding: 16px;
  border-top: 1px solid rgba(255, 255, 255, 0.08);
  display: flex;
  justify-content: center;

  .el-button {
    background: rgba(255, 255, 255, 0.1);
    border: none;
    color: #a3aed0;

    &:hover {
      background: rgba(255, 255, 255, 0.15);
      color: white;
    }
  }
}

// 主区域
.main-area {
  flex: 1;
  margin-left: 260px;
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  transition: margin-left 0.3s ease;

  .collapsed + & {
    margin-left: 80px;
  }
}

.sidebar.collapsed + .main-area {
  margin-left: 80px;
}

// 顶部导航栏
.header {
  height: 72px;
  background: white;
  padding: 0 32px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
  position: sticky;
  top: 0;
  z-index: 50;
}

.header-left {
  :deep(.el-breadcrumb__item) {
    .el-breadcrumb__inner {
      color: #6b7280;
      font-weight: 400;

      &.is-link:hover {
        color: #1890ff;
      }
    }

    &:last-child .el-breadcrumb__inner {
      color: #1f2937;
      font-weight: 500;
    }
  }
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  padding: 6px 12px;
  border-radius: 8px;
  transition: background 0.2s;

  &:hover {
    background: #f3f4f6;
  }

  .user-name {
    font-size: 14px;
    color: #374151;
    font-weight: 500;
  }

  .el-icon {
    font-size: 12px;
    color: #9ca3af;
  }
}

// 主内容区
.content {
  flex: 1;
  padding: 24px 32px;
  overflow-y: auto;
}

// 过渡动画
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: all 0.25s ease;
}

.fade-slide-enter-from {
  opacity: 0;
  transform: translateX(20px);
}

.fade-slide-leave-to {
  opacity: 0;
  transform: translateX(-20px);
}

// 响应式
@media (max-width: 768px) {
  .sidebar {
    width: 80px;

    .logo-text {
      display: none;
    }
  }

  .main-area {
    margin-left: 80px;
  }

  .content {
    padding: 16px;
  }
}
</style>
