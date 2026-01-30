<template>
  <div class="chat-container">
    <!-- 左侧会话列表 -->
    <aside class="conversation-sidebar" :class="{ collapsed: sidebarCollapsed }">
      <div class="sidebar-header">
        <el-button type="primary" class="new-chat-btn" @click="createNewConversation">
          <el-icon><Plus /></el-icon>
          <span v-if="!sidebarCollapsed">新建会话</span>
        </el-button>
      </div>
      
      <div class="conversation-list">
        <div
          v-for="conv in conversations"
          :key="conv.id"
          class="conversation-item"
          :class="{ active: currentConversationId === conv.id }"
          @click="selectConversation(conv)"
        >
          <el-icon class="conv-icon"><ChatDotRound /></el-icon>
          <span v-if="!sidebarCollapsed" class="conv-title">{{ conv.title || '新会话' }}</span>
          <el-button
            v-if="!sidebarCollapsed"
            class="delete-btn"
            :icon="Delete"
            link
            @click.stop="handleDeleteConversation(conv.id)"
          />
        </div>
        
        <el-empty v-if="conversations.length === 0" description="暂无会话" :image-size="60" />
      </div>
      
      <div class="sidebar-footer">
        <el-tooltip :content="sidebarCollapsed ? '展开' : '收起'" placement="right">
          <el-button class="collapse-btn" link @click="toggleSidebar">
            <el-icon><component :is="sidebarCollapsed ? Expand : Fold" /></el-icon>
          </el-button>
        </el-tooltip>
      </div>
    </aside>

    <!-- 右侧聊天区域 -->
    <main class="chat-main" :class="{ 'sidebar-collapsed': sidebarCollapsed }">
      <!-- 欢迎界面 -->
      <div v-if="!currentConversationId" class="welcome-screen">
        <div class="welcome-content">
          <div class="welcome-icon">🐾</div>
          <h1>PetClinic AI 助手</h1>
          <p>您的智能宠物健康顾问，随时为您解答宠物护理、健康咨询等问题</p>
          <el-button type="primary" size="large" @click="createNewConversation">
            <el-icon><Plus /></el-icon>
            开始新对话
          </el-button>
        </div>
      </div>

      <!-- 聊天消息区域 -->
      <template v-else>
        <div class="messages-container" ref="messagesContainer">
          <div
            v-for="(msg, index) in messages"
            :key="index"
            class="message-wrapper"
            :class="msg.role"
          >
            <div class="message-avatar">
              <el-avatar v-if="msg.role === 'user'" :size="32" :src="userAvatar">
                {{ userInfo.nickname?.charAt(0) || 'U' }}
              </el-avatar>
              <div v-else class="ai-avatar">🤖</div>
            </div>
            <div class="message-content">
              <div class="message-bubble" v-html="formatMessage(msg.content)"></div>
            </div>
          </div>
          
          <!-- AI正在输入 -->
          <div v-if="isStreaming" class="message-wrapper assistant">
            <div class="message-avatar">
              <div class="ai-avatar">🤖</div>
            </div>
            <div class="message-content">
              <div class="message-bubble streaming">
                <span v-html="formatMessage(streamingContent)"></span>
                <span class="typing-cursor">|</span>
              </div>
            </div>
          </div>
          
          <el-empty v-if="messages.length === 0 && !isStreaming" description="发送消息开始对话" :image-size="80" />
        </div>

        <!-- 输入区域 -->
        <div class="input-area">
          <div class="input-wrapper">
            <el-input
              v-model="inputMessage"
              type="textarea"
              :rows="1"
              :autosize="{ minRows: 1, maxRows: 4 }"
              placeholder="输入您的问题..."
              resize="none"
              @keydown.enter.exact.prevent="sendMessage"
            />
            <el-button
              class="send-btn"
              type="primary"
              :icon="Promotion"
              :disabled="!inputMessage.trim() || isStreaming"
              @click="sendMessage"
            />
          </div>
          <div class="input-hint">按 Enter 发送，Shift + Enter 换行</div>
        </div>
      </template>
    </main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Plus, ChatDotRound, Delete, Expand, Fold, Promotion 
} from '@element-plus/icons-vue'
import { useUserInfoStore } from '@/stores/userinfo'
import {
  createConversation,
  getConversations,
  getMessages,
  deleteConversation,
  sendMessage as sendChatMessage
} from '@/api/chat'

const userInfoStore = useUserInfoStore()
const userInfo = computed(() => userInfoStore.userInfo || {})
const userAvatar = computed(() => userInfo.value.avatar || '')

