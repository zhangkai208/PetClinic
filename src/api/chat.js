import request from '@/utils/request'
import { useTokenStore } from '@/stores/token'

// ============ 会话管理 API ============

/**
 * 创建新会话
 * @param {Object} data - 会话信息 {title: string}
 */
export const createConversation = (data) => {
    return request.post('/ChatConversation/create', data)
}

/**
 * 获取当前用户的所有会话列表
 */
export const getConversations = () => {
    return request.get('/ChatConversation/list')
}

/**
 * 根据ID获取单个会话详情
 * @param {number} id - 会话ID
 */
export const getConversationById = (id) => {
    return request.get(`/ChatConversation/${id}`)
}

/**
 * 删除会话
 * @param {number} id - 会话ID
 */
export const deleteConversation = (id) => {
    return request.delete(`/ChatConversation/${id}`)
}

// ============ 消息管理 API ============

/**
 * 获取会话的所有消息列表
 * @param {number} conversationId - 会话ID
 */
export const getMessages = (conversationId) => {
    return request.get('/chatMessage/list', { params: { conversationId } })
}

/**
 * 删除会话下的所有消息
 * @param {number} conversationId - 会话ID
 */
export const deleteMessages = (conversationId) => {
    return request.delete(`/chatMessage/${conversationId}`)
}

/**
 * 发送消息并获取AI流式回复（SSE）
 * @param {number} conversationId - 会话ID
 * @param {string} message - 用户消息
 * @param {function} onChunk - 收到数据块时的回调
 * @param {function} onComplete - 完成时的回调
 * @param {function} onError - 错误时的回调
 * @returns {function} 取消函数
 */
export const sendMessage = (conversationId, message, onChunk, onComplete, onError) => {
    const tokenStore = useTokenStore()
    const controller = new AbortController()

    const url = `/api/chatMessage/chat?conversationId=${conversationId}&message=${encodeURIComponent(message)}`

    fetch(url, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${tokenStore.token}`,
            'Accept': 'text/html'
        },
        signal: controller.signal
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('网络请求失败')
            }

            const reader = response.body.getReader()
            const decoder = new TextDecoder('utf-8')

            const read = () => {
                reader.read().then(({ done, value }) => {
                    if (done) {
                        onComplete && onComplete()
                        return
                    }

                    const chunk = decoder.decode(value, { stream: true })
                    onChunk && onChunk(chunk)
                    read()
                }).catch(err => {
                    if (err.name !== 'AbortError') {
                        onError && onError(err)
                    }
                })
            }

            read()
        })
        .catch(err => {
            if (err.name !== 'AbortError') {
                onError && onError(err)
            }
        })

    // 返回取消函数
    return () => controller.abort()
}
