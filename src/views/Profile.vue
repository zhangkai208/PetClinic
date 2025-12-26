<template>
  <div class="profile-page">
    <div class="page-header">
      <h1>个人中心</h1>
      <p>管理您的账户信息</p>
    </div>

    <div class="profile-content">
      <!-- 头像区域 -->
      <div class="avatar-section">
        <el-avatar :size="100" :src="form.avatar || defaultAvatar">
          {{ form.nickname?.charAt(0) || form.username?.charAt(0) || 'U' }}
        </el-avatar>
        <el-upload
          :show-file-list="false"
          :before-upload="handleAvatarUpload"
          accept="image/*"
        >
          <el-button type="primary" text>更换头像</el-button>
        </el-upload>
      </div>

      <!-- 表单区域 -->
      <el-form
        ref="formRef"
        :model="form"
        :rules="formRules"
        label-width="100px"
        label-position="left"
        class="profile-form"
      >
        <el-form-item label="用户名">
          <el-input v-model="form.username" disabled />
        </el-form-item>

        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="form.nickname" placeholder="请输入昵称" />
        </el-form-item>

        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>

        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" :loading="submitting" @click="handleSubmit">
            保存修改
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserInfoStore } from '@/stores/userinfo'
import { getById, update, upload } from '@/api/sysuser'

const userInfoStore = useUserInfoStore()
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

const formRef = ref()
const submitting = ref(false)
const form = reactive({
  id: null,
  username: '',
  nickname: '',
  phone: '',
  email: '',
  avatar: ''
})

const formRules = {
  nickname: [{ max: 20, message: '昵称最多20个字符', trigger: 'blur' }],
  phone: [{ pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }],
  email: [{ type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }]
}

// 加载用户信息
const loadUserInfo = async () => {
  const storedInfo = userInfoStore.userInfo
  if (storedInfo?.userId) {
    try {
      const res = await getById(storedInfo.userId)
      if (res.code === 200 && res.data) {
        Object.assign(form, {
          id: res.data.id,
          username: res.data.username,
          nickname: res.data.nickname,
          phone: res.data.phone,
          email: res.data.email,
          avatar: res.data.avatar
        })
      }
    } catch (e) {
      console.error('加载用户信息失败:', e)
    }
  }
}

// 上传头像
const handleAvatarUpload = async (file) => {
  if (file.size > 2 * 1024 * 1024) {
    ElMessage.warning('头像大小不能超过2MB')
    return false
  }
  try {
    const res = await upload(file)
    if (res.code === 200 && res.data) {
      form.avatar = res.data
      // 上传成功后自动保存到数据库
      await update(form.id, { avatar: form.avatar })
      // 更新本地存储
      userInfoStore.setUserInfo({
        ...userInfoStore.userInfo,
        avatar: form.avatar
      })
      ElMessage.success('头像更新成功')
    }
  } catch (e) {
    ElMessage.error('头像上传失败')
  }
  return false
}

// 提交表单
const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    await update(form.id, {
      nickname: form.nickname,
      phone: form.phone,
      email: form.email,
      avatar: form.avatar
    })
    // 更新本地存储
    userInfoStore.setUserInfo({
      ...userInfoStore.userInfo,
      nickname: form.nickname,
      avatar: form.avatar
    })
    ElMessage.success('保存成功')
  } catch (e) {
    console.error('保存失败:', e)
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadUserInfo()
})
</script>

<style lang="scss" scoped>
.profile-page {
  max-width: 600px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 32px;

  h1 {
    font-size: 24px;
    font-weight: 600;
    color: #1f2937;
    margin: 0 0 4px 0;
  }

  p {
    font-size: 14px;
    color: #6b7280;
    margin: 0;
  }
}

.profile-content {
  background: white;
  border-radius: 16px;
  padding: 32px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}

.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
  margin-bottom: 32px;
  padding-bottom: 32px;
  border-bottom: 1px solid #f0f0f0;

  .el-avatar {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    font-size: 36px;
  }
}

.profile-form {
  .el-form-item {
    margin-bottom: 24px;
  }
}
</style>
