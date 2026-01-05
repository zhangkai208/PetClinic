<template>
  <div class="health-records-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-info">
        <h1>健康记录</h1>
        <p>管理宠物的疫苗、驱虫、用药和健康笔记</p>
      </div>
    </div>

    <!-- 宠物选择器 -->
    <div class="pet-selector">
      <el-select 
        v-model="selectedPetId" 
        placeholder="请选择宠物" 
        @change="handlePetChange"
        clearable
        size="large"
        style="width: 280px"
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
      
      <el-button 
        type="primary" 
        :icon="Plus" 
        @click="openDialog()" 
        :disabled="!selectedPetId"
      >
        添加记录
      </el-button>
    </div>

    <!-- 记录类型标签页 -->
    <el-tabs v-model="activeTab" v-if="selectedPetId" class="record-tabs">
      <el-tab-pane label="全部" name="all">
        <template #label>
          <span class="tab-label">📋 全部</span>
        </template>
      </el-tab-pane>
      <el-tab-pane label="疫苗" name="1">
        <template #label>
          <span class="tab-label">💉 疫苗</span>
        </template>
      </el-tab-pane>
      <el-tab-pane label="驱虫" name="2">
        <template #label>
          <span class="tab-label">🐛 驱虫</span>
        </template>
      </el-tab-pane>
      <el-tab-pane label="用药" name="3">
        <template #label>
          <span class="tab-label">💊 用药</span>
        </template>
      </el-tab-pane>
      <el-tab-pane label="健康笔记" name="4">
        <template #label>
          <span class="tab-label">📝 健康笔记</span>
        </template>
      </el-tab-pane>
    </el-tabs>

    <!-- 健康记录列表 -->
    <div class="records-grid" v-loading="loading" v-if="selectedPetId">
      <div 
        v-for="record in filteredRecords" 
        :key="record.id" 
        class="record-card"
        :class="getRecordTypeClass(record.recordType)"
      >
        <div class="record-header">
          <span class="record-icon">{{ getRecordIcon(record.recordType) }}</span>
          <span class="record-type">{{ getRecordTypeName(record.recordType) }}</span>
          <el-tag v-if="isUpcoming(record.nextDate)" type="warning" size="small">
            即将到期
          </el-tag>
        </div>
        
        <h3 class="record-title">{{ record.title }}</h3>
        <p class="record-content">{{ record.content }}</p>
        
        <div class="record-dates">
          <div class="date-item">
            <el-icon><Calendar /></el-icon>
            <span>记录日期: {{ formatDate(record.recordDate) }}</span>
          </div>
          <div class="date-item" v-if="record.nextDate">
            <el-icon><AlarmClock /></el-icon>
            <span>下次提醒: {{ formatDate(record.nextDate) }}</span>
          </div>
        </div>

        <div class="record-actions">
          <el-button text type="primary" @click="openDialog(record)">
            <el-icon><Edit /></el-icon>
          </el-button>
          <el-button text type="danger" @click="handleDelete(record)">
            <el-icon><Delete /></el-icon>
          </el-button>
        </div>
      </div>

      <!-- 空状态 -->
      <el-empty 
        v-if="!loading && filteredRecords.length === 0" 
        :description="selectedPetId ? '暂无健康记录，点击上方添加' : '请先选择一个宠物'"
        :image-size="120"
        class="empty-state"
      />
    </div>

    <!-- 未选择宠物提示 -->
    <div class="empty-pet-hint" v-if="!selectedPetId">
      <el-empty description="请在上方选择一个宠物，查看或添加健康记录" :image-size="160" />
    </div>

    <!-- 分页 -->
    <div class="pagination-wrapper" v-if="total > pageSize && selectedPetId">
      <el-pagination
        v-model:current-page="currentPage"
        :page-size="pageSize"
        :total="total"
        layout="prev, pager, next"
        @current-change="loadRecords"
      />
    </div>

    <!-- 新增/编辑弹窗 -->
    <el-dialog 
      v-model="dialogVisible" 
      :title="isEdit ? '编辑健康记录' : '添加健康记录'"
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
        <el-form-item label="记录类型" prop="recordType">
          <el-radio-group v-model="form.recordType">
            <el-radio :value="1">💉 疫苗</el-radio>
            <el-radio :value="2">🐛 驱虫</el-radio>
            <el-radio :value="3">💊 用药</el-radio>
            <el-radio :value="4">📝 健康笔记</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入标题" />
        </el-form-item>
        
        <el-form-item label="内容" prop="content">
          <el-input 
            v-model="form.content" 
            type="textarea" 
            :rows="4"
            placeholder="请输入详细内容..." 
          />
        </el-form-item>
        
        <el-form-item label="记录日期" prop="recordDate">
          <el-date-picker 
            v-model="form.recordDate" 
            type="date" 
            placeholder="选择记录日期"
            style="width: 100%"
          />
        </el-form-item>
        
        <el-form-item 
          label="下次提醒" 
          v-if="form.recordType === 1 || form.recordType === 2"
        >
          <el-date-picker 
            v-model="form.nextDate" 
            type="date" 
            placeholder="选择下次提醒日期（可选）"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">
          {{ isEdit ? '保存' : '添加' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete, Calendar, AlarmClock } from '@element-plus/icons-vue'
import { page as getRecords, create, update, deleteBatch } from '@/api/healthRecord'
import { page as getPets, list as getAllPets } from '@/api/pet'
import { useUserInfoStore } from '@/stores/userinfo'

const loading = ref(false)
const submitting = ref(false)
const records = ref([])
const petList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(12)
const selectedPetId = ref(null)
const activeTab = ref('all')

// 用户信息
const userInfoStore = useUserInfoStore()
const isAdmin = computed(() => userInfoStore.userInfo?.roleType === 3)

// 弹窗相关
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()
const form = reactive({
  id: null,
  recordType: 1,
  title: '',
  content: '',
  recordDate: new Date(),
  nextDate: null
})

const formRules = {
  recordType: [{ required: true, message: '请选择记录类型', trigger: 'change' }],
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }],
  recordDate: [{ required: true, message: '请选择记录日期', trigger: 'change' }]
}

