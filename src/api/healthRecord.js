import request from '@/utils/request'

// 分页查询健康记录（按宠物ID）
export const page = (petId, pageNo = 1, pageSize = 10) => {
    return request.get('/healthRecord/page', { params: { petId, pageNo, pageSize } })
}

// 创建健康记录
export const create = (petId, data) => {
    return request.post('/healthRecord/create', data, { params: { petId } })
}

// 更新健康记录
export const update = (id, data) => {
    return request.put(`/healthRecord/${id}`, data)
}

// 批量删除健康记录
export const deleteBatch = (ids) => {
    return request.delete('/healthRecord/delete', { data: ids })
}
