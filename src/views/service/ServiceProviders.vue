<template>
  <div class="service-providers-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-info">
        <h1>🏥 服务商管理</h1>
        <p>管理宠物医院、美容店等服务商信息</p>
      </div>
      <el-button type="primary" :icon="Plus" @click="openDialog()">
        入驻申请
      </el-button>
    </div>

    <!-- 搜索和筛选 -->
    <div class="filter-bar">
      <el-input 
        v-model="searchKeyword" 
        placeholder="搜索服务商名称..." 
        :prefix-icon="Search"
        clearable
        class="search-input"
      />
      <el-select v-model="filterType" placeholder="服务类型" clearable>
        <el-option label="全部" value="" />
        <el-option label="医院" value="医院" />
        <el-option label="美容店" value="美容店" />
        <el-option label="寄养" value="寄养" />
        <el-option label="训练" value="训练" />
      </el-select>
      <el-select v-model="filterStatus" placeholder="状态" clearable>
        <el-option label="全部" value="" />
        <el-option label="待审核" value="待审核" />
        <el-option label="已通过" value="已通过" />
        <el-option label="已拒绝" value="已拒绝" />
      </el-select>
    </div>

    <!-- 服务商卡片网格 -->
    <div class="providers-grid" v-loading="loading">
      <div 
        v-for="provider in filteredProviders" 
        :key="provider.id" 
        class="provider-card"
      >
        <div class="provider-header">
          <div class="provider-icon">
            {{ getTypeEmoji(provider.type) }}
          </div>
          <el-tag 
            :type="getStatusType(provider.status)" 
            size="small"
          >
            {{ provider.status }}
          </el-tag>
        </div>
        
        <div class="provider-info">
          <h3 class="provider-name">{{ provider.name }}</h3>
          <p class="provider-type">{{ provider.type }}</p>
          
          <div class="provider-details">
            <div class="detail-item" v-if="provider.address">
              <el-icon><Location /></el-icon>
              <span>{{ provider.address }}</span>
            </div>
            <div class="detail-item" v-if="provider.phone">
              <el-icon><Phone /></el-icon>
              <span>{{ provider.phone }}</span>
            </div>
            <div class="detail-item" v-if="provider.license">
              <el-icon><Document /></el-icon>
              <span>{{ provider.license }}</span>
            </div>
          </div>
        </div>

        <div class="provider-actions">
          <!-- 管理员审核按钮 -->
          <el-button v-if="isAdmin" text type="warning" @click="openReviewDialog(provider)" title="审核">
            <el-icon><Checked /></el-icon>
          </el-button>
          <el-button text type="primary" @click="openDialog(provider)">
            <el-icon><Edit /></el-icon>
          </el-button>
          <el-button text type="danger" @click="handleDelete(provider)">
            <el-icon><Delete /></el-icon>
          </el-button>
        </div>
      </div>

      <!-- 空状态 -->
      <el-empty 
        v-if="!loading && filteredProviders.length === 0" 
        description="暂无服务商，点击右上角入驻申请~"
        :image-size="120"
        class="empty-state"
      />
    </div>

    <!-- 分页 -->
    <div class="pagination-wrapper" v-if="total > pageSize">
      <el-pagination
        v-model:current-page="currentPage"
        :page-size="pageSize"
        :total="total"
        layout="prev, pager, next"
        @current-change="loadProviders"
      />
    </div>

    <!-- 新增/编辑弹窗 -->
    <el-dialog 
      v-model="dialogVisible" 
      :title="isEdit ? '编辑服务商' : '入驻申请'"
      width="550px"
      destroy-on-close
    >
      <el-form 
        ref="formRef" 
        :model="form" 
        :rules="formRules" 
        label-width="100px"
        label-position="left"
      >
        <el-form-item label="服务商名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入服务商名称" />
        </el-form-item>
        
        <el-form-item label="服务类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择服务类型" style="width: 100%">
            <el-option label="医院" value="医院" />
            <el-option label="美容店" value="美容店" />
            <el-option label="寄养" value="寄养" />
            <el-option label="训练" value="训练" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="地址" prop="address">
          <el-input v-model="form.address" placeholder="请输入详细地址" />
        </el-form-item>
        
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入联系电话" />
        </el-form-item>
        
        <el-form-item label="资质证书" prop="license">
          <el-input 
            v-model="form.license" 
            type="textarea" 
            :rows="2"
            placeholder="请输入资质证书编号或描述" 
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">
          {{ isEdit ? '保存' : '提交申请' }}
        </el-button>
      </template>
    </el-dialog>

    <!-- 审核弹窗（仅管理员） -->
    <el-dialog 
      v-model="reviewDialogVisible" 
      title="📋 服务商审核"
      width="650px"
      destroy-on-close
      class="review-dialog"
    >
      <div class="review-content" v-if="reviewProvider">
        <!-- 服务商信息卡片 -->
        <div class="review-card">
          <div class="review-header">
            <div class="review-icon">{{ getTypeEmoji(reviewProvider.type) }}</div>
            <div class="review-title">
              <h2>{{ reviewProvider.name }}</h2>
              <el-tag :type="getStatusType(reviewProvider.status)" size="large">
                {{ reviewProvider.status }}
              </el-tag>
            </div>
          </div>
          
          <el-divider />
          
          <div class="review-info-grid">
            <div class="info-item">
              <label>服务类型</label>
              <span>{{ reviewProvider.type }}</span>
            </div>
            <div class="info-item">
              <label>联系电话</label>
              <span>{{ reviewProvider.phone || '未填写' }}</span>
            </div>
            <div class="info-item full-width">
              <label>详细地址</label>
              <span>{{ reviewProvider.address || '未填写' }}</span>
            </div>
            <div class="info-item full-width">
              <label>资质证书</label>
              <span>{{ reviewProvider.license || '未上传' }}</span>
            </div>
            <div class="info-item">
              <label>申请时间</label>
              <span>{{ formatDate(reviewProvider.createTime) }}</span>
            </div>
          </div>
        </div>

        <!-- 审核操作区 -->
        <div class="review-action-section">
          <h3>✍️ 审核操作</h3>
          <div class="status-options">
            <div 
              class="status-option" 
              :class="{ active: newStatus === '待审核', pending: true }"
              @click="newStatus = '待审核'"
            >
              <el-icon><Clock /></el-icon>
              <span>待审核</span>
            </div>
            <div 
              class="status-option" 
              :class="{ active: newStatus === '已通过', approved: true }"
              @click="newStatus = '已通过'"
            >
              <el-icon><CircleCheckFilled /></el-icon>
              <span>通过</span>
            </div>
            <div 
              class="status-option" 
              :class="{ active: newStatus === '已拒绝', rejected: true }"
              @click="newStatus = '已拒绝'"
            >
              <el-icon><CircleCloseFilled /></el-icon>
              <span>拒绝</span>
            </div>
          </div>
        </div>
      </div>

      <template #footer>
        <el-button @click="reviewDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="reviewSubmitting" @click="handleReviewSubmit">
          确认审核
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Edit, Delete, Location, Phone, Document, Checked, Clock, CircleCheckFilled, CircleCloseFilled } from '@element-plus/icons-vue'
import { page as getProviders, list as getAllProviders, create, update, deleteBatch } from '@/api/serviceProvider'
import { useUserInfoStore } from '@/stores/userinfo'

