package com.zk.petclinic.scheduler;

import com.zk.petclinic.service.AppointmentReminderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * 预约提醒定时任务调度器
 */
@Slf4j
@Component
public class AppointmentReminderScheduler {
    @Autowired
    private AppointmentReminderService appointmentReminderService;
    /**
     * 是否启用提醒功能（从配置读取）
     */
    @Value("${app.mail.reminder.enabled:true}")
    private boolean reminderEnabled;
    @Scheduled(cron = "0 0 9 * * ?")
    public void sendDailyReminders() {
        if(!reminderEnabled) {
            log.info("预约提醒功能已禁用，跳过执行");
            return;
        }
        log.info("========== 开始执行每日预约提醒任务 ==========");
        try {
            // 发送明天的预约提醒
            LocalDate tomorrow = LocalDate.now().plusDays(1);
            int count = appointmentReminderService.sendRemindersForDate(tomorrow);
            log.info("========== 预约提醒任务完成，成功发送 {} 条 ==========", count);
        }catch (Exception e) {
            log.error("预约提醒任务执行异常", e);
        }
    }
    /**
     * 手动触发测试方法
     * 可以通过Controller调用此方法进行测试
     */
    public int triggerManually(LocalDate targetDate) {
        log.info("手动触发预约提醒：targetDate={}", targetDate);
        return appointmentReminderService.sendRemindersForDate(targetDate);
    }
}
