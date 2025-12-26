import request from '@/utils/request'

// 分页查询宠物
export const page = (pageNo = 1, pageSize = 10) => {
    return request.get('/pet/page', { params: { pageNo, pageSize } })
}

// 新增宠物
export const create = (data) => {
    return request.post('/pet/create', data)
}

// 更新宠物信息
export const update = (id, data) => {
    return request.put(`/pet/${id}`, data)
}

// 删除单个宠物
export const deletePet = (id) => {
    return request.delete(`/pet/${id}`)
}

// 批量删除宠物
export const deleteBatch = (ids) => {
    return request.delete('/pet', { data: ids })
}

// 上传宠物头像
export const upload = (file) => {
    const formData = new FormData()
    formData.append('file', file)
    return request.post('/pet/upload', formData, {
        headers: { 'Content-Type': 'multipart/form-data' }
    })
}