const loading = ref(false)
const submitting = ref(false)
const providers = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(12)
const searchKeyword = ref('')
const filterType = ref('')
const filterStatus = ref('')

// 用户信息和角色判断
const userInfoStore = useUserInfoStore()
const isAdmin = computed(() => userInfoStore.userInfo?.roleType === 3)

// 弹窗相关
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()
const form = reactive({
  id: null,
  name: '',
  type: '',
  address: '',
  phone: '',
  license: ''
})

// 审核弹窗相关
const reviewDialogVisible = ref(false)
const reviewSubmitting = ref(false)
const reviewProvider = ref(null)
const newStatus = ref('')

const formRules = {
  name: [{ required: true, message: '请输入服务商名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择服务类型', trigger: 'change' }],
  phone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }],
  address: [{ required: true, message: '请输入详细地址', trigger: 'blur' }]
}

// 筛选后的服务商列表
const filteredProviders = computed(() => {
  let result = providers.value
  if (searchKeyword.value) {
    result = result.filter(p => 
      p.name?.toLowerCase().includes(searchKeyword.value.toLowerCase())
    )
  }
  if (filterType.value) {
    result = result.filter(p => p.type === filterType.value)
  }
  if (filterStatus.value) {
    result = result.filter(p => p.status === filterStatus.value)
  }
  return result
})

