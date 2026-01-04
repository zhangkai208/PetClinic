import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useTokenStore } from '@/stores/token'
import router from '@/router'

const request = axios.create({
  baseURL: '/api'
})

request.interceptors.request.use((config) => {
  const tokenStore = useTokenStore()
  if (tokenStore.token) {
    config.headers.Authorization = `Bearer ${tokenStore.token}`
  }
  return config
})

request.interceptors.response.use(
  (res) => {
    const data = res.data
    if (data && typeof data.code !== 'undefined' && data.code !== 200) {
      ElMessage.error(data.message || '请求失败')
      return Promise.reject(new Error(data.message || '请求失败'))
    }
    return data ?? res
  },
  (err) => {
    // 处理401未授权错误
    if (err.response?.status === 401) {
      const tokenStore = useTokenStore()
      tokenStore.removeToken()
      router.push('/login')
      ElMessage.error('登录已过期，请重新登录')
    } else {
      ElMessage.error(err?.response?.data?.message || err.message || '网络错误')
    }
    return Promise.reject(err)
  }
)

export default request
