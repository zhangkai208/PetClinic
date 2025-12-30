package com.zk.petclinic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zk.petclinic.domain.ChatConversation;
import com.zk.petclinic.mapper.ChatMessageMapper;
import com.zk.petclinic.service.ChatConversationService;
import com.zk.petclinic.mapper.ChatConversationMapper;
import com.zk.petclinic.service.ChatMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
* @author 张恺
* @description 针对表【chat_conversation(会话表)】的数据库操作Service实现
* @createDate 2025-12-30 11:26:53
*/
@Service
public class ChatConversationServiceImpl extends ServiceImpl<ChatConversationMapper, ChatConversation>
    implements ChatConversationService{

    @Autowired
    private ChatMessageService chatMessageService;
    /**
     * 创建新的会话
     * @param conversation 会话信息实体
     * @param userId 用户ID
     * @return 创建后的会话实体
     */
    @Override
    public ChatConversation createConversation(ChatConversation conversation,Long userId) {
        // 设置用户ID
        conversation.setUserId(userId);
        // 设置创建和更新时间
        conversation.setCreatedAt(new Date());
        conversation.setUpdatedAt(new Date());
        // 保存会话信息
        this.save(conversation);
        // 返回创建的会话
        return conversation;
    }

    /**
     * 根据ID获取会话信息
     * @param conversationId 会话ID
     * @return 会话信息实体
     */
    @Override
    public ChatConversation getConversationById(Long conversationId) {
        // 通过ID获取会话信息
        return this.getById(conversationId);
    }

    /**
     * 获取指定用户的所有会话列表
     * @param userId 用户ID
     * @return 会话列表
     */
    @Override
    public List<ChatConversation> getConversationsByUserId(Long userId) {
        // 创建查询条件构造器
        LambdaQueryWrapper<ChatConversation> queryWrapper = new LambdaQueryWrapper<>();
        // 设置查询条件：用户ID等于传入的userId
        queryWrapper.eq(ChatConversation::getUserId, userId);
        // 执行查询并返回结果列表
        return this.list(queryWrapper);
    }

    /**
     * 删除指定ID的会话
     * @param conversationId 会话ID
     */
    @Override
    public void deleteConversation(Long conversationId) {
        chatMessageService.deleteMessagesByConversationId(conversationId);
        // 根据ID删除会话
        this.removeById(conversationId);
    }
}




