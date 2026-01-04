<template>
  <div class="pets-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-info">
        <h1>宠物宝贝</h1>
        <p>管理您的宠物信息</p>
      </div>
      <el-button type="primary" :icon="Plus" @click="openDialog()">
        添加宠物
      </el-button>
    </div>

    <!-- 搜索和筛选 -->
    <div class="filter-bar">
      <el-input 
        v-model="searchKeyword" 
        placeholder="搜索宠物名称..." 
        :prefix-icon="Search"
        clearable
        class="search-input"
      />
      <el-select v-model="filterType" placeholder="宠物类型" clearable>
        <el-option label="全部" value="" />
        <el-option label="狗" value="狗" />
        <el-option label="猫" value="猫" />
        <el-option label="兔子" value="兔子" />
        <el-option label="仓鼠" value="仓鼠" />
        <el-option label="其他" value="其他" />
      </el-select>
      <el-select v-model="filterGender" placeholder="性别" clearable @change="handleGenderFilter">
        <el-option label="全部" value="" />
        <el-option label="公" value="MALE" />
        <el-option label="母" value="FEMALE" />
        <el-option label="未知" value="UNKNOWN" />
      </el-select>
    </div>

    <!-- 宠物卡片网格 -->
    <div class="pets-grid" v-loading="loading">
      <div 
        v-for="pet in filteredPets" 
        :key="pet.id" 
        class="pet-card"
      >
        <div class="pet-avatar">
          <el-avatar :size="80" :src="pet.avatar">
            {{ getPetEmoji(pet.type) }}
          </el-avatar>
          <el-tag 
            :type="getGenderType(pet.gender)" 
            size="small" 
            class="gender-tag"
          >
            {{ getGenderText(pet.gender) }}
          </el-tag>
        </div>
        
        <div class="pet-info">
          <h3 class="pet-name">{{ pet.name }}</h3>
          <p class="pet-breed">{{ pet.type }} · {{ pet.breed || '未知品种' }}</p>
          <p class="pet-owner" v-if="isAdmin && pet.ownerNickname">👤 {{ pet.ownerNickname }}</p>
          
          <div class="pet-details">
            <div class="detail-item" v-if="pet.birthDate">
              <el-icon><Calendar /></el-icon>
              <span>{{ formatDate(pet.birthDate) }}</span>
            </div>
            <div class="detail-item" v-if="pet.weight">
              <span class="weight-icon">⚖️</span>
              <span>{{ pet.weight }} kg</span>
            </div>
          </div>
        </div>

        <div class="pet-actions">
          <el-upload
            :show-file-list="false"
            :http-request="(options) => handleUploadPhotos(options, pet)"
            accept="image/*"
            multiple
          >
            <el-button text type="success" title="添加照片">
              <el-icon><Camera /></el-icon>
            </el-button>
          </el-upload>
          <el-button text type="primary" @click="openDialog(pet)">
            <el-icon><Edit /></el-icon>
          </el-button>
          <el-button text type="danger" @click="handleDelete(pet)">
            <el-icon><Delete /></el-icon>
          </el-button>
        </div>
      </div>

      <!-- 空状态 -->
      <el-empty 
        v-if="!loading && filteredPets.length === 0" 
        description="暂无宠物，点击右上角添加您的爱宠吧~"
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
        @current-change="loadPets"
      />
    </div>

    <!-- 新增/编辑弹窗 -->
    <el-dialog 
      v-model="dialogVisible" 
      :title="isEdit ? '编辑宠物' : '添加宠物'"
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
        <el-form-item label="头像">
          <div class="avatar-upload">
            <el-avatar :size="80" :src="petAvatarDisplay()">
              {{ getPetEmoji(form.type) }}
            </el-avatar>
            <el-upload
              :show-file-list="false"
              :before-upload="handlePetAvatarUpload"
              accept="image/*"
            >
              <el-button type="primary" text size="small">更换头像</el-button>
            </el-upload>
          </div>
        </el-form-item>
        
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入宠物名称" />
        </el-form-item>
        
        <el-form-item label="类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择宠物类型" style="width: 100%">
            <el-option label="狗" value="狗" />
            <el-option label="猫" value="猫" />
            <el-option label="兔子" value="兔子" />
            <el-option label="仓鼠" value="仓鼠" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="品种" prop="breed">
          <el-input v-model="form.breed" placeholder="请输入品种（选填）" />
        </el-form-item>
        
        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="form.gender">
            <el-radio :value="1">公</el-radio>
            <el-radio :value="2">母</el-radio>
            <el-radio :value="0">未知</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item label="生日" prop="birthDate">
          <el-date-picker 
            v-model="form.birthDate" 
            type="date" 
            placeholder="选择生日"
            style="width: 100%"
          />
        </el-form-item>
        
        <el-form-item label="体重" prop="weight">
          <el-input-number 
            v-model="form.weight" 
            :precision="1" 
            :min="0" 
            :max="200"
            placeholder="体重(kg)"
          />
          <span style="margin-left: 8px; color: #999;">kg</span>
        </el-form-item>

        <el-form-item label="过敏史">
          <el-input 
            v-model="form.allergy" 
            type="textarea" 
            :rows="2"
            placeholder="请输入过敏史（选填）" 
          />
        </el-form-item>

        <el-form-item label="病史">
          <el-input 
            v-model="form.medicalHistory" 
            type="textarea" 
            :rows="2"
            placeholder="请输入过往病史（选填）" 
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
import { Plus, Search, Edit, Delete, Calendar, Camera } from '@element-plus/icons-vue'
import { page as getPets, list as getAllPets, create, update, deletePet, upload, getByGender, uploadPhotos } from '@/api/pet'
import { useUserInfoStore } from '@/stores/userinfo'

