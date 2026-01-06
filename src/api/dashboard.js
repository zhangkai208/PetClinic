import request from '@/utils/request'

/**
 * 获取看板统计数据
 */
export function getStats() {
  return request.get('/admin/dashboard/stats')
}

/**
 * 获取宠物类型分布
 */
export function getPetTypeDistribution() {
  return request.get('/admin/dashboard/pet-type-distribution')
}

/**
 * 获取预约趋势
 */
export function getAppointmentTrend() {
  return request.get('/admin/dashboard/appointment-trend')
}

/**
 * 获取用户角色分布
 */
export function getUserRoleDistribution() {
  return request.get('/admin/dashboard/user-role-distribution')
}
