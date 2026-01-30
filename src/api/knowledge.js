import request from '@/utils/request'

/**
 * 添加单条知识
 * @param {string} content 知识内容
 */
export function addKnowledge(content) {
    return request.post('/knowledge/add', { content })
}

/**
 * 批量添加知识
 * @param {string[]} contents 知识内容列表
 */
export function batchAddKnowledge(contents) {
    return request.post('/knowledge/batch-add', contents)
}

/**
 * 搜索知识
 * @param {string} query 搜索关键词
 * @param {number} topK 返回结果数量
 */
export function searchKnowledge(query, topK = 5) {
    return request.get('/knowledge/search', { params: { query, topK } })
}

/**
 * 列出全部知识
 * @param {number} limit 返回数量限制
 */
export function listKnowledge(limit = 100) {
    return request.get('/knowledge/list', { params: { limit } })
}

/**
 * 删除知识
 * @param {string[]} documentIds 文档ID列表
 */
export function deleteKnowledge(documentIds) {
    return request.delete('/knowledge/delete', { data: documentIds })
}
