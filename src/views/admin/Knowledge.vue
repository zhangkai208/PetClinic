<template>
  <div class="knowledge-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-info">
        <h1>AI知识库</h1>
        <p>管理 AI 助手的知识库内容，支持 RAG 检索增强</p>
      </div>
      <div class="header-actions">
        <el-button :icon="Refresh" @click="loadAllKnowledge">刷新</el-button>
        <el-button type="primary" :icon="Plus" @click="openAddDialog">
          添加知识
        </el-button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-cards">
      <div class="stat-card">
        <div class="stat-icon">📚</div>
        <div class="stat-content">
          <div class="stat-label">搜索结果</div>
          <div class="stat-value">{{ knowledgeList.length }}</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">🔍</div>
        <div class="stat-content">
          <div class="stat-label">当前查询</div>
          <div class="stat-value">{{ searchQuery || '全部' }}</div>
        </div>
      </div>
    </div>

    <!-- 搜索测试区域 -->
    <div class="search-section">
      <div class="section-title">
        <el-icon><Search /></el-icon>
        <span>知识检索测试</span>
      </div>
      <div class="search-bar">
        <el-input 
          v-model="searchQuery" 
          placeholder="输入问题测试知识库检索效果..." 
          :prefix-icon="Search"
          clearable
          size="large"
          class="search-input"
          @keyup.enter="handleSearch"
        />
        <el-input-number v-model="topK" :min="1" :max="20" size="large" style="width: 120px" />
        <el-button type="primary" size="large" :loading="searching" @click="handleSearch">
          检索
        </el-button>
      </div>
    </div>

    <!-- 知识列表 -->
    <div class="knowledge-list">
      <div class="section-title">
        <el-icon><Document /></el-icon>
        <span>知识内容</span>
      </div>
      
      <el-empty v-if="knowledgeList.length === 0 && !loading" description="暂无知识，请添加或检索" />
      
      <div v-loading="loading" class="knowledge-cards">
        <div v-for="item in knowledgeList" :key="item.id" class="knowledge-card">
          <div class="card-header">
            <el-tag size="small" type="info">{{ item.id?.substring(0, 8) }}...</el-tag>
            <el-button text type="danger" size="small" @click="handleDelete(item)">
              <el-icon><Delete /></el-icon>
            </el-button>
          </div>
          <div class="card-content">
            {{ item.content }}
          </div>
          <div v-if="item.metadata && Object.keys(item.metadata).length > 0" class="card-meta">
            <span v-for="(value, key) in item.metadata" :key="key" class="meta-tag">
              {{ key }}: {{ value }}
            </span>
          </div>
        </div>
      </div>
    </div>

    <!-- 添加知识弹窗 -->
    <el-dialog 
      v-model="addDialogVisible" 
      title="添加知识"
      width="600px"
      destroy-on-close
    >
      <el-tabs v-model="addMode">
        <el-tab-pane label="单条添加" name="single">
          <el-input
            v-model="singleContent"
            type="textarea"
            :rows="6"
            placeholder="输入知识内容..."
          />
        </el-tab-pane>
        <el-tab-pane label="批量添加" name="batch">
          <el-input
            v-model="batchContent"
            type="textarea"
            :rows="10"
            placeholder="每行一条知识内容..."
          />
          <div class="batch-tip">
            <el-icon><InfoFilled /></el-icon>
            <span>每行作为一条独立的知识存储</span>
          </div>
        </el-tab-pane>
      </el-tabs>

      <template #footer>
        <el-button @click="addDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleAdd">
          添加
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Delete, Document, InfoFilled, Refresh } from '@element-plus/icons-vue'
import { addKnowledge, batchAddKnowledge, searchKnowledge, deleteKnowledge, listKnowledge } from '@/api/knowledge'

const loading = ref(false)
const searching = ref(false)
const submitting = ref(false)
const searchQuery = ref('')
const topK = ref(5)
const knowledgeList = ref([])

// 添加弹窗
const addDialogVisible = ref(false)
const addMode = ref('single')
const singleContent = ref('')
const batchContent = ref('')

// 打开添加弹窗
const openAddDialog = () => {
  singleContent.value = ''
  batchContent.value = ''
  addMode.value = 'single'
  addDialogVisible.value = true
}

