package com.zk.petclinic.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zk.petclinic.domain.AppointmentReminder;
import com.baomidou.mybatisplus.extension.service.IService;

import java.time.LocalDate;
import java.util.List;

/**
* @author 张恺
* @description 针对表【appointment_reminder(预约提醒发送记录表)】的数据库操作Service
* @createDate 2026-01-23 15:00:58
*/
public interface AppointmentReminderService extends IService<AppointmentReminder> {
    /**
     * 发送指定日期的预约提醒
     *
     * @param targetDate 目标日期（预约日期）
     * @return 成功发送的数量
     */
    int sendRemindersForDate(LocalDate targetDate);

    /**
     * 检查某个预约是否已发送过提醒
     *
     * @param appointmentId 预约ID
     * @return 是否已发送
     */
    boolean hasBeenSent(Long appointmentId);

    List<AppointmentReminder> listReminder();

    Page<AppointmentReminder> pageReminder(long pageNo, long pageSize, Long userId);
}