// 按类型筛选记录
const filteredRecords = computed(() => {
  if (activeTab.value === 'all') {
    return records.value
  }
  return records.value.filter(r => r.recordType === parseInt(activeTab.value))
})

// 工具函数
const getRecordTypeName = (type) => {
  const map = { 1: '疫苗接种', 2: '驱虫记录', 3: '用药记录', 4: '健康笔记' }
  return map[type] || '其他'
}

const getRecordIcon = (type) => {
  const map = { 1: '💉', 2: '🐛', 3: '💊', 4: '📝' }
  return map[type] || '📋'
}

const getRecordTypeClass = (type) => {
  const map = { 1: 'type-vaccine', 2: 'type-deworm', 3: 'type-medicine', 4: 'type-note' }
  return map[type] || ''
}

const getPetEmoji = (type) => {
  const map = { '狗': '🐕', '猫': '🐱', '兔子': '🐰', '仓鼠': '🐹' }
  return map[type] || '🐾'
}

const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleDateString('zh-CN')
}

const isUpcoming = (nextDate) => {
  if (!nextDate) return false
  const now = new Date()
  const next = new Date(nextDate)
  const diffDays = (next - now) / (1000 * 60 * 60 * 24)
  return diffDays >= 0 && diffDays <= 7
}

// 加载宠物列表
const loadPets = async () => {
  try {
    if (isAdmin.value) {
      const res = await getAllPets()
      petList.value = res.data || []
    } else {
      const res = await getPets(1, 100)
      petList.value = res.data?.records || []
    }
    // 如果有宠物，默认选择第一个
    if (petList.value.length > 0) {
      selectedPetId.value = petList.value[0].id
      loadRecords()
    }
  } catch (e) {
    console.error('加载宠物失败:', e)
  }
}

