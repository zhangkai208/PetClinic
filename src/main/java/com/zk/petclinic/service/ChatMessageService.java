package com.zk.petclinic.service;

import com.zk.petclinic.domain.ChatMessage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author 张恺
* @description 针对表【chat_message(消息表)】的数据库操作Service
* @createDate 2025-12-30 11:26:56
*/
public interface ChatMessageService extends IService<ChatMessage> {
    // 根据会话ID获取聊天消息列表
    List<ChatMessage> getChatMessageByConversationId(Long conversationId);
    
    // 保存消息方法
    ChatMessage saveMessage(Long conversationId, String role, String content);
    
    // 根据会话ID删除该会话下的消息
    void deleteMessagesByConversationId(Long conversationId);
}
