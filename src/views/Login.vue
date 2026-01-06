<template>
  <div class="login-container">
    <!-- 左侧品牌区域 -->
    <div class="brand-section">
      <div class="brand-content">
        <div class="logo">
          <span class="logo-icon">🐾</span>
          <h1>PetClinic</h1>
        </div>
        <p class="tagline">专业的宠物健康管理平台</p>
        <div class="features">
          <div class="feature-item">
            <el-icon><Check /></el-icon>
            <span>全面的宠物档案管理</span>
          </div>
          <div class="feature-item">
            <el-icon><Check /></el-icon>
            <span>便捷的在线预约服务</span>
          </div>
          <div class="feature-item">
            <el-icon><Check /></el-icon>
            <span>完善的健康记录追踪</span>
          </div>
          <div class="feature-item">
            <el-icon><Check /></el-icon>
            <span>专业的宠物医疗服务</span>
          </div>
        </div>
      </div>
      <div class="brand-footer">
        <p>© 2024 PetClinic. All rights reserved.</p>
      </div>
    </div>

    <!-- 右侧登录区域 -->
    <div class="login-section">
      <div class="login-card">
        <div class="login-header">
          <h2>欢迎回来</h2>
          <p>登录您的账户以继续</p>
        </div>

        <el-tabs v-model="activeTab" class="login-tabs">
          <!-- 登录表单 -->
          <el-tab-pane label="登录" name="login">
            <el-form 
              ref="loginFormRef" 
              :model="loginForm" 
              :rules="loginRules" 
              label-width="0"
              size="large"
            >
              <el-form-item prop="username">
                <el-input 
                  v-model="loginForm.username" 
                  placeholder="请输入用户名"
                  :prefix-icon="User"
                />
              </el-form-item>
              <el-form-item prop="password">
                <el-input 
                  v-model="loginForm.password" 
                  type="password" 
                  placeholder="请输入密码"
                  :prefix-icon="Lock"
                  show-password
                  @keyup.enter="handleLogin"
                />
              </el-form-item>
              <el-form-item>
                <el-button 
                  type="primary" 
                  :loading="loading" 
                  class="submit-btn"
                  @click="handleLogin"
                >
                  {{ loading ? '登录中...' : '登 录' }}
                </el-button>
              </el-form-item>
            </el-form>
          </el-tab-pane>

          <!-- 注册表单 -->
          <el-tab-pane label="注册" name="register">
            <el-form 
              ref="registerFormRef" 
              :model="registerForm" 
              :rules="registerRules" 
              label-width="0"
              size="large"
            >
              <el-form-item prop="username">
                <el-input 
                  v-model="registerForm.username" 
                  placeholder="请输入用户名"
                  :prefix-icon="User"
                />
              </el-form-item>
              <el-form-item prop="password">
                <el-input 
                  v-model="registerForm.password" 
                  type="password" 
                  placeholder="请输入密码（6-20位）"
                  :prefix-icon="Lock"
                  show-password
                />
              </el-form-item>
              <el-form-item prop="confirmPassword">
                <el-input 
                  v-model="registerForm.confirmPassword" 
                  type="password" 
                  placeholder="请再次输入密码"
                  :prefix-icon="Lock"
                  show-password
                />
              </el-form-item>
              <el-form-item prop="nickname">
                <el-input 
                  v-model="registerForm.nickname" 
                  placeholder="请输入昵称"
                  :prefix-icon="UserFilled"
                />
              </el-form-item>
              <el-form-item>
                <el-button 
                  type="primary" 
                  :loading="loading" 
                  class="submit-btn"
                  @click="handleRegister"
                >
                  {{ loading ? '注册中...' : '注 册' }}
                </el-button>
              </el-form-item>
            </el-form>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, UserFilled, Check } from '@element-plus/icons-vue'
import { login, register } from '@/api/sysuser'
import { useTokenStore } from '@/stores/token'
import { useUserInfoStore } from '@/stores/userinfo'

const router = useRouter()
const tokenStore = useTokenStore()
const userInfoStore = useUserInfoStore()

const activeTab = ref('login')
const loading = ref(false)

// 登录表单
const loginFormRef = ref()
const loginForm = reactive({
  username: '',
  password: ''
})

const loginRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

// 注册表单
const registerFormRef = ref()
const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  nickname: ''
})

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== registerForm.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const registerRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度为3-20个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度为6-20个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ],
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 2, max: 20, message: '昵称长度为2-20个字符', trigger: 'blur' }
  ]
}

