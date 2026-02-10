package com.zk.petclinic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zk.petclinic.domain.*;
import com.zk.petclinic.service.*;
import com.zk.petclinic.mapper.AppointmentReminderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static com.zk.petclinic.enums.AppointmentStatus.Booked;
import static com.zk.petclinic.enums.AppointmentStatus.Pending_Confirmation;

/**
 * @author 张恺
 * @description 针对表【appointment_reminder(预约提醒发送记录表)】的数据库操作Service实现
 * @createDate 2026-01-23 15:00:58
 */
@Slf4j
@Service
public class AppointmentReminderServiceImpl extends ServiceImpl<AppointmentReminderMapper, AppointmentReminder>
        implements AppointmentReminderService {
    @Autowired
    private AppointmentService appointmentService;
    @Autowired
    private PetService petService;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private ServiceProviderService serviceProviderService;
    @Autowired
    private EmailService emailService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int sendRemindersForDate(LocalDate targetDate) {
        log.info("发送预约提醒，目标日期：{}", targetDate);
        // 计算目标日期的时间范围（当天0点到23:59:59）
        LocalDateTime startOfDay = targetDate.atStartOfDay();
        LocalDateTime endOfDay = targetDate.atTime(LocalTime.MAX);
        Date startDate = Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(endOfDay.atZone(ZoneId.systemDefault()).toInstant());
        // 查询目标日期的有效预约（待确认或已预约状态）
        LambdaQueryWrapper<Appointment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Appointment::getStatus, Pending_Confirmation, Booked)// 状态为待确认或已预约
                .between(Appointment::getAppointmentTime, startDate, endDate);// 预约时间在目标日期范围内
        List<Appointment> appointments = appointmentService.list(queryWrapper);
        log.info("查询到{}个预约", appointments.size());
        int successCount = 0;
        // 遍历发送提醒
        for (Appointment appointment : appointments) {
            try {
                Pet pet = petService.getById(appointment.getPetId());
                if (pet == null) {
                    log.warn("预约ID={} 的宠物不存在，跳过", appointment.getId());
                    continue;
                }
                SysUser owner = sysUserService.getById(pet.getOwnerId());
                ServiceProvider serviceProvider = serviceProviderService.getById(appointment.getProviderId());
                String providerName = (serviceProvider != null) ? serviceProvider.getName() : "未知服务商";

                // ========== 1. 发送给宠物主人 ==========
                if (owner != null && owner.getEmail() != null && !owner.getEmail().isEmpty()) {
                    // 检查该用户是否已经收到过此预约的提醒
                    if (hasBeenSentToUser(appointment.getId(), owner.getId(), appointment.getAppointmentTime())) {
                        log.debug("预约ID={} 已发送过给宠物主人userID={}，跳过", appointment.getId(), owner.getId());
                    } else {
                        String ownerSubject = "🐾 宠物预约提醒 - 明天有服务预约";
                        String ownerContent = buildOwnerEmailContent(owner, pet, appointment, providerName);
                        boolean ownerSuccess = emailService.sendSimpleMail(owner.getEmail(), ownerSubject,
                                ownerContent);
                        // 记录发送结果（宠物主人）
                        saveReminderRecord(appointment.getId(), owner.getId(), owner.getEmail(),
                                ownerSuccess, ownerSuccess ? null : "邮件发送失败", appointment.getAppointmentTime());
                        if (ownerSuccess) {
                            successCount++;
                            log.info("宠物主人提醒发送成功：appointmentId={}, email={}", appointment.getId(), owner.getEmail());
                        }
                    }
                } else {
                    log.warn("预约ID={} 的主人不存在或未设置邮箱，跳过主人通知", appointment.getId());
                }

                // ========== 2. 发送给服务商 ==========
                if (serviceProvider != null && serviceProvider.getUserId() != null) {
                    SysUser providerUser = sysUserService.getById(serviceProvider.getUserId());
                    if (providerUser != null && providerUser.getEmail() != null && !providerUser.getEmail().isEmpty()) {
                        // 检查该服务商是否已经收到过此预约的提醒
                        if (hasBeenSentToUser(appointment.getId(), providerUser.getId(),
                                appointment.getAppointmentTime())) {
                            log.debug("预约ID={} 已发送过给服务商userID={}，跳过", appointment.getId(), providerUser.getId());
                        } else {
                            String providerSubject = "🏥 服务预约提醒 - 明天有客户预约";
                            String providerContent = buildProviderEmailContent(providerUser, pet, owner, appointment,
                                    serviceProvider);
                            boolean providerSuccess = emailService.sendSimpleMail(providerUser.getEmail(),
                                    providerSubject, providerContent);
                            // 记录发送结果（服务商）
                            saveReminderRecord(appointment.getId(), providerUser.getId(), providerUser.getEmail(),
                                    providerSuccess, providerSuccess ? null : "邮件发送失败",
                                    appointment.getAppointmentTime());
                            if (providerSuccess) {
                                successCount++;
                                log.info("服务商提醒发送成功：appointmentId={}, email={}", appointment.getId(),
                                        providerUser.getEmail());
                            } else {
                                log.warn("服务商提醒发送失败：appointmentId={}, email={}", appointment.getId(),
                                        providerUser.getEmail());
                            }
                        }
                    } else {
                        log.warn("预约ID={} 的服务商用户不存在或未设置邮箱，跳过服务商通知", appointment.getId());
                    }
                }

            } catch (Exception e) {
                log.error("发送预约提醒异常：appointmentId={}, error={}", appointment.getId(), e.getMessage());
                try {
                    Pet pet = petService.getById(appointment.getPetId());
                    if (pet != null) {
                        SysUser user = sysUserService.getById(pet.getOwnerId());
                        if (user != null) {
                            saveReminderRecord(appointment.getId(), user.getId(),
                                    user.getEmail(), false, e.getMessage(), appointment.getAppointmentTime());
                        }
                    }
                } catch (Exception e1) {
                    log.error("记录发送结果异常：appointmentId={}, error={}", appointment.getId(), e1.getMessage());
                }
            }
        }
        log.info("预约提醒发送完成，成功：{}/{}", successCount, appointments.size() * 2);
        return successCount;
    }

    // 记录发送结果
    private void saveReminderRecord(Long id, Long userid, String email, boolean success, String errorMessage,
            Date appointmentTime) {
        AppointmentReminder reminder = new AppointmentReminder();
        reminder.setAppointmentId(id);
        reminder.setUserId(userid);
        reminder.setEmail(email);
        reminder.setStatus(success ? 1 : 0);
        reminder.setErrorMessage(errorMessage);
        reminder.setSendTime(new Date());
        reminder.setAppointmentTime(appointmentTime); // 保存预约时间
        this.save(reminder);
    }

    // 构建宠物主人邮件内容
    private String buildOwnerEmailContent(SysUser owner, Pet pet, Appointment appointment, String providerName) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        String appointmentTimeStr = sdf.format(appointment.getAppointmentTime());
        StringBuilder sb = new StringBuilder();
        sb.append("亲爱的 ").append(owner.getNickname() != null ? owner.getNickname() : owner.getUsername())
                .append("：\n\n");
        sb.append("您好！温馨提醒您，您的宠物明天有一个预约服务：\n\n");
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        sb.append("🐾 宠物名字：").append(pet.getName()).append("\n");
        sb.append("📅 预约时间：").append(appointmentTimeStr).append("\n");
        sb.append("🏥 服务类型：").append(appointment.getServiceType() != null ? appointment.getServiceType() : "常规服务")
                .append("\n");
        sb.append("👨‍⚕️ 服务商：").append(providerName).append("\n");
        if (appointment.getMoney() != null && appointment.getMoney() > 0) {
            sb.append("💰 预约金额：").append(appointment.getMoney()).append(" 元\n");
        }
        if (appointment.getRemark() != null && !appointment.getRemark().isEmpty()) {
            sb.append("📝 备注信息：").append(appointment.getRemark()).append("\n");
        }
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");
        sb.append("请提前做好准备，按时赴约哦！\n\n");
        sb.append("如需修改或取消预约，请登录系统操作。\n\n");
        sb.append("祝您和爱宠健康快乐！\n\n");
        sb.append("—— 宠物健康管理系统\n");
        return sb.toString();
    }

    // 构建服务商邮件内容
    private String buildProviderEmailContent(SysUser providerUser, Pet pet, SysUser owner,
            Appointment appointment, ServiceProvider serviceProvider) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        String appointmentTimeStr = sdf.format(appointment.getAppointmentTime());
        String ownerName = (owner != null) ? (owner.getNickname() != null ? owner.getNickname() : owner.getUsername())
                : "未知客户";
        String ownerPhone = (owner != null && owner.getPhone() != null) ? owner.getPhone() : "未提供";

        StringBuilder sb = new StringBuilder();
        sb.append("尊敬的 ").append(serviceProvider.getName()).append("：\n\n");
        sb.append("您好！温馨提醒您，明天有一位客户预约了您的服务：\n\n");
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        sb.append("👤 客户姓名：").append(ownerName).append("\n");
        sb.append("📞 联系电话：").append(ownerPhone).append("\n");
        sb.append("🐾 宠物名字：").append(pet.getName()).append("\n");
        sb.append("🐕 宠物类型：").append(pet.getType() != null ? pet.getType() : "未知").append("\n");
        if (pet.getBreed() != null && !pet.getBreed().isEmpty()) {
            sb.append("📋 宠物品种：").append(pet.getBreed()).append("\n");
        }
        sb.append("📅 预约时间：").append(appointmentTimeStr).append("\n");
        sb.append("🏥 服务类型：").append(appointment.getServiceType() != null ? appointment.getServiceType() : "常规服务")
                .append("\n");
        if (appointment.getMoney() != null && appointment.getMoney() > 0) {
            sb.append("💰 预约金额：").append(appointment.getMoney()).append(" 元\n");
        }
        if (appointment.getRemark() != null && !appointment.getRemark().isEmpty()) {
            sb.append("📝 备注信息：").append(appointment.getRemark()).append("\n");
        }
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");
        sb.append("请提前做好准备，为客户提供优质服务！\n\n");
        sb.append("如有变动，请及时登录系统查看或联系客户。\n\n");
        sb.append("感谢您的辛勤付出！\n\n");
        sb.append("—— 宠物健康管理系统\n");
        return sb.toString();
    }

    @Override
    public boolean hasBeenSent(Long appointmentId) {
        // 查询是否已经发送过提醒（整个预约）
        LambdaQueryWrapper<AppointmentReminder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AppointmentReminder::getAppointmentId, appointmentId);
        return count(queryWrapper) > 0;
    }

    /**
     * 检查某个用户是否已经收到过某个预约的提醒（针对当前预约时间）
     * 
     * @param appointmentId   预约ID
     * @param userId          用户ID
     * @param appointmentTime 当前预约时间
     * @return 如果已针对该预约时间发送过提醒则返回true，否则返回false
     */
    private boolean hasBeenSentToUser(Long appointmentId, Long userId, Date appointmentTime) {
        LambdaQueryWrapper<AppointmentReminder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AppointmentReminder::getAppointmentId, appointmentId)
                .eq(AppointmentReminder::getUserId, userId)
                .eq(AppointmentReminder::getAppointmentTime, appointmentTime); // 增加预约时间判断
        return count(queryWrapper) > 0;
    }

    @Override
    public List<AppointmentReminder> listReminder() {
        return this.list();
    }

    @Override
    public Page<AppointmentReminder> pageReminder(long pageNo, long pageSize, Long userId) {
        Page<AppointmentReminder> page = new Page<>(pageNo, pageSize);
        LambdaQueryWrapper<AppointmentReminder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AppointmentReminder::getUserId, userId);
        return this.page(page, queryWrapper);
    }
}
