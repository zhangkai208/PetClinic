import request from '@/utils/request'

// 分页查询预约
export const page = (petId, providerId, pageNo = 1, pageSize = 10) => {
    return request.get('/appointment/page', { 
        params: { petId, providerId, pageNo, pageSize } 
    })
}

// 创建预约
export const create = (petId, providerId, data) => {
    return request.post('/appointment/create', data, { 
        params: { petId, providerId } 
    })
}

// 更新预约
export const update = (id, data) => {
    return request.put(`/appointment/${id}`, data)
}

// 批量删除预约
export const deleteBatch = (ids) => {
    return request.delete('/appointment/delete', { data: ids })
}

// 添加评价
export const addEvaluation = (id, evaluation) => {
    return request.put('/appointment/evaluation', evaluation, {
        params: { id },
        headers: { 'Content-Type': 'text/plain' }
    })
}
