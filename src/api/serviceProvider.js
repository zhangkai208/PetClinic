import request from '@/utils/request'

// 分页查询服务商（当前用户的）
export const page = (pageNo = 1, pageSize = 10) => {
    return request.get('/serviceProviders/page', { params: { pageNo, pageSize } })
}

// 获取全部服务商（仅管理员可用）
export const list = () => {
    return request.get('/serviceProviders/list')
}

// 创建服务商
export const create = (data) => {
    return request.post('/serviceProviders/create', data)
}

// 更新服务商
export const update = (data) => {
    return request.put('/serviceProviders/update', data)
}

// 批量删除服务商
export const deleteBatch = (ids) => {
    return request.delete('/serviceProviders', { data: ids })
}