// 加载健康记录
const loadRecords = async () => {
  if (!selectedPetId.value) return
  
  loading.value = true
  try {
    const res = await getRecords(selectedPetId.value, currentPage.value, pageSize.value)
    records.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (e) {
    console.error('加载健康记录失败:', e)
    ElMessage.error('加载健康记录失败')
  } finally {
    loading.value = false
  }
}

// 切换宠物
const handlePetChange = () => {
  currentPage.value = 1
  activeTab.value = 'all'
  loadRecords()
}

// 打开弹窗
const openDialog = (record = null) => {
  if (record) {
    isEdit.value = true
    Object.assign(form, {
      id: record.id,
      recordType: record.recordType,
      title: record.title,
      content: record.content,
      recordDate: record.recordDate,
      nextDate: record.nextDate
    })
  } else {
    isEdit.value = false
    Object.assign(form, {
      id: null,
      recordType: 1,
      title: '',
      content: '',
      recordDate: new Date(),
      nextDate: null
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
      await create(selectedPetId.value, form)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    loadRecords()
  } catch (e) {
    console.error('操作失败:', e)
    ElMessage.error('操作失败')
  } finally {
    submitting.value = false
  }
}

// 删除记录
const handleDelete = async (record) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除 "${record.title}" 吗？此操作不可恢复。`,
      '确认删除',
      { confirmButtonText: '删除', cancelButtonText: '取消', type: 'warning' }
    )
    await deleteBatch([record.id])
    ElMessage.success('删除成功')
    loadRecords()
  } catch (e) {
    if (e !== 'cancel') {
      console.error('删除失败:', e)
    }
  }
}

onMounted(() => {
  loadPets()
})
</script>

<style lang="scss" scoped>
.health-records-page {
  max-width: 1400px;
  margin: 0 auto;
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

.pet-selector {
  display: flex;
  gap: 16px;
  align-items: center;
  margin-bottom: 20px;
  padding: 16px 20px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.pet-option {
  display: flex;
  align-items: center;
  gap: 8px;

  .pet-emoji {
    font-size: 18px;
  }
}

.record-tabs {
  margin-bottom: 20px;
  
  :deep(.el-tabs__header) {
    margin-bottom: 0;
  }

  .tab-label {
    font-size: 14px;
  }
}

// 记录卡片网格
.records-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 20px;
  min-height: 200px;
}

.record-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  transition: all 0.3s ease;
  position: relative;
  border-left: 4px solid #e5e7eb;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);

    .record-actions {
      opacity: 1;
    }
  }

  // 类型颜色
  &.type-vaccine {
    border-left-color: #10b981;
  }
  &.type-deworm {
    border-left-color: #f59e0b;
  }
  &.type-medicine {
    border-left-color: #3b82f6;
  }
  &.type-note {
    border-left-color: #8b5cf6;
  }
}

.record-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;

  .record-icon {
    font-size: 20px;
  }

  .record-type {
    font-size: 12px;
    color: #6b7280;
    font-weight: 500;
  }
}

.record-title {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 8px 0;
}

.record-content {
  font-size: 14px;
  color: #6b7280;
  margin: 0 0 16px 0;
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.record-dates {
  display: flex;
  flex-direction: column;
  gap: 6px;
  font-size: 13px;
  color: #9ca3af;

  .date-item {
    display: flex;
    align-items: center;
    gap: 6px;
  }
}

.record-actions {
  position: absolute;
  top: 12px;
  right: 12px;
  opacity: 0;
  transition: opacity 0.2s;
  display: flex;
  gap: 4px;
}

.empty-state {
  grid-column: 1 / -1;
  padding: 60px 0;
}

.empty-pet-hint {
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
@media (max-width: 600px) {
  .pet-selector {
    flex-direction: column;
    align-items: stretch;
    
    .el-select {
      width: 100% !important;
    }
  }

  .records-grid {
    grid-template-columns: 1fr;
  }
}
</style>