// 状态
const sidebarCollapsed = ref(false)
const conversations = ref([])
const currentConversationId = ref(null)
const messages = ref([])
const inputMessage = ref('')
const isStreaming = ref(false)
const streamingContent = ref('')
const messagesContainer = ref(null)
let cancelStream = null

// 初始化
onMounted(() => {
  loadConversations()
})

// 监听消息变化，自动滚动到底部
watch([messages, streamingContent], () => {
  nextTick(() => {
    scrollToBottom()
  })
})

// 切换侧边栏
const toggleSidebar = () => {
  sidebarCollapsed.value = !sidebarCollapsed.value
}

// 加载会话列表
const loadConversations = async () => {
  try {
    const res = await getConversations()
    conversations.value = res.data || []
  } catch (e) {
    console.error('加载会话列表失败:', e)
  }
}

// 创建新会话
const createNewConversation = async () => {
  try {
    const title = `新会话 ${new Date().toLocaleString()}`
    await createConversation({ title })
    await loadConversations()
    
    // 选中新创建的会话
    if (conversations.value.length > 0) {
      selectConversation(conversations.value[0])
    }
    ElMessage.success('会话创建成功')
  } catch (e) {
    ElMessage.error('创建会话失败')
  }
}

// 选择会话
const selectConversation = async (conv) => {
  currentConversationId.value = conv.id
  messages.value = []
  streamingContent.value = ''
  
  try {
    const res = await getMessages(conv.id)
    messages.value = res.data || []
  } catch (e) {
    console.error('加载消息失败:', e)
  }
}

// 删除会话
const handleDeleteConversation = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除这个会话吗？删除后无法恢复。', '删除确认', {
      confirmButtonText: '删除',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await deleteConversation(id)
    await loadConversations()
    
    if (currentConversationId.value === id) {
      currentConversationId.value = null
      messages.value = []
    }
    
    ElMessage.success('删除成功')
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 发送消息
const sendMessage = async () => {
  const message = inputMessage.value.trim()
  if (!message || isStreaming.value || !currentConversationId.value) return
  
  // 添加用户消息到列表
  messages.value.push({
    role: 'user',
    content: message
  })
  
  inputMessage.value = ''
  isStreaming.value = true
  streamingContent.value = ''
  
  // 发送消息并接收流式回复
  cancelStream = sendChatMessage(
    currentConversationId.value,
    message,
    // onChunk
    (chunk) => {
      streamingContent.value += chunk
    },
    // onComplete
    () => {
      // 将流式内容添加到消息列表
      if (streamingContent.value) {
        messages.value.push({
          role: 'assistant',
          content: streamingContent.value
        })
      }
      streamingContent.value = ''
      isStreaming.value = false
      cancelStream = null
    },
    // onError
    (err) => {
      ElMessage.error('AI回复出错，请重试')
      isStreaming.value = false
      cancelStream = null
    }
  )
}

// 滚动到底部
const scrollToBottom = () => {
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
  }
}

// 格式化消息（简单的markdown支持）
const formatMessage = (content) => {
  if (!content) return ''
  return content
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/\n/g, '<br>')
    .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
    .replace(/`(.*?)`/g, '<code>$1</code>')
}
</script>