// 加载全部知识
const loadAllKnowledge = async () => {
  loading.value = true
  try {
    const res = await listKnowledge(100)
    knowledgeList.value = res.data || []
  } catch (e) {
    console.error('加载知识列表失败:', e)
  } finally {
    loading.value = false
  }
}

// 搜索知识
const handleSearch = async () => {
  if (!searchQuery.value.trim()) {
    ElMessage.warning('请输入搜索内容')
    return
  }
  
  searching.value = true
  loading.value = true
  try {
    const res = await searchKnowledge(searchQuery.value, topK.value)
    knowledgeList.value = res.data || []
    if (knowledgeList.value.length === 0) {
      ElMessage.info('未找到相关知识')
    }
  } catch (e) {
    console.error('搜索失败:', e)
  } finally {
    searching.value = false
    loading.value = false
  }
}

// 添加知识
const handleAdd = async () => {
  submitting.value = true
  try {
    if (addMode.value === 'single') {
      if (!singleContent.value.trim()) {
        ElMessage.warning('请输入知识内容')
        return
      }
      await addKnowledge(singleContent.value.trim())
      ElMessage.success('添加成功')
    } else {
      const lines = batchContent.value.split('\n').filter(line => line.trim())
      if (lines.length === 0) {
        ElMessage.warning('请输入知识内容')
        return
      }
      await batchAddKnowledge(lines)
      ElMessage.success(`批量添加成功，共 ${lines.length} 条`)
    }
    addDialogVisible.value = false
    // 重新加载知识列表
    loadAllKnowledge()
  } catch (e) {
    console.error('添加失败:', e)
  } finally {
    submitting.value = false
  }
}

// 删除知识
const handleDelete = async (item) => {
  try {
    await ElMessageBox.confirm(
      '确定要删除这条知识吗？此操作不可恢复。',
      '确认删除',
      { confirmButtonText: '删除', cancelButtonText: '取消', type: 'warning' }
    )
    await deleteKnowledge([item.id])
    ElMessage.success('删除成功')
    // 从列表中移除
    knowledgeList.value = knowledgeList.value.filter(k => k.id !== item.id)
  } catch (e) {
    if (e !== 'cancel') {
      console.error('删除失败:', e)
    }
  }
}

onMounted(() => {
  // 页面加载时加载全部知识
  loadAllKnowledge()
})
</script>

<style lang="scss" scoped>
.knowledge-page {
  max-width: 1200px;
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

.stats-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
  margin-bottom: 24px;
}

.stat-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);

  .stat-icon {
    font-size: 32px;
  }

  .stat-label {
    font-size: 13px;
    color: #9ca3af;
    margin-bottom: 4px;
  }

  .stat-value {
    font-size: 20px;
    font-weight: 600;
    color: #1f2937;
  }
}

.search-section {
  background: white;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
  margin-bottom: 16px;

  .el-icon {
    color: #667eea;
  }
}

.search-bar {
  display: flex;
  gap: 12px;

  .search-input {
    flex: 1;
  }
}

.knowledge-list {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}

.knowledge-cards {
  display: grid;
  gap: 16px;
}

.knowledge-card {
  background: #f9fafb;
  border-radius: 8px;
  padding: 16px;
  border: 1px solid #e5e7eb;
  transition: all 0.2s;

  &:hover {
    border-color: #667eea;
    box-shadow: 0 4px 12px rgba(102, 126, 234, 0.1);
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 12px;
  }

  .card-content {
    font-size: 14px;
    line-height: 1.8;
    color: #374151;
    white-space: pre-wrap;
    word-break: break-word;
  }

  .card-meta {
    margin-top: 12px;
    padding-top: 12px;
    border-top: 1px solid #e5e7eb;
    display: flex;
    flex-wrap: wrap;
    gap: 8px;

    .meta-tag {
      font-size: 12px;
      color: #6b7280;
      background: #e5e7eb;
      padding: 2px 8px;
      border-radius: 4px;
    }
  }
}

.batch-tip {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-top: 12px;
  font-size: 13px;
  color: #9ca3af;
}

// 响应式
@media (max-width: 768px) {
  .search-bar {
    flex-wrap: wrap;

    .search-input {
      width: 100%;
    }
  }
}
</style>