const loading = ref(false)
const submitting = ref(false)
const pets = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(12)
const searchKeyword = ref('')
const filterType = ref('')
const filterGender = ref('')  // 性别筛选

// 用户信息和角色判断
const userInfoStore = useUserInfoStore()
const isAdmin = computed(() => userInfoStore.userInfo?.roleType === 3)

// 弹窗相关
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()
const avatarFile = ref(null)  // 待上传的头像文件
const avatarPreview = ref('')  // 头像预览URL
const form = reactive({
  id: null,
  name: '',
  type: '',
  breed: '',
  gender: 0,
  birthDate: null,
  weight: null,
  allergy: '',
  medicalHistory: '',
  avatar: ''
})

const formRules = {
  name: [{ required: true, message: '请输入宠物名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择宠物类型', trigger: 'change' }]
}

// 筛选后的宠物列表
const filteredPets = computed(() => {
  let result = pets.value
  if (searchKeyword.value) {
    result = result.filter(p => 
      p.name?.toLowerCase().includes(searchKeyword.value.toLowerCase())
    )
  }
  if (filterType.value) {
    result = result.filter(p => p.type === filterType.value)
  }
  return result
})

// 工具函数 - 适配后端枚举返回的label字符串
const getGenderText = (gender) => {
  // 后端现在返回的是枚举的label（"公"/"母"/"未知"）
  if (typeof gender === 'string') {
    const map = { '未知': '未知', '公': '♂ 公', '母': '♀ 母' }
    return map[gender] || gender
  }
  // 兼容旧的数字格式
  const map = { 0: '未知', 1: '♂ 公', 2: '♀ 母' }
  return map[gender] || '未知'
}

const getGenderType = (gender) => {
  if (typeof gender === 'string') {
    const map = { '未知': 'info', '公': '', '母': 'danger' }
    return map[gender] || 'info'
  }
  const map = { 0: 'info', 1: '', 2: 'danger' }
  return map[gender] || 'info'
}

const getPetEmoji = (type) => {
  const map = { '狗': '🐕', '猫': '🐱', '兔子': '🐰', '仓鼠': '🐹' }
  return map[type] || '🐾'
}

const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleDateString('zh-CN')
}

// 加载宠物列表
const loadPets = async () => {
  loading.value = true
  try {
    if (isAdmin.value) {
      // 管理员：获取全部宠物
      const res = await getAllPets()
      pets.value = res.data || []
      total.value = pets.value.length
    } else {
      // 普通用户：分页获取自己的宠物
      const res = await getPets(currentPage.value, pageSize.value)
      pets.value = res.data?.records || []
      total.value = res.data?.total || 0
    }
  } catch (e) {
    console.error('加载失败:', e)
  } finally {
    loading.value = false
  }
}

// 按性别筛选（调用后端接口）
const handleGenderFilter = async (gender) => {
  if (!gender) {
    // 清空筛选，重新加载全部
    loadPets()
    return
  }
  loading.value = true
  try {
    const res = await getByGender(gender)
    if (res.code === 200) {
      pets.value = res.data || []
      total.value = pets.value.length
    }
  } catch (e) {
    console.error('性别筛选失败:', e)
    ElMessage.error('筛选失败')
  } finally {
    loading.value = false
  }
}

// 宠物头像显示
const petAvatarDisplay = () => {
  return avatarPreview.value || form.avatar || ''
}

// 选择宠物头像（只做本地预览，不上传）
const handlePetAvatarUpload = (file) => {
  if (file.size > 2 * 1024 * 1024) {
    ElMessage.warning('头像大小不能超过2MB')
    return false
  }
  avatarFile.value = file
  avatarPreview.value = URL.createObjectURL(file)
  return false
}