// 工具函数
const getTypeEmoji = (type) => {
  const map = { '医院': '🏥', '美容店': '✂️', '寄养': '🏠', '训练': '🎓' }
  return map[type] || '🏪'
}

const getStatusType = (status) => {
  const map = { '待审核': 'warning', '已通过': 'success', '已拒绝': 'danger' }
  return map[status] || 'info'
}

const formatDate = (date) => {
  if (!date) return '未知'
  return new Date(date).toLocaleString('zh-CN')
}

// 加载服务商列表
const loadProviders = async () => {
  loading.value = true
  try {
    if (isAdmin.value) {
      // 管理员：获取全部服务商
      const res = await getAllProviders()
      providers.value = res.data || []
      total.value = providers.value.length
    } else {
      // 服务商：分页获取自己的申请
      const res = await getProviders(currentPage.value, pageSize.value)
      providers.value = res.data?.records || []
      total.value = res.data?.total || 0
    }
  } catch (e) {
    console.error('加载失败:', e)
  } finally {
    loading.value = false
  }
}

// 打开弹窗
const openDialog = (provider = null) => {
  if (provider) {
    isEdit.value = true
    Object.assign(form, {
      id: provider.id,
      name: provider.name,
      type: provider.type,
      address: provider.address,
      phone: provider.phone,
      license: provider.license
    })
  } else {
    isEdit.value = false
    Object.assign(form, {
      id: null,
      name: '',
      type: '',
      address: '',
      phone: '',
      license: ''
    })
  }
  dialogVisible.value = true
}

// 打开审核弹窗
const openReviewDialog = (provider) => {
  reviewProvider.value = provider
  newStatus.value = provider.status
  reviewDialogVisible.value = true
}

// 提交审核
const handleReviewSubmit = async () => {
  if (!newStatus.value) {
    ElMessage.warning('请选择审核状态')
    return
  }

  reviewSubmitting.value = true
  try {
    await update({
      id: reviewProvider.value.id,
      status: newStatus.value
    })
    ElMessage.success('审核完成')
    reviewDialogVisible.value = false
    loadProviders()
  } catch (e) {
    console.error('审核失败:', e)
  } finally {
    reviewSubmitting.value = false
  }
}

// 提交表单
const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    if (isEdit.value) {
      await update(form)
      ElMessage.success('更新成功')
    } else {
      await create(form)
      ElMessage.success('提交成功，请等待审核')
    }
    dialogVisible.value = false
    loadProviders()
  } catch (e) {
    console.error('操作失败:', e)
  } finally {
    submitting.value = false
  }
}