// 登录处理
const handleLogin = async () => {
  const valid = await loginFormRef.value.validate().catch(() => false)
  if (!valid) return
  
  loading.value = true
  try {
    const res = await login(loginForm)
    // 保存Token和用户信息
    tokenStore.setToken(res.data.token)
    userInfoStore.setUserInfo({
      userId: res.data.userId,
      username: res.data.username,
      nickname: res.data.nickname,
      roleType: res.data.roleType,
      avatar: res.data.avatar
    })
    ElMessage.success('登录成功')
    // 根据角色跳转到不同页面
    const roleType = res.data.roleType
    if (roleType === 3) {
      router.push('/admin/dashboard')  // 管理员进入数据看板
    } else if (roleType === 2) {
      router.push('/service-providers')  // 服务商进入服务商管理
    } else {
      router.push('/pets')  // 宠物主人进入宠物列表
    }
  } catch (error) {
    console.error('登录失败:', error)
  } finally {
    loading.value = false
  }
}

// 注册处理
const handleRegister = async () => {
  const valid = await registerFormRef.value.validate().catch(() => false)
  if (!valid) return
  
  loading.value = true
  try {
    await register({
      username: registerForm.username,
      password: registerForm.password,
      nickname: registerForm.nickname || undefined
    })
    ElMessage.success('注册成功，请登录')
    activeTab.value = 'login'
    loginForm.username = registerForm.username
    registerFormRef.value.resetFields()
  } catch (error) {
    console.error('注册失败:', error)
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
.login-container {
  display: flex;
  min-height: 100vh;
  background: #f0f2f5;
}

// 左侧品牌区域
.brand-section {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 60px;
  background: linear-gradient(135deg, #1890ff 0%, #36cfc9 50%, #52c41a 100%);
  color: white;
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    top: -50%;
    left: -50%;
    width: 200%;
    height: 200%;
    background: radial-gradient(circle, rgba(255,255,255,0.1) 0%, transparent 50%);
    animation: pulse 15s ease-in-out infinite;
  }

  @keyframes pulse {
    0%, 100% { transform: translate(0, 0); }
    50% { transform: translate(50px, 50px); }
  }
}

.brand-content {
  position: relative;
  z-index: 1;
  max-width: 500px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 16px;

  .logo-icon {
    font-size: 56px;
  }

  h1 {
    font-size: 42px;
    font-weight: 700;
    margin: 0;
    letter-spacing: 2px;
  }
}

.tagline {
  font-size: 20px;
  opacity: 0.9;
  margin-bottom: 48px;
}

.features {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 16px;
  opacity: 0.95;

  .el-icon {
    font-size: 20px;
    background: rgba(255,255,255,0.2);
    padding: 6px;
    border-radius: 50%;
  }
}

.brand-footer {
  position: absolute;
  bottom: 30px;
  left: 60px;
  font-size: 14px;
  opacity: 0.7;
}

// 右侧登录区域
.login-section {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 40px;
  background: #f7f9fc;
}

.login-card {
  width: 100%;
  max-width: 420px;
  padding: 48px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 8px 40px rgba(0, 0, 0, 0.08);
}

.login-header {
  text-align: center;
  margin-bottom: 32px;

  h2 {
    font-size: 28px;
    font-weight: 600;
    color: #1f2937;
    margin: 0 0 8px 0;
  }

  p {
    font-size: 15px;
    color: #6b7280;
    margin: 0;
  }
}

.login-tabs {
  :deep(.el-tabs__nav-wrap::after) {
    display: none;
  }

  :deep(.el-tabs__nav) {
    width: 100%;
    display: flex;
  }

  :deep(.el-tabs__item) {
    flex: 1;
    text-align: center;
    font-size: 16px;
    font-weight: 500;
    color: #9ca3af;
    padding: 0 0 12px 0;

    &.is-active {
      color: #1890ff;
    }
  }

  :deep(.el-tabs__active-bar) {
    background: linear-gradient(90deg, #1890ff, #36cfc9);
  }
}

.submit-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 500;
  border-radius: 8px;
  background: linear-gradient(135deg, #1890ff 0%, #36cfc9 100%);
  border: none;
  margin-top: 8px;

  &:hover {
    background: linear-gradient(135deg, #40a9ff 0%, #5cdbd3 100%);
  }
}

:deep(.el-input__wrapper) {
  border-radius: 8px;
  box-shadow: 0 0 0 1px #e5e7eb inset;

  &:hover {
    box-shadow: 0 0 0 1px #1890ff inset;
  }

  &.is-focus {
    box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.2) inset, 0 0 0 1px #1890ff inset;
  }
}

:deep(.el-input__inner) {
  height: 48px;
}

// 响应式设计
@media (max-width: 900px) {
  .login-container {
    flex-direction: column;
  }

  .brand-section {
    flex: none;
    padding: 40px 30px;
    min-height: 300px;
  }

  .brand-footer {
    display: none;
  }

  .login-section {
    padding: 30px 20px;
  }

  .login-card {
    padding: 32px 24px;
  }
}
</style>