<style lang="scss" scoped>
.chat-container {
  display: flex;
  height: calc(100vh - 96px);
  background: #f7f8fa;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

// 左侧会话列表
.conversation-sidebar {
  width: 260px;
  background: linear-gradient(180deg, #1e293b 0%, #0f172a 100%);
  display: flex;
  flex-direction: column;
  transition: width 0.3s ease;
  
  &.collapsed {
    width: 64px;
    
    .new-chat-btn {
      padding: 10px;
      
      span {
        display: none;
      }
    }
    
    .conv-title,
    .delete-btn {
      display: none;
    }
    
    .conversation-item {
      justify-content: center;
      padding: 12px;
    }
  }
}

.sidebar-header {
  padding: 16px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.new-chat-btn {
  width: 100%;
  height: 44px;
  border-radius: 8px;
  font-size: 14px;
  
  .el-icon {
    margin-right: 6px;
  }
}

.conversation-list {
  flex: 1;
  overflow-y: auto;
  padding: 12px 8px;
  
  // 隐藏滚动条但保持滚动功能
  scrollbar-width: none;  // Firefox
  -ms-overflow-style: none;  // IE 10+
  
  &::-webkit-scrollbar {
    display: none;  // Chrome, Safari, Edge
  }
}

.conversation-item {
  display: flex;
  align-items: center;
  padding: 10px 12px;
  margin-bottom: 4px;
  border-radius: 8px;
  cursor: pointer;
  color: rgba(255, 255, 255, 0.7);
  transition: all 0.2s;
  
  &:hover {
    background: rgba(255, 255, 255, 0.1);
    
    .delete-btn {
      opacity: 1;
    }
  }
  
  &.active {
    background: rgba(255, 255, 255, 0.15);
    color: white;
  }
  
  .conv-icon {
    font-size: 18px;
    flex-shrink: 0;
  }
  
  .conv-title {
    flex: 1;
    margin-left: 10px;
    font-size: 13px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
  
  .delete-btn {
    opacity: 0;
    color: rgba(255, 255, 255, 0.5);
    transition: opacity 0.2s;
    
    &:hover {
      color: #ef4444;
    }
  }
}

.sidebar-footer {
  padding: 12px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  display: flex;
  justify-content: center;
}

.collapse-btn {
  color: rgba(255, 255, 255, 0.6);
  
  &:hover {
    color: white;
  }
}

// 右侧聊天区域
.chat-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: white;
  transition: margin-left 0.3s ease;
}

// 欢迎界面
.welcome-screen {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}

.welcome-content {
  text-align: center;
  max-width: 400px;
  
  .welcome-icon {
    font-size: 64px;
    margin-bottom: 20px;
  }
  
  h1 {
    font-size: 28px;
    color: #1e293b;
    margin-bottom: 12px;
    font-weight: 600;
  }
  
  p {
    color: #64748b;
    font-size: 15px;
    line-height: 1.6;
    margin-bottom: 24px;
  }
}

// 消息区域
.messages-container {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
  
  &::-webkit-scrollbar {
    width: 6px;
  }
  
  &::-webkit-scrollbar-thumb {
    background: #e2e8f0;
    border-radius: 3px;
  }
}

.message-wrapper {
  display: flex;
  gap: 12px;
  margin-bottom: 24px;
  
  &.user {
    flex-direction: row-reverse;
    
    .message-bubble {
      background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);
      color: white;
      border-radius: 16px 16px 4px 16px;
    }
  }
  
  &.assistant {
    .message-bubble {
      background: #f1f5f9;
      color: #1e293b;
      border-radius: 16px 16px 16px 4px;
    }
  }
}

.message-avatar {
  flex-shrink: 0;
  
  .el-avatar {
    background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);
  }
  
  .ai-avatar {
    width: 32px;
    height: 32px;
    border-radius: 50%;
    background: linear-gradient(135deg, #10b981 0%, #059669 100%);
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 18px;
  }
}

.message-content {
  max-width: 70%;
}

.message-bubble {
  padding: 12px 16px;
  font-size: 14px;
  line-height: 1.6;
  word-break: break-word;
  
  &.streaming {
    .typing-cursor {
      animation: blink 1s infinite;
      color: #3b82f6;
      font-weight: bold;
    }
  }
  
  :deep(code) {
    background: rgba(0, 0, 0, 0.1);
    padding: 2px 6px;
    border-radius: 4px;
    font-family: 'Consolas', monospace;
  }
  
  :deep(strong) {
    font-weight: 600;
  }
}

@keyframes blink {
  0%, 50% { opacity: 1; }
  51%, 100% { opacity: 0; }
}

// 输入区域
.input-area {
  padding: 16px 24px 20px;
  background: white;
  border-top: 1px solid #e2e8f0;
}

.input-wrapper {
  display: flex;
  gap: 12px;
  align-items: flex-end;
  
  :deep(.el-textarea__inner) {
    border-radius: 12px;
    padding: 12px 16px;
    font-size: 14px;
    resize: none;
    border: 1px solid #e2e8f0;
    box-shadow: 0 2px 6px rgba(0, 0, 0, 0.04);
    
    &:focus {
      border-color: #3b82f6;
      box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
    }
  }
}

.send-btn {
  height: 44px;
  width: 44px;
  border-radius: 12px;
  flex-shrink: 0;
}

.input-hint {
  text-align: center;
  font-size: 12px;
  color: #94a3b8;
  margin-top: 8px;
}

// Element Plus Empty 组件样式覆盖
:deep(.el-empty) {
  padding: 40px 0;
  
  .el-empty__description {
    color: #94a3b8;
  }
}

// 响应式
@media (max-width: 768px) {
  .conversation-sidebar {
    width: 64px;
    
    .new-chat-btn span,
    .conv-title,
    .delete-btn {
      display: none;
    }
    
    .conversation-item {
      justify-content: center;
      padding: 12px;
    }
  }
  
  .message-content {
    max-width: 85%;
  }
}
</style>
