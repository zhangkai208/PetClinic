<template>
  <div class="reminders-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-info">
        <h1>📬 邮件通知</h1>
        <p>查看预约提醒邮件发送记录</p>
      </div>
      <!-- 管理员发送按钮 -->
      <div class="header-actions" v-if="isAdmin">
        <el-button type="primary" :icon="Message" @click="openSendDialog" :loading="sending">
          发送提醒
        </el-button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-cards" v-if="isAdmin">
      <div class="stat-card success">
        <div class="stat-icon">✅</div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.success }}</div>
          <div class="stat-label">发送成功</div>
        </div>
      </div>
      <div class="stat-card failed">
        <div class="stat-icon">❌</div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.failed }}</div>
          <div class="stat-label">发送失败</div>
        </div>
      </div>
      <div class="stat-card total">
        <div class="stat-icon">📧</div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.total }}</div>
          <div class="stat-label">总计</div>
        </div>
      </div>
    </div>

    <!-- 邮件列表 -->
    <div class="mail-container" v-loading="loading">
      <!-- 邮件卡片 -->
      <div class="mail-list" v-if="reminders.length > 0">
        <div 
          v-for="reminder in reminders" 
          :key="reminder.id" 
          class="mail-card"
          :class="{ 'mail-read': true, 'mail-failed': reminder.status === 0 }"
        >
          <div class="mail-icon">
            <span v-if="reminder.status === 1">📩</span>
            <span v-else>📭</span>
          </div>
          
          <div class="mail-content">
            <div class="mail-header">
              <span class="mail-subject">🐾 宠物预约提醒</span>
              <el-tag :type="reminder.status === 1 ? 'success' : 'danger'" size="small">
                {{ reminder.status === 1 ? '发送成功' : '发送失败' }}
              </el-tag>
            </div>
            
            <div class="mail-meta">
              <span class="mail-to">
                <el-icon><Message /></el-icon>
                {{ reminder.email }}
              </span>
              <span class="mail-appointment" v-if="reminder.appointmentTime">
                <el-icon><Calendar /></el-icon>
                预约: {{ formatDateTime(reminder.appointmentTime) }}
              </span>
              <span class="mail-time">
                <el-icon><Clock /></el-icon>
                发送: {{ formatDateTime(reminder.sendTime) }}
              </span>
            </div>

            <div class="mail-preview" v-if="reminder.errorMessage">
              <span class="error-label">失败原因：</span>
              <span class="error-msg">{{ reminder.errorMessage }}</span>
            </div>
          </div>

          <div class="mail-actions">
            <el-tooltip content="查看详情" placement="top">
              <el-button text :icon="View" @click="viewDetail(reminder)" />
            </el-tooltip>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <el-empty 
        v-else-if="!loading" 
        description="暂无邮件通知记录"
        :image-size="140"
      >
        <template #image>
          <div class="empty-icon">📭</div>
        </template>
      </el-empty>
    </div>

    <!-- 分页（普通用户） -->
    <div class="pagination-wrapper" v-if="!isAdmin && total > pageSize">
      <el-pagination
        v-model:current-page="currentPage"
        :page-size="pageSize"
        :total="total"
        layout="prev, pager, next"
        @current-change="loadReminders"
      />
    </div>

    <!-- 发送提醒对话框（管理员） -->
    <el-dialog
      v-model="sendDialogVisible"
      title="发送预约提醒"
      width="420px"
      destroy-on-close
    >
      <div class="send-dialog-content">
        <p class="send-hint">选择要发送提醒的预约日期：</p>
        <el-date-picker
          v-model="targetDate"
          type="date"
          placeholder="选择日期"
          format="YYYY-MM-DD"
          value-format="YYYY-MM-DD"
          :shortcuts="dateShortcuts"
          style="width: 100%"
        />
        <p class="send-tip">
          <el-icon><InfoFilled /></el-icon>
          系统将向该日期有预约的用户发送邮件提醒
        </p>
      </div>
      <template #footer>
        <el-button @click="sendDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="sending" @click="handleSend">
          {{ sending ? '发送中...' : '确认发送' }}
        </el-button>
      </template>
    </el-dialog>

    <!-- 详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="邮件详情"
      width="500px"
    >
      <div class="detail-content" v-if="currentReminder">
        <div class="detail-item">
          <span class="label">发送状态：</span>
          <el-tag :type="currentReminder.status === 1 ? 'success' : 'danger'">
            {{ currentReminder.status === 1 ? '发送成功' : '发送失败' }}
          </el-tag>
        </div>
        <div class="detail-item">
          <span class="label">收件邮箱：</span>
          <span class="value">{{ currentReminder.email }}</span>
        </div>
        <div class="detail-item">
          <span class="label">预约ID：</span>
          <span class="value">#{{ currentReminder.appointmentId }}</span>
        </div>
        <div class="detail-item" v-if="currentReminder.appointmentTime">
          <span class="label">预约时间：</span>
          <span class="value appointment-time">{{ formatDateTime(currentReminder.appointmentTime) }}</span>
        </div>
        <div class="detail-item">
          <span class="label">发送时间：</span>
          <span class="value">{{ formatDateTime(currentReminder.sendTime) }}</span>
        </div>
        <div class="detail-item">
          <span class="label">创建时间：</span>
          <span class="value">{{ formatDateTime(currentReminder.createTime) }}</span>
        </div>
        <div class="detail-item error" v-if="currentReminder.errorMessage">
          <span class="label">失败原因：</span>
          <span class="value error-text">{{ currentReminder.errorMessage }}</span>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Message, Clock, View, InfoFilled, Calendar } from '@element-plus/icons-vue'