// 打开弹窗
const openDialog = (pet = null) => {
  // 重置头像相关状态
  avatarFile.value = null
  avatarPreview.value = ''
  
  if (pet) {
    isEdit.value = true
    Object.assign(form, {
      id: pet.id,
      name: pet.name,
      type: pet.type,
      breed: pet.breed,
      gender: pet.gender,
      birthDate: pet.birthDate,
      weight: pet.weight,
      allergy: pet.allergy,
      medicalHistory: pet.medicalHistory,
      avatar: pet.avatar
    })
  } else {
    isEdit.value = false
    Object.assign(form, {
      id: null,
      name: '',
      type: '',
      breed: '',
      gender: 0,
      birthDate: null,
      weight: null,
      allergy: '',
      medicalHistory: '',
      avatar: ''
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
    let avatarUrl = form.avatar
    
    // 如果有新选择的头像文件，先上传
    if (avatarFile.value) {
      const uploadRes = await upload(avatarFile.value)
      if (uploadRes.code === 200 && uploadRes.data) {
        avatarUrl = uploadRes.data
      } else {
        ElMessage.error('头像上传失败')
        return
      }
    }
    
    const submitData = { ...form, avatar: avatarUrl }
    
    if (isEdit.value) {
      await update(form.id, submitData)
      ElMessage.success('更新成功')
    } else {
      await create(submitData)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    loadPets()
  } catch (e) {
    console.error('操作失败:', e)
    ElMessage.error('操作失败')
  } finally {
    submitting.value = false
  }
}

// 删除宠物
const handleDelete = async (pet) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除宠物 "${pet.name}" 吗？此操作不可恢复。`,
      '确认删除',
      { confirmButtonText: '删除', cancelButtonText: '取消', type: 'warning' }
    )
    await deletePet(pet.id)
    ElMessage.success('删除成功')
    loadPets()
  } catch (e) {
    if (e !== 'cancel') {
      console.error('删除失败:', e)
    }
  }
}

// 上传宠物照片 - 使用http-request每个文件单独上传但累加到现有照片
const handleUploadPhotos = async (options, pet) => {
  const file = options.file
  if (file.size > 5 * 1024 * 1024) {
    ElMessage.warning('图片大小不能超过5MB')
    return
  }
  
  try {
    // 上传图片
    const res = await uploadPhotos([file])
    if (res.code === 200 && res.data && res.data.length > 0) {
      // 重新获取最新的宠物数据
      const currentPet = pets.value.find(p => p.id === pet.id)
      let existingPhotos = []
      if (currentPet?.photos) {
        try {
          existingPhotos = JSON.parse(currentPet.photos)
        } catch (e) {
          existingPhotos = []
        }
      }
      // 添加新照片
      existingPhotos.push(...res.data)
      // 更新宠物信息
      await update(pet.id, { photos: JSON.stringify(existingPhotos) })
      // 同步更新本地数据
      if (currentPet) {
        currentPet.photos = JSON.stringify(existingPhotos)
      }
      ElMessage.success('照片添加成功')
    }
  } catch (e) {
    console.error('上传失败:', e)
    ElMessage.error('上传失败')
  }
}

onMounted(() => {
  loadPets()
})
</script>

<style lang="scss" scoped>
.pets-page {
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

// 宠物卡片网格
.pets-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
  min-height: 200px;
}

.pet-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  transition: all 0.3s ease;
  position: relative;

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 12px 32px rgba(0, 0, 0, 0.1);

    .pet-actions {
      opacity: 1;
    }
  }
}

.pet-avatar {
  display: flex;
  justify-content: center;
  margin-bottom: 16px;
  position: relative;

  .el-avatar {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    font-size: 36px;
  }

  .gender-tag {
    position: absolute;
    right: 20%;
    bottom: 0;
  }
}

.pet-info {
  text-align: center;

  .pet-name {
    font-size: 18px;
    font-weight: 600;
    color: #1f2937;
    margin: 0 0 4px 0;
  }

  .pet-breed {
    font-size: 14px;
    color: #6b7280;
    margin: 0 0 8px 0;
  }

  .pet-owner {
    font-size: 12px;
    color: #9ca3af;
    margin: 0 0 12px 0;
  }
}

.pet-details {
  display: flex;
  justify-content: center;
  gap: 20px;
  font-size: 13px;
  color: #9ca3af;

  .detail-item {
    display: flex;
    align-items: center;
    gap: 4px;
  }

  .weight-icon {
    font-size: 14px;
  }
}

.pet-actions {
  position: absolute;
  top: 8px;
  right: 8px;
  opacity: 0;
  transition: opacity 0.2s;
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 4px;

  // 处理el-upload让它不破坏布局
  :deep(.el-upload) {
    display: flex !important;
  }

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

  .pets-grid {
    grid-template-columns: 1fr;
  }
}

// 头像上传
.avatar-upload {
  display: flex;
  align-items: center;
  gap: 16px;
  
  .el-avatar {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    font-size: 32px;
  }
}
</style>
