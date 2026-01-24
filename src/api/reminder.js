import request from '@/utils/request'

/**
 * 获取所有提醒记录（管理员用）
 */
export const list = () => {
    return request.get('/appointment-reminder/list')
}

/**
 * 分页获取当前用户的提醒记录
 * @param {number} pageNo 页码
 * @param {number} pageSize 每页数量
 */
export const page = (pageNo = 1, pageSize = 10) => {
    return request.get('/appointment-reminder/page', {
        params: { pageNo, pageSize }
    })
}

/**
 * 手动触发预约提醒（管理员用）
 * @param {string} date 目标日期，格式：yyyy-MM-dd，不传则默认明天
 */
export const sendReminder = (date) => {
    return request.get('/appointment-reminder/reminder', {
        params: date ? { date } : {}
    })
}
