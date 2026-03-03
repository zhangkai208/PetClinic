package com.zk.petclinic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zk.petclinic.domain.ChatMessage;
import com.zk.petclinic.service.ChatMessageService;
import com.zk.petclinic.mapper.ChatMessageMapper;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
* @author 张恺
* @description 针对表【chat_message(消息表)】的数据库操作Service实现
* @createDate 2025-12-30 11:26:56
*/
@Service
public class ChatMessageServiceImpl extends ServiceImpl<ChatMessageMapper, ChatMessage>
    implements ChatMessageService{


    /**
     * 根据会话ID获取聊天消息列表
     * @param conversationId 会话ID
     * @return 返回按创建时间升序排列的消息列表
     */
    @Override
    public List<ChatMessage> getChatMessageByConversationId(Long conversationId) {
        // 创建Lambda查询构造器
        LambdaQueryWrapper<ChatMessage> queryWrapper = new LambdaQueryWrapper<>();
        // 设置查询条件：会话ID相等，并按创建时间升序排列
        queryWrapper.eq(ChatMessage::getConversationId, conversationId)
                .orderByAsc(ChatMessage::getCreatedAt);
        // 执行查询并返回结果
        return this.list(queryWrapper);
    }

    /**
     * 保存聊天消息
     * @param conversationId 会话ID
     * @param role 消息发送者角色
     * @param content 消息内容
     * @return 返回保存后的消息对象
     */
    @Override
    public ChatMessage saveMessage(Long conversationId, String role, String content,
                                   String messageType, String mediaUrl, String extraJson) {
        // 创建新的消息对象
        ChatMessage message = new ChatMessage();
        // 设置消息属性
        message.setConversationId(conversationId);
        message.setRole(role);
        message.setContent(content);
        message.setMessageType(messageType);
        message.setMediaUrl(mediaUrl);
        message.setExtraJson(extraJson);
        message.setCreatedAt(new Date());
        // 保存消息到数据库
        this.save(message);
        // 返回保存后的消息对象
        return message;
    }

    @Override
    public ChatMessage saveMessage(Long conversationId, String role, String content) {
        return saveMessage(conversationId, role, content, "text", null, null);
    }

    /**
     * 根据会话ID删除聊天消息
     * @param conversationId 会话ID
     */
    @Override
    public void deleteMessagesByConversationId(Long conversationId) {
        // 根据会话ID删除消息
        LambdaQueryWrapper<ChatMessage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChatMessage::getConversationId, conversationId);
        this.remove(queryWrapper);
    }
}




