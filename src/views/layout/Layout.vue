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
        text-color="rgba(255,255,255,0.65)"
        active-text-color="#fff"
        router
      >
      <el-menu-item v-if="isAdmin" index="/admin/dashboard">
          <el-icon><DataLine /></el-icon>
          <template #title>数据看板</template>
        </el-menu-item>
        <el-menu-item v-if="canSeePets" index="/pets">
          <el-icon><HomeFilled /></el-icon>
          <template #title>宠物宝贝</template>
        </el-menu-item>
        <el-menu-item v-if="canSeePets" index="/gallery">
          <el-icon><Picture /></el-icon>
          <template #title>宠物相册</template>
        </el-menu-item>
        <el-menu-item index="/health-records">
          <el-icon><Tickets /></el-icon>
          <template #title>健康记录</template>
        </el-menu-item>
        <el-menu-item index="/appointments">
          <el-icon><Calendar /></el-icon>
          <template #title>预约管理</template>
        </el-menu-item>
        <el-menu-item index="/chat">
          <el-icon><ChatDotRound /></el-icon>
          <template #title>AI助手</template>
        </el-menu-item>
        <el-menu-item index="/profile">
          <el-icon><User /></el-icon>
          <template #title>个人中心</template>
        </el-menu-item>
        <!-- 服务商菜单 -->
        <el-menu-item v-if="canSeeProviders" index="/service-providers">
          <el-icon><OfficeBuilding /></el-icon>
          <template #title>服务商管理</template>
        </el-menu-item>
        <!-- 管理员菜单 -->
        <el-menu-item v-if="isAdmin" index="/admin/users">
          <el-icon><UserFilled /></el-icon>
          <template #title>用户管理</template>
        </el-menu-item>
      </el-menu>

      <div class="sidebar-footer">
        <el-tooltip :content="isCollapsed ? '展开菜单' : '收起菜单'" placement="right">
          <div class="collapse-btn" @click="toggleCollapse">
            <el-icon :size="18">
              <component :is="isCollapsed ? Expand : Fold" />
            </el-icon>
          </div>
        </el-tooltip>
      </div>
    </aside>

    <!-- 右侧主区域 -->
    <div class="main-area" :class="{ 'sidebar-collapsed': isCollapsed }">
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
  HomeFilled, User, UserFilled, ArrowDown, SwitchButton, Expand, Fold, Picture, ChatDotRound, OfficeBuilding, Tickets, Calendar, DataLine 
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
// 宠物主人(1)和管理员(3)可以看到宠物菜单，服务商(2)看不到
const canSeePets = computed(() => userInfo.value.roleType === 1 || userInfo.value.roleType === 3)
// 服务商(2)和管理员(3)可以看到服务商管理菜单
const canSeeProviders = computed(() => userInfo.value.roleType === 2 || userInfo.value.roleType === 3)
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
  background: #f0f2f5;
}

// 侧边栏 - 深色主题
.sidebar {
  width: 210px;
  background: linear-gradient(180deg, #1e293b 0%, #0f172a 100%);
  display: flex;
  flex-direction: column;
  transition: width 0.3s cubic-bezier(0.2, 0, 0, 1);
  position: fixed;
  left: 0;
  top: 0;
  bottom: 0;
  z-index: 100;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.15);

  &.collapsed {
    width: 64px;

    .sidebar-header {
      padding: 20px 0;
      justify-content: center;
    }

    .logo-icon {
      font-size: 28px;
    }
  }
}

.sidebar-header {
  display: flex;
  align-items: center;
  padding: 20px 16px;
  gap: 10px;
  background: rgba(255, 255, 255, 0.05);
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);

  .logo-icon {
    font-size: 28px;
    transition: font-size 0.3s;
  }

  .logo-text {
    font-size: 18px;
    font-weight: 600;
    color: white;
    letter-spacing: 1px;
    white-space: nowrap;
  }
}

.sidebar :deep(.el-menu) {
  border: none;
  padding: 12px 8px;
  flex: 1;
  background: transparent !important;

  .el-menu-item {
    height: 44px;
    line-height: 44px;
    margin-bottom: 4px;
    border-radius: 6px;
    font-size: 14px;
    transition: all 0.2s;

    &:hover {
      background: rgba(255, 255, 255, 0.1) !important;
    }

    &.is-active {
      background: rgba(255, 255, 255, 0.2) !important;
      color: white !important;
      font-weight: 500;

      &::before {
        content: '';
        position: absolute;
        left: 0;
        top: 50%;
        transform: translateY(-50%);
        width: 3px;
        height: 20px;
        background: #fff;
        border-radius: 0 2px 2px 0;
      }
    }

    .el-icon {
      font-size: 18px;
      margin-right: 10px;
    }
  }

  // 收起时的样式
  &.el-menu--collapse {
    padding: 12px 0;  // 移除左右padding
    
    .el-menu-item {
      padding-left: 0 !important;
      padding-right: 0 !important;
      justify-content: center;
      
      .el-icon {
        margin-right: 0;
      }

      &.is-active::before {
        display: none;
      }
    }
  }
}

.sidebar-footer {
  padding: 12px;
  border-top: 1px solid rgba(255, 255, 255, 0.08);
  display: flex;
  justify-content: center;
}

.collapse-btn {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 6px;
  cursor: pointer;
  color: rgba(255, 255, 255, 0.65);
  transition: all 0.2s;

  &:hover {
    background: rgba(255, 255, 255, 0.1);
    color: white;
  }
}

// 主区域
.main-area {
  flex: 1;
  margin-left: 210px;
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  transition: margin-left 0.3s cubic-bezier(0.2, 0, 0, 1);

  &.sidebar-collapsed {
    margin-left: 64px;
  }
}

// 顶部导航栏
.header {
  height: 56px;
  background: white;
  padding: 0 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  position: sticky;
  top: 0;
  z-index: 50;
}

.header-left {
  :deep(.el-breadcrumb__item) {
    .el-breadcrumb__inner {
      color: #8c8c8c;
      font-weight: 400;
      font-size: 14px;

      &.is-link:hover {
        color: #1d42ab;
      }
    }

    &:last-child .el-breadcrumb__inner {
      color: #262626;
      font-weight: 500;
    }
  }
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 6px;
  transition: background 0.2s;

  &:hover {
    background: #f5f5f5;
  }

  .el-avatar {
    background: linear-gradient(135deg, #1d42ab 0%, #2b5fd9 100%);
  }

  .user-name {
    font-size: 14px;
    color: #262626;
    font-weight: 500;
    max-width: 100px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .el-icon {
    font-size: 12px;
    color: #8c8c8c;
  }
}

// 主内容区
.content {
  flex: 1;
  padding: 20px 24px;
  overflow-y: scroll;  // 始终显示滚动条，避免内容增减时布局跳动
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
  transition: all 0.2s ease;
}

.fade-slide-enter-from {
  opacity: 0;
  transform: translateX(10px);
}

.fade-slide-leave-to {
  opacity: 0;
  transform: translateX(-10px);
}

// 响应式
@media (max-width: 768px) {
  .sidebar {
    width: 64px;

    .logo-text {
      display: none;
    }
  }

  .main-area {
    margin-left: 64px;
  }

  .content {
    padding: 16px;
  }

  .header {
    padding: 0 16px;
  }
}
</style>