import { list, page, sendReminder } from '@/api/reminder'
import { useUserInfoStore } from '@/stores/userinfo'

const loading = ref(false)
const sending = ref(false)
const reminders = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

// 用户信息
const userInfoStore = useUserInfoStore()
const isAdmin = computed(() => userInfoStore.userInfo?.roleType === 3)

// 统计数据
const stats = computed(() => {
  const success = reminders.value.filter(r => r.status === 1).length
  const failed = reminders.value.filter(r => r.status === 0).length
  return {
    success,
    failed,
    total: reminders.value.length
  }
})

// 发送对话框
const sendDialogVisible = ref(false)
const targetDate = ref('')
const dateShortcuts = [
  { text: '明天', value: () => { 
    const d = new Date(); 
    d.setDate(d.getDate() + 1); 
    return d.toISOString().split('T')[0]
  }},
  { text: '后天', value: () => { 
    const d = new Date(); 
    d.setDate(d.getDate() + 2); 
    return d.toISOString().split('T')[0]
  }},
  { text: '下周', value: () => { 
    const d = new Date(); 
    d.setDate(d.getDate() + 7); 
    return d.toISOString().split('T')[0]
  }}
]

// 详情对话框
const detailDialogVisible = ref(false)
const currentReminder = ref(null)

// 加载提醒记录
const loadReminders = async () => {
  loading.value = true
  try {
    if (isAdmin.value) {
      // 管理员获取全部
      const res = await list()
      reminders.value = res.data || []
    } else {
      // 普通用户分页获取自己的
      const res = await page(currentPage.value, pageSize.value)
      reminders.value = res.data?.records || []
      total.value = res.data?.total || 0
    }
  } catch (e) {
    console.error('加载提醒记录失败:', e)
    ElMessage.error('加载提醒记录失败')
  } finally {
    loading.value = false
  }
}

// 打开发送对话框
const openSendDialog = () => {
  // 默认选择明天
  const tomorrow = new Date()
  tomorrow.setDate(tomorrow.getDate() + 1)
  targetDate.value = tomorrow.toISOString().split('T')[0]
  sendDialogVisible.value = true
}

// 发送提醒
const handleSend = async () => {
  if (!targetDate.value) {
    ElMessage.warning('请选择日期')
    return
  }
  sending.value = true
  try {
    await sendReminder(targetDate.value)
    ElMessage.success('提醒发送成功！')
    sendDialogVisible.value = false
    loadReminders()  // 刷新列表
  } catch (e) {
    console.error('发送提醒失败:', e)
    ElMessage.error('发送提醒失败')
  } finally {
    sending.value = false
  }
}

