import request from '@/utils/request'

// 用户注册
export const register = (data) => {
    return request.post('/sysUser/register', data)
}

// 用户登录
export const login = (data) => {
    return request.post('/sysUser/login', data)
}

// 用户登出
export const logout = () => {
    return request.post('/sysUser/logout')
}

// 根据ID获取用户信息
export const getById = (id) => {
    return request.get(`/sysUser/${id}`)
}

// 分页查询用户
export const page = (pageNo = 1, pageSize = 10) => {
    return request.get('/sysUser/page', { params: { pageNo, pageSize } })
}

// 更新用户信息
export const update = (id, data) => {
    return request.put(`/sysUser/${id}`, data)
}

// 删除单个用户
export const deleteUser = (id) => {
    return request.delete(`/sysUser/${id}`)
}

// 批量删除用户
export const deleteBatch = (ids) => {
    return request.delete('/sysUser', { data: ids })
}

// 新增用户（后台管理）
export const create = (data) => {
    return request.post('/sysUser/create', data)
}

// 上传头像
export const upload = (file) => {
    const formData = new FormData()
    formData.append('file', file)
    return request.post('/sysUser/upload', formData, {
        headers: { 'Content-Type': 'multipart/form-data' }
    })
}