<template>
  <div class="appointments-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-info">
        <h1>预约管理</h1>
        <p>管理宠物的服务预约，跟踪预约状态</p>
      </div>
    </div>

    <!-- 服务商未审核通过提示 -->
    <div v-if="isProvider && !isProviderApproved" class="provider-notice">
      <el-result
        icon="warning"
        title="服务商资质审核中"
        sub-title="您的服务商资质尚未审核通过，审核通过后可查看和管理预约"
      >
        <template #extra>
          <el-button type="primary" @click="$router.push('/service-providers')">
            前往服务商管理
          </el-button>
        </template>
      </el-result>
    </div>

    <!-- 主要内容区域 -->
    <template v-if="canAccessContent">
      <!-- 筛选区域 -->
      <div class="filter-section">
        <div class="filter-row">
          <el-select 
            v-model="selectedPetId" 
            placeholder="请选择宠物" 
            @change="handleFilterChange"
            clearable
            size="large"
            style="width: 200px"
          >
            <el-option
              v-for="pet in petList"
              :key="pet.id"
              :label="pet.name"
              :value="pet.id"
            >
              <span class="pet-option">
                <span class="pet-emoji">{{ getPetEmoji(pet.type) }}</span>
                <span>{{ pet.name }}</span>
                <el-tag size="small" type="info">{{ pet.type }}</el-tag>
              </span>
            </el-option>
          </el-select>

          <el-select 
            v-model="selectedProviderId" 
            placeholder="请选择服务商" 
            @change="handleFilterChange"
            clearable
            size="large"
            style="width: 200px"
          >
            <el-option
              v-for="provider in providerList"
              :key="provider.id"
              :label="provider.name"
              :value="provider.id"
            >
              <span class="provider-option">
                <span class="provider-icon">🏥</span>
                <span>{{ provider.name }}</span>
              </span>
            </el-option>
          </el-select>

          <el-button 
            type="primary" 
            :icon="Plus" 
            @click="openDialog()" 
            :disabled="!selectedPetId || !selectedProviderId"
          >
            新建预约
          </el-button>
        </div>
      </div>

      <!-- 状态标签页 -->
      <el-tabs v-model="activeTab" v-if="selectedPetId && selectedProviderId" class="status-tabs">
        <el-tab-pane label="全部" name="all">
          <template #label>
            <span class="tab-label">📋 全部</span>
          </template>
        </el-tab-pane>
        <el-tab-pane label="待确认" name="0">
          <template #label>
            <span class="tab-label">⏳ 待确认</span>
          </template>
        </el-tab-pane>
        <el-tab-pane label="已预约" name="1">
          <template #label>
            <span class="tab-label">📅 已预约</span>
          </template>
        </el-tab-pane>
        <el-tab-pane label="已完成" name="2">
          <template #label>
            <span class="tab-label">✅ 已完成</span>
          </template>
        </el-tab-pane>
        <el-tab-pane label="已取消" name="3">
          <template #label>
            <span class="tab-label">❌ 已取消</span>
          </template>
        </el-tab-pane>
      </el-tabs>

      <!-- 预约卡片列表 -->
      <div class="appointments-grid" v-loading="loading" v-if="selectedPetId && selectedProviderId">
        <div 
          v-for="appointment in filteredAppointments" 
          :key="appointment.id" 
          class="appointment-card"
          :class="getStatusClass(appointment.status)"
        >
          <div class="card-header">
            <span class="status-icon">{{ getStatusIcon(appointment.status) }}</span>
            <el-tag :type="getStatusTagType(appointment.status)" size="small">
              {{ getStatusName(appointment.status) }}
            </el-tag>
          </div>
          
          <h3 class="service-type">{{ appointment.serviceType || '未指定服务' }}</h3>
          
          <div class="appointment-info">
            <div class="info-item">
              <el-icon><Calendar /></el-icon>
              <span>预约时间: {{ formatDateTime(appointment.appointmentTime) }}</span>
            </div>
            <div class="info-item" v-if="appointment.remark">
              <el-icon><Document /></el-icon>
              <span>备注: {{ appointment.remark }}</span>
            </div>
            <div class="info-item">
              <el-icon><Clock /></el-icon>
              <span>创建时间: {{ formatDateTime(appointment.createTime) }}</span>
            </div>
          </div>

          <div class="card-actions">
            <el-button text type="info" @click="viewDetail(appointment)">
              <el-icon><View /></el-icon>
            </el-button>
            <el-button v-if="canEdit" text type="primary" @click="openDialog(appointment)">
              <el-icon><Edit /></el-icon>
            </el-button>
            <el-button 
              v-if="canEdit && appointment.status === 0" 
              text 
              type="success" 
              @click="confirmAppointment(appointment)"
              title="确认预约"
            >
              <el-icon><Check /></el-icon>
            </el-button>
            <el-button 
              v-if="canEdit && appointment.status === 1" 
              text 
              type="success" 
              @click="completeAppointment(appointment)"
              title="完成预约"
            >
              <el-icon><Finished /></el-icon>
            </el-button>
            <el-button 
              v-if="canEdit && (appointment.status === 0 || appointment.status === 1)" 
              text 
              type="warning" 
              @click="cancelAppointment(appointment)"
              title="取消预约"
            >
              <el-icon><Close /></el-icon>
            </el-button>
            <el-button v-if="canEdit" text type="danger" @click="handleDelete(appointment)">
              <el-icon><Delete /></el-icon>
            </el-button>
          </div>
        </div>

        <!-- 空状态 -->
        <el-empty 
          v-if="!loading && filteredAppointments.length === 0" 
          :description="'暂无预约记录'"
          :image-size="120"
          class="empty-state"
        />
      </div>

      <!-- 未选择筛选条件提示 -->
      <div class="empty-filter-hint" v-if="!selectedPetId || !selectedProviderId">
        <el-empty description="请选择宠物和服务商以查看预约记录" :image-size="160" />
      </div>

      <!-- 分页 -->
      <div class="pagination-wrapper" v-if="total > pageSize && selectedPetId && selectedProviderId">
        <el-pagination
          v-model:current-page="currentPage"
          :page-size="pageSize"
          :total="total"
          layout="prev, pager, next"
          @current-change="loadAppointments"
        />
      </div>
    </template>

    <!-- 新增/编辑弹窗 -->
    <el-dialog 
      v-model="dialogVisible" 
      :title="isEdit ? '编辑预约' : '新建预约'"
      width="520px"
      destroy-on-close
    >
      <el-form 
        ref="formRef" 
        :model="form" 
        :rules="formRules" 
        label-width="100px"
        label-position="left"
      >
        <el-form-item label="服务类型" prop="serviceType">
          <el-select v-model="form.serviceType" placeholder="请选择服务类型" style="width: 100%">
            <el-option label="🏥 常规检查" value="常规检查" />
            <el-option label="💉 疫苗接种" value="疫苗接种" />
            <el-option label="🐛 驱虫服务" value="驱虫服务" />
            <el-option label="✂️ 美容护理" value="美容护理" />
            <el-option label="🦷 口腔护理" value="口腔护理" />
            <el-option label="🩺 手术治疗" value="手术治疗" />
            <el-option label="📋 健康咨询" value="健康咨询" />
            <el-option label="🏠 寄养服务" value="寄养服务" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="预约时间" prop="appointmentTime">
          <el-date-picker 
            v-model="form.appointmentTime" 
            type="datetime" 
            placeholder="选择预约时间"
            style="width: 100%"
            :shortcuts="dateShortcuts"
          />
        </el-form-item>
        
        <!-- 状态不在编辑弹窗中修改，通过卡片上的操作按钮来改变 -->

        <el-form-item label="备注">
          <el-input 
            v-model="form.remark" 
            type="textarea" 
            :rows="3"
            placeholder="请输入备注信息（可选）" 
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">
          {{ isEdit ? '保存' : '创建' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete, Calendar, Clock, Document, View, Check, Close, Finished } from '@element-plus/icons-vue'
import { page as getAppointments, create, update, deleteBatch } from '@/api/appointment'
import { page as getPets, list as getAllPets } from '@/api/pet'
import { page as getProviders, list as getAllProviders } from '@/api/serviceProvider'
import { useUserInfoStore } from '@/stores/userinfo'

const loading = ref(false)
const submitting = ref(false)
const appointments = ref([])
const petList = ref([])
const providerList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(12)
const selectedPetId = ref(null)
const selectedProviderId = ref(null)
const activeTab = ref('all')

// 用户信息
const userInfoStore = useUserInfoStore()
const isAdmin = computed(() => userInfoStore.userInfo?.roleType === 3)
const isProvider = computed(() => userInfoStore.userInfo?.roleType === 2)
const isOwner = computed(() => userInfoStore.userInfo?.roleType === 1)

// 服务商审核状态
const providerStatus = ref(null)
const isProviderApproved = computed(() => providerStatus.value === '已通过')

// 是否可以访问内容
const canAccessContent = computed(() => {
  if (isOwner.value || isAdmin.value) return true
  if (isProvider.value) return isProviderApproved.value
  return false
})

// 是否可以增删改
const canEdit = computed(() => {
  return isProvider.value || isAdmin.value
})

// 弹窗相关
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()
const form = reactive({
  id: null,
  serviceType: '',
  appointmentTime: null,
  status: 0,
  remark: ''
})

const formRules = {
  serviceType: [{ required: true, message: '请选择服务类型', trigger: 'change' }],
  appointmentTime: [{ required: true, message: '请选择预约时间', trigger: 'change' }]
}

const dateShortcuts = [
  { text: '今天', value: new Date() },
  { text: '明天', value: () => { const d = new Date(); d.setDate(d.getDate() + 1); return d } },
  { text: '下周', value: () => { const d = new Date(); d.setDate(d.getDate() + 7); return d } }
]

// 按状态筛选预约
const filteredAppointments = computed(() => {
  if (activeTab.value === 'all') {
    return appointments.value
  }
  return appointments.value.filter(a => a.status === parseInt(activeTab.value))
})

// 工具函数
const getStatusName = (status) => {
  const map = { 0: '待确认', 1: '已预约', 2: '已完成', 3: '已取消' }
  return map[status] || '未知'
}

const getStatusIcon = (status) => {
  const map = { 0: '⏳', 1: '📅', 2: '✅', 3: '❌' }
  return map[status] || '📋'
}

const getStatusClass = (status) => {
  const map = { 0: 'status-pending', 1: 'status-confirmed', 2: 'status-completed', 3: 'status-cancelled' }
  return map[status] || ''
}

const getStatusTagType = (status) => {
  const map = { 0: 'warning', 1: 'primary', 2: 'success', 3: 'info' }
  return map[status] || ''
}

const getPetEmoji = (type) => {
  const map = { '狗': '🐕', '猫': '🐱', '兔子': '🐰', '仓鼠': '🐹' }
  return map[type] || '🐾'
}

const formatDateTime = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 加载宠物列表
const loadPets = async () => {
  try {
    const roleType = userInfoStore.userInfo?.roleType
    if (roleType === 2 || roleType === 3) {
      const res = await getAllPets()
      petList.value = res.data || []
    } else {
      const res = await getPets(1, 100)
      petList.value = res.data?.records || []
    }
    if (petList.value.length > 0) {
      selectedPetId.value = petList.value[0].id
    }
  } catch (e) {
    console.error('加载宠物失败:', e)
  }
}

// 加载服务商列表（只显示审核通过的服务商）
const loadProviders = async () => {
  try {
    const roleType = userInfoStore.userInfo?.roleType
    let allProviders = []
    
    // 服务商(2)只查看自己的服务商信息
    // 宠物主人(1)和管理员(3)查看全部服务商，用于选择预约
    if (roleType === 2) {
      const res = await getProviders(1, 100)
      allProviders = res.data?.records || []
    } else {
      // 宠物主人和管理员获取全部服务商
      const res = await getAllProviders()
      allProviders = res.data || []
    }
    
    // 只保留审核通过的服务商
    providerList.value = allProviders.filter(p => p.status === '已通过')
    
    if (providerList.value.length > 0) {
      selectedProviderId.value = providerList.value[0].id
      // 检查服务商审核状态
      if (isProvider.value && providerList.value.length > 0) {
        providerStatus.value = providerList.value[0].status
      }
    }
  } catch (e) {
    console.error('加载服务商失败:', e)
  }
}

// 加载预约列表
const loadAppointments = async () => {
  if (!selectedPetId.value || !selectedProviderId.value) return
  
  loading.value = true
  try {
    const res = await getAppointments(selectedPetId.value, selectedProviderId.value, currentPage.value, pageSize.value)
    appointments.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (e) {
    console.error('加载预约列表失败:', e)
    ElMessage.error('加载预约列表失败')
  } finally {
    loading.value = false
  }
}

// 筛选条件变化
const handleFilterChange = () => {
  currentPage.value = 1
  activeTab.value = 'all'
  loadAppointments()
}

// 打开弹窗
const openDialog = (appointment = null) => {
  if (appointment) {
    isEdit.value = true
    Object.assign(form, {
      id: appointment.id,
      serviceType: appointment.serviceType,
      appointmentTime: appointment.appointmentTime,
      status: appointment.status,
      remark: appointment.remark
    })
  } else {
    isEdit.value = false
    Object.assign(form, {
      id: null,
      serviceType: '',
      appointmentTime: null,
      status: 0,
      remark: ''
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
      await update(form.id, form)
      ElMessage.success('更新成功')
    } else {
      await create(selectedPetId.value, selectedProviderId.value, form)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadAppointments()
  } catch (e) {
    console.error('操作失败:', e)
    ElMessage.error('操作失败')
  } finally {
    submitting.value = false
  }
}

// 确认预约
const confirmAppointment = async (appointment) => {
  try {
    await ElMessageBox.confirm('确定要确认这个预约吗？', '确认预约', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'info'
    })
    await update(appointment.id, { ...appointment, status: 1 })
    ElMessage.success('预约已确认')
    loadAppointments()
  } catch (e) {
    if (e !== 'cancel') {
      console.error('确认预约失败:', e)
    }
  }
}

// 完成预约
const completeAppointment = async (appointment) => {
  try {
    await ElMessageBox.confirm('确定要将这个预约标记为已完成吗？', '完成预约', {
      confirmButtonText: '确认完成',
      cancelButtonText: '取消',
      type: 'success'
    })
    await update(appointment.id, { ...appointment, status: 2 })
    ElMessage.success('预约已完成')
    loadAppointments()
  } catch (e) {
    if (e !== 'cancel') {
      console.error('完成预约失败:', e)
    }
  }
}

// 取消预约
const cancelAppointment = async (appointment) => {
  try {
    await ElMessageBox.confirm('确定要取消这个预约吗？', '取消预约', {
      confirmButtonText: '确认取消',
      cancelButtonText: '返回',
      type: 'warning'
    })
    await update(appointment.id, { ...appointment, status: 3 })
    ElMessage.success('预约已取消')
    loadAppointments()
  } catch (e) {
    if (e !== 'cancel') {
      console.error('取消预约失败:', e)
    }
  }
}

// 查看详情
const viewDetail = (appointment) => {
  const statusNames = { 0: '待确认', 1: '已预约', 2: '已完成', 3: '已取消' }
  const content = `
    <div style="line-height: 2;">
      <p><strong>服务类型：</strong>${appointment.serviceType || '未指定'}</p>
      <p><strong>预约时间：</strong>${formatDateTime(appointment.appointmentTime)}</p>
      <p><strong>状态：</strong>${statusNames[appointment.status] || '未知'}</p>
      <p><strong>备注：</strong>${appointment.remark || '无'}</p>
      <p><strong>创建时间：</strong>${formatDateTime(appointment.createTime)}</p>
    </div>
  `
  ElMessageBox.alert(content, '预约详情', {
    dangerouslyUseHTMLString: true,
    confirmButtonText: '关闭'
  })
}

// 删除预约
const handleDelete = async (appointment) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除这个预约吗？此操作不可恢复。`,
      '确认删除',
      { confirmButtonText: '删除', cancelButtonText: '取消', type: 'warning' }
    )
    await deleteBatch([appointment.id])
    ElMessage.success('删除成功')
    loadAppointments()
  } catch (e) {
    if (e !== 'cancel') {
      console.error('删除失败:', e)
    }
  }
}

onMounted(async () => {
  await loadPets()
  await loadProviders()
  if (selectedPetId.value && selectedProviderId.value) {
    loadAppointments()
  }
})
</script>

<style lang="scss" scoped>
.appointments-page {
  max-width: 1400px;
  margin: 0 auto;
}

.provider-notice {
  background: white;
  border-radius: 16px;
  padding: 40px;
  margin-top: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  text-align: center;
}

.page-header {
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

.filter-section {
  padding: 16px 20px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  margin-bottom: 20px;
}

.filter-row {
  display: flex;
  gap: 16px;
  align-items: center;
  flex-wrap: wrap;
}

.pet-option, .provider-option {
  display: flex;
  align-items: center;
  gap: 8px;

  .pet-emoji, .provider-icon {
    font-size: 18px;
  }
}

.status-tabs {
  margin-bottom: 20px;
  
  :deep(.el-tabs__header) {
    margin-bottom: 0;
  }

  .tab-label {
    font-size: 14px;
  }
}

// 预约卡片网格
.appointments-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  min-height: 200px;
  box-sizing: border-box;
}

.appointment-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  padding-right: 50px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  transition: all 0.3s ease;
  position: relative;
  border-left: 4px solid #e5e7eb;
  min-height: 200px;  // 最小高度确保能容纳5个竖直排列的按钮
  box-sizing: border-box;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);

    .card-actions {
      opacity: 1;
    }
  }

  // 状态颜色
  &.status-pending {
    border-left-color: #f59e0b;
  }
  &.status-confirmed {
    border-left-color: #3b82f6;
  }
  &.status-completed {
    border-left-color: #10b981;
  }
  &.status-cancelled {
    border-left-color: #9ca3af;
  }
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;

  .status-icon {
    font-size: 20px;
  }
}

.service-type {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 12px 0;
}

.appointment-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
  font-size: 13px;
  color: #6b7280;

  .info-item {
    display: flex;
    align-items: center;
    gap: 6px;

    .el-icon {
      color: #9ca3af;
    }
  }
}

.card-actions {
  position: absolute;
  top: 12px;
  right: 8px;
  opacity: 0;
  transition: opacity 0.2s;
  display: flex;
  flex-direction: column;
  align-items: flex-end;
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

.empty-filter-hint {
  padding: 80px 0;
  text-align: center;
  background: white;
  border-radius: 12px;
  margin-top: 20px;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 32px;
}

// 响应式
@media (max-width: 1200px) {
  .appointments-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .appointments-grid {
    grid-template-columns: 1fr;
  }
  
  .filter-row {
    flex-direction: column;
    align-items: stretch;
    
    .el-select {
      width: 100% !important;
    }
  }
}
</style>
