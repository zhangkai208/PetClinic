package com.zk.petclinic.service.impl;

import com.zk.petclinic.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService {
    @Autowired
    private JavaMailSender javaMailSender;
    /**
     * 发件人邮箱（从配置读取）
     */
    @Value("${spring.mail.username}")
    private String fromEmail;

    /**
     * 发件人显示名称
     */
    @Value("${app.mail.reminder.sender-name:宠物健康管理系统}")
    private String senderName;

    @Override
    public boolean sendSimpleMail(String to, String subject, String content) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(senderName + " <" + fromEmail + ">");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        javaMailSender.send(message);
        log.info("简单邮件发送成功：to={}, subject={}", to, subject);
        return true;
        }
        catch (Exception e){
            log.error("简单邮件发送失败：to={}, subject={}, error={}", to, subject, e.getMessage());
            return false;
        }
    }
}