// 查看详情
const viewDetail = (reminder) => {
  currentReminder.value = reminder
  detailDialogVisible.value = true
}

// 格式化时间
const formatDateTime = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

onMounted(() => {
  loadReminders()
})
</script>

<style lang="scss" scoped>
.reminders-page {
  max-width: 900px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;

  .header-info {
    h1 {
      font-size: 24px;
      font-weight: 600;
      color: #1a1a2e;
      margin: 0 0 8px 0;
    }
    p {
      font-size: 14px;
      color: #8c8c8c;
      margin: 0;
    }
  }
}

// 统计卡片
.stats-cards {
  display: flex;
  gap: 16px;
  margin-bottom: 24px;

  .stat-card {
    flex: 1;
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 16px 20px;
    background: white;
    border-radius: 12px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);

    .stat-icon {
      font-size: 28px;
    }

    .stat-info {
      .stat-value {
        font-size: 24px;
        font-weight: 600;
        color: #1a1a2e;
      }
      .stat-label {
        font-size: 13px;
        color: #8c8c8c;
      }
    }

    &.success {
      border-left: 4px solid #52c41a;
    }
    &.failed {
      border-left: 4px solid #ff4d4f;
    }
    &.total {
      border-left: 4px solid #1890ff;
    }
  }
}

// 邮件容器
.mail-container {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  min-height: 400px;
}

// 邮件列表
.mail-list {
  padding: 8px;
}

// 邮件卡片
.mail-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
  border-bottom: 1px solid #f0f0f0;

  &:last-child {
    border-bottom: none;
  }

  &:hover {
    background: #fafafa;
  }

  &.mail-failed {
    background: #fff2f0;
    
    &:hover {
      background: #ffebe8;
    }
  }

  .mail-icon {
    font-size: 32px;
    flex-shrink: 0;
  }

  .mail-content {
    flex: 1;
    min-width: 0;

    .mail-header {
      display: flex;
      align-items: center;
      gap: 8px;
      margin-bottom: 6px;

      .mail-subject {
        font-size: 15px;
        font-weight: 500;
        color: #1a1a2e;
      }
    }

    .mail-meta {
      display: flex;
      gap: 16px;
      font-size: 13px;
      color: #8c8c8c;

      span {
        display: flex;
        align-items: center;
        gap: 4px;
      }
      
      .mail-appointment {
        color: #1890ff;
        font-weight: 500;
      }
    }

    .mail-preview {
      margin-top: 8px;
      font-size: 13px;
      
      .error-label {
        color: #8c8c8c;
      }
      .error-msg {
        color: #ff4d4f;
      }
    }
  }

  .mail-actions {
    flex-shrink: 0;
  }
}

// 空状态
.empty-icon {
  font-size: 80px;
  opacity: 0.6;
}

// 分页
.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

// 发送对话框
.send-dialog-content {
  .send-hint {
    margin: 0 0 12px 0;
    font-size: 14px;
    color: #333;
  }

  .send-tip {
    display: flex;
    align-items: center;
    gap: 6px;
    margin-top: 12px;
    font-size: 13px;
    color: #8c8c8c;
    
    .el-icon {
      color: #1890ff;
    }
  }
}

// 详情对话框
.detail-content {
  .detail-item {
    display: flex;
    align-items: flex-start;
    padding: 12px 0;
    border-bottom: 1px solid #f0f0f0;

    &:last-child {
      border-bottom: none;
    }

    .label {
      width: 90px;
      flex-shrink: 0;
      font-size: 14px;
      color: #8c8c8c;
    }

    .value {
      font-size: 14px;
      color: #333;
    }

    &.error .error-text {
      color: #ff4d4f;
    }
    
    .appointment-time {
      color: #1890ff;
      font-weight: 500;
    }
  }
}

// 响应式
@media (max-width: 640px) {
  .stats-cards {
    flex-direction: column;
  }

  .mail-card {
    flex-wrap: wrap;
    
    .mail-content {
      width: 100%;
      order: 2;
    }
    
    .mail-actions {
      order: 1;
      margin-left: auto;
    }
  }
}
</style>
