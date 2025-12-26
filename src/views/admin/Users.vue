<template>
  <div class="users-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-info">
        <h1>用户管理</h1>
        <p>管理系统中的所有用户账号</p>
      </div>
      <el-button type="primary" :icon="Plus" @click="openDialog()">
        新增用户
      </el-button>
    </div>

    <!-- 搜索栏 -->
    <div class="filter-bar">
      <el-input 
        v-model="searchKeyword" 
        placeholder="搜索用户名或昵称..." 
        :prefix-icon="Search"
        clearable
        class="search-input"
      />
      <el-select v-model="filterRole" placeholder="角色类型" clearable>
        <el-option label="全部" value="" />
        <el-option label="宠物主人" :value="1" />
        <el-option label="服务商" :value="2" />
        <el-option label="管理员" :value="3" />
      </el-select>
      <el-select v-model="filterStatus" placeholder="状态" clearable>
        <el-option label="全部" value="" />
        <el-option label="启用" :value="1" />
        <el-option label="禁用" :value="0" />
      </el-select>
    </div>

    <!-- 用户表格 -->
    <div class="table-wrapper">
      <el-table 
        :data="filteredUsers" 
        v-loading="loading"
        stripe
        style="width: 100%"
      >
        <el-table-column label="用户信息" min-width="200">
          <template #default="{ row }">
            <div class="user-info">
              <el-avatar :size="40" :src="row.avatar">
                {{ row.nickname?.charAt(0) || row.username?.charAt(0) || 'U' }}
              </el-avatar>
              <div class="user-text">
                <div class="username">{{ row.username }}</div>
                <div class="nickname">{{ row.nickname || '未设置昵称' }}</div>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="联系方式" min-width="180">
          <template #default="{ row }">
            <div class="contact-info">
              <div v-if="row.phone">📱 {{ row.phone }}</div>
              <div v-if="row.email">📧 {{ row.email }}</div>
              <span v-if="!row.phone && !row.email" class="no-data">未设置</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="角色" width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="getRoleType(row.roleType)">
              {{ getRoleText(row.roleType) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="创建时间" width="160" align="center">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>

        <el-table-column label="操作" width="150" align="center" fixed="right">
          <template #default="{ row }">
            <el-button text type="primary" @click="openDialog(row)">
              <el-icon><Edit /></el-icon>
            </el-button>
            <el-button text type="danger" @click="handleDelete(row)">
              <el-icon><Delete /></el-icon>
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 分页 -->
    <div class="pagination-wrapper">
      <el-pagination
        v-model:current-page="currentPage"
        :page-size="pageSize"
        :total="total"
        layout="total, prev, pager, next"
        @current-change="loadUsers"
      />
    </div>

    <!-- 新增/编辑弹窗 -->
    <el-dialog 
      v-model="dialogVisible" 
      :title="isEdit ? '编辑用户' : '新增用户'"
      width="500px"
      destroy-on-close
    >
      <el-form 
        ref="formRef" 
        :model="form" 
        :rules="formRules" 
        label-width="80px"
        label-position="left"
      >
        <el-form-item label="用户名" prop="username">
          <el-input 
            v-model="form.username" 
            placeholder="请输入用户名" 
            :disabled="isEdit"
          />
        </el-form-item>
        
        <el-form-item label="密码" prop="password" v-if="!isEdit">
          <el-input 
            v-model="form.password" 
            type="password" 
            placeholder="请输入密码"
            show-password
          />
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
        
        <el-form-item label="角色" prop="roleType">
          <el-select v-model="form.roleType" placeholder="请选择角色" style="width: 100%">
            <el-option label="宠物主人" :value="1" />
            <el-option label="服务商" :value="2" />
            <el-option label="管理员" :value="3" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">
          {{ isEdit ? '保存' : '新增' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Edit, Delete } from '@element-plus/icons-vue'
import { page as getUsers, create, update, deleteUser } from '@/api/sysuser'

const loading = ref(false)
const submitting = ref(false)
const users = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const searchKeyword = ref('')
const filterRole = ref('')
const filterStatus = ref('')

// 弹窗相关
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()
const form = reactive({
  id: null,
  username: '',
  password: '',
  nickname: '',
  phone: '',
  email: '',
  roleType: 1,
  status: 1
})

const formRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 20, message: '用户名长度2-20个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少6个字符', trigger: 'blur' }
  ],
  roleType: [{ required: true, message: '请选择角色', trigger: 'change' }]
}

