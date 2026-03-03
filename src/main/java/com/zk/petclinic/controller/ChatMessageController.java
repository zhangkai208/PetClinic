package com.zk.petclinic.controller;


import com.zk.petclinic.domain.ChatConversation;
import com.zk.petclinic.domain.ChatMessage;
import com.zk.petclinic.domain.SysUser;
import com.zk.petclinic.domain.dto.IntentResult;
import com.zk.petclinic.service.ChatConversationService;
import com.zk.petclinic.service.ChatMessageService;
import com.zk.petclinic.service.SysUserService;
import com.zk.petclinic.enums.SysUserRoleType;
import com.zk.petclinic.service.impl.ChatAiServiceImpl;
import com.zk.petclinic.util.ResultUtil;
import com.zk.petclinic.util.ThreadLocalUtil;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private ChatAiServiceImpl chatAiService;
    private static final String IMAGE_PREFIX = "__IMAGE__";

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

        // 先存用户消息
        chatMessageService.saveMessage(conversationId, "user", message, "text", null, null);

        // LLM 判断是否图片请求
        IntentResult intent = chatAiService.detectIntent(message);
        if (intent.image) {
            String prompt = intent.prompt;
            if (prompt == null || prompt.isBlank()) {
                prompt = message;
            }
            String finalPrompt = prompt;

            // 图片分支：生成图 -> 存 image 消息 -> 返回给前端协议
            return Mono.fromCallable(() -> {
                String imageUrl = chatAiService.generateImageAndGetUrl(finalPrompt, conversationId);
                String extraJson = chatAiService.buildImageExtraJson(finalPrompt);
                chatMessageService.saveMessage(
                        conversationId,
                        "assistant",
                        "我为你生成了一张图片",
                        "image",
                        imageUrl,
                        extraJson
                );
                return IMAGE_PREFIX + imageUrl + "||petclinic-image.png";
            }).onErrorReturn("图片生成失败，请稍后重试")
                    .flux();
        }

        // 用于收集AI回复
        StringBuilder aiResponse = new StringBuilder();

        // 获取当前用户角色类型
        SysUser currentUser = sysUserService.getById(userId);
        Integer roleType = currentUser != null ? currentUser.getRoleType() : SysUserRoleType.OWNER.getValue();
        SysUserRoleType role = SysUserRoleType.getEnumByValue(roleType);
        String roleName = role != null ? role.getLabel() : "未知";
        boolean isAdmin = role == SysUserRoleType.ADMIN;

        // 将用户ID和角色注入到消息中，让AI知道当前用户身份和权限
        String enhancedMessage = String.format(
                "[系统信息: 当前用户ID=%d, 角色=%s, 是否管理员=%s] 用户问题: %s",
                userId,
                roleName,
                isAdmin ? "是(可查询所有用户数据)" : "否(只能查询自己的数据)",
                message
        );

        return chatClient.prompt()
                .user(enhancedMessage)  // 使用增强后的消息
                .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, String.valueOf(conversationId)))
                .stream()
                .content()
                .doOnNext(chunk -> {
                    // 收集AI回复内容
                    aiResponse.append(chunk);
                })
                .doOnComplete(() -> {
                    // 流结束后保存AI回复到数据库
                    if (aiResponse.length() > 0) {
                        chatMessageService.saveMessage(
                                conversationId,
                                "assistant",
                                aiResponse.toString(),
                                "text",
                                null,
                                null
                        );
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

