<template>
  <div class="gallery-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-info">
        <h1>宠物相册</h1>
        <p>查看您所有宠物的精彩瞬间</p>
      </div>
    </div>

    <!-- 宠物选择器 -->
    <div class="pet-tabs">
      <el-radio-group v-model="selectedPetId" @change="handlePetChange">
        <el-radio-button :value="0">全部</el-radio-button>
        <el-radio-button 
          v-for="pet in pets" 
          :key="pet.id" 
          :value="pet.id"
        >
          {{ pet.name }}
        </el-radio-button>
      </el-radio-group>
    </div>

    <!-- 相册展示 -->
    <div class="gallery-container" v-loading="loading">
      <template v-if="displayPhotos.length > 0">
        <div 
          v-for="(item, index) in displayPhotos" 
          :key="index" 
          class="photo-card"
          @click="previewImage(item)"
        >
          <el-image 
            :src="item.url" 
            fit="cover"
            class="photo-image"
            lazy
          >
            <template #placeholder>
              <div class="image-placeholder">
                <el-icon><Loading /></el-icon>
              </div>
            </template>
          </el-image>
          <div class="photo-info">
            <span class="pet-name">{{ item.petName }}</span>
          </div>
        </div>
      </template>
      
      <!-- 空状态 -->
      <el-empty 
        v-if="!loading && displayPhotos.length === 0" 
        description="暂无照片，去【宝贝宠物】页面添加照片吧~"
        :image-size="120"
        class="empty-state"
      />
    </div>

    <!-- 图片预览 -->
    <el-image-viewer
      v-if="showViewer"
      :url-list="viewerList"
      :initial-index="viewerIndex"
      @close="showViewer = false"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Loading } from '@element-plus/icons-vue'
import { page as getPets, list as getAllPets } from '@/api/pet'
import { useUserInfoStore } from '@/stores/userinfo'

const loading = ref(false)
const pets = ref([])
const selectedPetId = ref(0)
const showViewer = ref(false)
const viewerIndex = ref(0)

// 用户信息和角色判断
const userInfoStore = useUserInfoStore()
const isAdmin = computed(() => userInfoStore.userInfo?.roleType === 3)

// 解析所有宠物的照片
const allPhotos = computed(() => {
  const photos = []
  pets.value.forEach(pet => {
    if (pet.photos) {
      try {
        const urls = JSON.parse(pet.photos)
        urls.forEach(url => {
          photos.push({
            url,
            petId: pet.id,
            petName: pet.name
          })
        })
      } catch (e) {
        console.error('解析照片失败:', e)
      }
    }
  })
  return photos
})

// 根据选择的宠物过滤
const displayPhotos = computed(() => {
  if (selectedPetId.value === 0) {
    return allPhotos.value
  }
  return allPhotos.value.filter(p => p.petId === selectedPetId.value)
})

// 预览图片列表
const viewerList = computed(() => displayPhotos.value.map(p => p.url))

// 加载宠物列表
const loadPets = async () => {
  loading.value = true
  try {
    if (isAdmin.value) {
      // 管理员：获取全部宠物
      const res = await getAllPets()
      pets.value = res.data || []
    } else {
      // 普通用户：获取自己的宠物
      const res = await getPets(1, 100)
      pets.value = res.data?.records || []
    }
  } catch (e) {
    console.error('加载失败:', e)
  } finally {
    loading.value = false
  }
}

const handlePetChange = () => {
  // 切换时可以添加动画效果
}

const previewImage = (item) => {
  viewerIndex.value = displayPhotos.value.findIndex(p => p.url === item.url)
  showViewer.value = true
}

onMounted(() => {
  loadPets()
})
</script>

<style lang="scss" scoped>
.gallery-page {
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

.pet-tabs {
  margin-bottom: 24px;

  :deep(.el-radio-button__inner) {
    border-radius: 20px;
    padding: 8px 20px;
  }

  :deep(.el-radio-button:first-child .el-radio-button__inner) {
    border-radius: 20px;
  }

  :deep(.el-radio-button:last-child .el-radio-button__inner) {
    border-radius: 20px;
  }
}

.gallery-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
  min-height: 200px;
}

.photo-card {
  position: relative;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.3s, box-shadow 0.3s;
  background: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);

    .photo-info {
      opacity: 1;
    }
  }
}

.photo-image {
  width: 100%;
  aspect-ratio: 1;
  display: block;
}

.image-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f5f5;
  color: #999;
}

.photo-info {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 12px;
  background: linear-gradient(transparent, rgba(0, 0, 0, 0.6));
  opacity: 0;
  transition: opacity 0.3s;

  .pet-name {
    color: white;
    font-size: 14px;
    font-weight: 500;
  }
}

.empty-state {
  grid-column: 1 / -1;
  padding: 60px 0;
}

@media (max-width: 600px) {
  .gallery-container {
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
  }
}
</style>