// 筛选后的用户列表
const filteredUsers = computed(() => {
  let result = users.value
  if (searchKeyword.value) {
    const kw = searchKeyword.value.toLowerCase()
    result = result.filter(u => 
      u.username?.toLowerCase().includes(kw) ||
      u.nickname?.toLowerCase().includes(kw)
    )
  }
  if (filterRole.value !== '') {
    result = result.filter(u => u.roleType === filterRole.value)
  }
  if (filterStatus.value !== '') {
    result = result.filter(u => u.status === filterStatus.value)
  }
  return result
})

// 工具函数
const getRoleText = (role) => {
  const map = { 1: '宠物主人', 2: '服务商', 3: '管理员' }
  return map[role] || '未知'
}

const getRoleType = (role) => {
  const map = { 1: 'info', 2: 'warning', 3: 'danger' }
  return map[role] || 'info'
}

const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 加载用户列表
const loadUsers = async () => {
  loading.value = true
  try {
    const res = await getUsers(currentPage.value, pageSize.value)
    users.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (e) {
    console.error('加载失败:', e)
  } finally {
    loading.value = false
  }
}

// 打开弹窗
const openDialog = (user = null) => {
  if (user) {
    isEdit.value = true
    Object.assign(form, {
      id: user.id,
      username: user.username,
      password: '',
      nickname: user.nickname,
      phone: user.phone,
      email: user.email,
      roleType: user.roleType,
      status: user.status
    })
  } else {
    isEdit.value = false
    Object.assign(form, {
      id: null,
      username: '',
      password: '',
      nickname: '',
      phone: '',
      email: '',
      roleType: 1,
      status: 1
    })
  }
  dialogVisible.value = true
}

// 提交表单
const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    if (isEdit.value) {
      // 编辑时不提交密码字段（除非用户填写了新密码）
      const { password, ...updateData } = form
      await update(form.id, updateData)
      ElMessage.success('更新成功')
    } else {
      await create(form)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadUsers()
  } catch (e) {
    console.error('操作失败:', e)
  } finally {
    submitting.value = false
  }
}

// 删除用户
const handleDelete = async (user) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除用户 "${user.username}" 吗？此操作不可恢复。`,
      '确认删除',
      { confirmButtonText: '删除', cancelButtonText: '取消', type: 'warning' }
    )
    await deleteUser(user.id)
    ElMessage.success('删除成功')
    loadUsers()
  } catch (e) {
    if (e !== 'cancel') {
      console.error('删除失败:', e)
    }
  }
}

onMounted(() => {
  loadUsers()
})
</script>

<style lang="scss" scoped>
.users-page {
  max-width: 1400px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;

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

.filter-bar {
  display: flex;
  gap: 16px;
  margin-bottom: 24px;

  .search-input {
    width: 280px;
  }

  .el-select {
    width: 140px;
  }
}

.table-wrapper {
  background: white;
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;

  .el-avatar {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  }

  .user-text {
    .username {
      font-weight: 500;
      color: #1f2937;
    }
    .nickname {
      font-size: 12px;
      color: #9ca3af;
    }
  }
}

.contact-info {
  font-size: 13px;
  color: #6b7280;
  line-height: 1.6;

  .no-data {
    color: #d1d5db;
  }
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}

// 响应式
@media (max-width: 768px) {
  .filter-bar {
    flex-wrap: wrap;

    .search-input {
      width: 100%;
    }

    .el-select {
      width: calc(50% - 8px);
    }
  }
}
</style>
