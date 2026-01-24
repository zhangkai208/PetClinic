package com.zk.petclinic.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zk.petclinic.domain.AppointmentReminder;
import com.zk.petclinic.scheduler.AppointmentReminderScheduler;
import com.zk.petclinic.service.AppointmentReminderService;
import com.zk.petclinic.util.ResultUtil;
import com.zk.petclinic.util.ThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Slf4j
@RestController
@RequestMapping("/appointment-reminder")
public class AppointmentReminderController {
    @Autowired
    private AppointmentReminderService appointmentReminderService;
    @Autowired
    private AppointmentReminderScheduler appointmentReminderScheduler;
    /**
     * 手动触发预约提醒
     *
     */
    @GetMapping("/reminder")
    public ResultUtil<String> reminder(@RequestParam(required = false) String date) {

        try {
            LocalDate targetDate;
            if (date != null && !date.isEmpty()) {
                targetDate = LocalDate.parse(date, DateTimeFormatter.ISO_DATE);
            } else {
                targetDate = LocalDate.now().plusDays(1);
            }
            int count = appointmentReminderScheduler.triggerManually(targetDate);
            log.info("成功发送 {} 条预约提醒", count);
        } catch (Exception e) {
            log.error("发送预约提醒失败", e);
            return ResultUtil.fail("发送预约提醒失败");
        }
        return ResultUtil.success("发送预约提醒成功");
    }
    @GetMapping("/list")
    public ResultUtil<List<AppointmentReminder>> list() {
        List<AppointmentReminder> list = appointmentReminderService.listReminder();
        return ResultUtil.success(list);
    }
    @GetMapping("/page")
    public ResultUtil<Page<AppointmentReminder>> page(@RequestParam(defaultValue = "1") long pageNo,
                                                      @RequestParam(defaultValue = "10") long pageSize) {
        String userIdStr = ThreadLocalUtil.get();
        if (userIdStr == null || userIdStr.isEmpty()) {
            return ResultUtil.fail("请先登录");
        }
        Long userId = Long.valueOf(userIdStr);
        Page<AppointmentReminder> page = appointmentReminderService.pageReminder(pageNo, pageSize, userId);
        return ResultUtil.success(page);
    }
}
