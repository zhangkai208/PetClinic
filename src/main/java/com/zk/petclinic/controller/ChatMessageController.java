package com.zk.petclinic.controller;


import com.zk.petclinic.domain.ChatConversation;
import com.zk.petclinic.domain.ChatMessage;
import com.zk.petclinic.service.ChatConversationService;
import com.zk.petclinic.service.ChatMessageService;
import com.zk.petclinic.util.ResultUtil;
import com.zk.petclinic.util.ThreadLocalUtil;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;


@RestController
@RequestMapping("/chatMessage")
public class ChatMessageController {
    @Autowired
    private ChatClient chatClient;
    @Autowired
    private ChatMessageService chatMessageService;
    @Autowired
    private ChatConversationService chatConversationService;

    /**
     * 获取当前登录用户ID
     * @return 用户ID，未登录返回null
     */
    private Long getCurrentUserId() {
        String userIdStr = ThreadLocalUtil.get();
        if (userIdStr == null || userIdStr.isEmpty()) {
            return null;
        }
        return Long.valueOf(userIdStr);
    }

    /**
     * 验证会话是否属于指定用户
     * @param conversationId 会话ID
     * @param userId 用户ID
     * @return 会话属于该用户返回true，否则返回false
     */
    private boolean isConversationOwner(Long conversationId, Long userId) {
        ChatConversation conversation = chatConversationService.getConversationById(conversationId);
        return conversation != null && conversation.getUserId().equals(userId);
    }

    @GetMapping(value = "/chat", produces = "text/html;charset=utf-8")
    public Flux<String> saveMessage(@RequestParam String message,
                             @RequestParam Long conversationId) {
        Long userId = getCurrentUserId();
        // 验证会话所有权
        if (userId == null || !isConversationOwner(conversationId, userId)) {
            return Flux.just("无权访问该会话");
        }

        chatMessageService.saveMessage(conversationId, "user", message);

        // 用于收集AI回复
        StringBuilder aiResponse = new StringBuilder();

        return chatClient.prompt()
                .user(message)  // 使用用户传入的message
                .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, String.valueOf(conversationId)))  // 使用用户传入的id
                .stream()
                .content()
                .doOnNext(chunk -> {
                    // 收集AI回复内容
                    aiResponse.append(chunk);
                })
                .doOnComplete(() -> {
                    // 流结束后保存AI回复到数据库
                    if (aiResponse.length() > 0) {
                        chatMessageService.saveMessage(conversationId, "assistant", aiResponse.toString());
                    }
                });
    }

    @GetMapping("/list")
    public ResultUtil<List<ChatMessage>> getChatMessageByConversationId(@RequestParam Long conversationId) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return ResultUtil.fail("请先登录");
        }
        if (!isConversationOwner(conversationId, userId)) {
            return ResultUtil.fail("无权访问该会话");
        }
        return ResultUtil.success(chatMessageService.getChatMessageByConversationId(conversationId));
    }

    @DeleteMapping("/{conversationId}")
    public ResultUtil<String> deleteChatMessageByConversationId(@PathVariable Long conversationId) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return ResultUtil.fail("请先登录");
        }
        if (!isConversationOwner(conversationId, userId)) {
            return ResultUtil.fail("无权删除该会话消息");
        }
        chatMessageService.deleteMessagesByConversationId(conversationId);
        return ResultUtil.success("删除成功");
    }
}