// 删除服务商
const handleDelete = async (provider) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除服务商 "${provider.name}" 吗？此操作不可恢复。`,
      '确认删除',
      { confirmButtonText: '删除', cancelButtonText: '取消', type: 'warning' }
    )
    await deleteBatch([provider.id])
    ElMessage.success('删除成功')
    loadProviders()
  } catch (e) {
    if (e !== 'cancel') {
      console.error('删除失败:', e)
    }
  }
}

onMounted(() => {
  loadProviders()
})
</script>

<style lang="scss" scoped>
.service-providers-page {
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

// 服务商卡片网格
.providers-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 20px;
  min-height: 200px;
}

.provider-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  transition: all 0.3s ease;
  position: relative;

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 12px 32px rgba(0, 0, 0, 0.1);

    .provider-actions {
      opacity: 1;
    }
  }
}

.provider-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;

  .provider-icon {
    width: 56px;
    height: 56px;
    border-radius: 14px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 28px;
  }
}

.provider-info {
  .provider-name {
    font-size: 18px;
    font-weight: 600;
    color: #1f2937;
    margin: 0 0 4px 0;
  }

  .provider-type {
    font-size: 14px;
    color: #6b7280;
    margin: 0 0 16px 0;
  }
}

.provider-details {
  display: flex;
  flex-direction: column;
  gap: 8px;
  font-size: 13px;
  color: #6b7280;

  .detail-item {
    display: flex;
    align-items: center;
    gap: 8px;

    .el-icon {
      color: #9ca3af;
      flex-shrink: 0;
    }

    span {
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
  }
}

.provider-actions {
  position: absolute;
  top: 80px;
  right: 16px;
  opacity: 0;
  transition: opacity 0.2s;
  display: flex;
  flex-direction: column;
  gap: 4px;

  .el-button {
    margin: 0 !important;
    padding: 4px !important;
  }
}

.empty-state {
  grid-column: 1 / -1;
  padding: 60px 0;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 32px;
}

// 响应式
@media (max-width: 600px) {
  .filter-bar {
    flex-direction: column;

    .search-input,
    .el-select {
      width: 100%;
    }
  }

  .providers-grid {
    grid-template-columns: 1fr;
  }
}

// 审核弹窗样式
.review-content {
  .review-card {
    background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
    border-radius: 16px;
    padding: 24px;
    margin-bottom: 20px;
  }

  .review-header {
    display: flex;
    align-items: center;
    gap: 16px;

    .review-icon {
      width: 64px;
      height: 64px;
      border-radius: 16px;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 32px;
    }

    .review-title {
      flex: 1;
      display: flex;
      align-items: center;
      gap: 12px;

      h2 {
        font-size: 22px;
        font-weight: 600;
        color: #1f2937;
        margin: 0;
      }
    }
  }

  .review-info-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 16px;

    .info-item {
      display: flex;
      flex-direction: column;
      gap: 4px;

      &.full-width {
        grid-column: 1 / -1;
      }

      label {
        font-size: 12px;
        color: #9ca3af;
        font-weight: 500;
        text-transform: uppercase;
        letter-spacing: 0.5px;
      }

      span {
        font-size: 14px;
        color: #374151;
        font-weight: 500;
      }
    }
  }

  .review-action-section {
    h3 {
      font-size: 16px;
      font-weight: 600;
      color: #1f2937;
      margin: 0 0 16px 0;
    }
  }

  .status-options {
    display: flex;
    gap: 12px;

    .status-option {
      flex: 1;
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 8px;
      padding: 20px 16px;
      border: 2px solid #e5e7eb;
      border-radius: 12px;
      cursor: pointer;
      transition: all 0.2s ease;
      background: white;

      .el-icon {
        font-size: 28px;
        color: #9ca3af;
        transition: color 0.2s;
      }

      span {
        font-size: 14px;
        font-weight: 500;
        color: #6b7280;
        transition: color 0.2s;
      }

      &:hover {
        border-color: #93c5fd;
        background: #f0f9ff;
      }

      &.pending .el-icon { color: #f59e0b; }
      &.approved .el-icon { color: #10b981; }
      &.rejected .el-icon { color: #ef4444; }

      &.active {
        &.pending {
          border-color: #f59e0b;
          background: linear-gradient(135deg, #fef3c7 0%, #fde68a 100%);
          span { color: #92400e; }
        }
        &.approved {
          border-color: #10b981;
          background: linear-gradient(135deg, #d1fae5 0%, #a7f3d0 100%);
          span { color: #065f46; }
        }
        &.rejected {
          border-color: #ef4444;
          background: linear-gradient(135deg, #fee2e2 0%, #fecaca 100%);
          span { color: #991b1b; }
        }
      }
    }
  }
}
</style>
