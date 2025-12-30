package com.zk.petclinic.service;

import com.zk.petclinic.domain.ChatConversation;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author 张恺
* @description 针对表【chat_conversation(会话表)】的数据库操作Service
* @createDate 2025-12-30 11:26:53
*/
public interface ChatConversationService extends IService<ChatConversation> {
    // 创建新会话的方法（需要userId关联用户）
    ChatConversation createConversation(ChatConversation conversation,Long userId);
    
    // 根据会话ID获取单个会话详情
    ChatConversation getConversationById(Long conversationId);
    
    // 根据用户ID获取该用户的所有会话列表
    List<ChatConversation> getConversationsByUserId(Long userId);
    
    // 根据会话ID删除会话
    void deleteConversation(Long conversationId);

}
