package com.zk.petclinic.controller;


import com.zk.petclinic.domain.ChatMessage;
import com.zk.petclinic.service.ChatMessageService;
import com.zk.petclinic.util.ResultUtil;
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
    @GetMapping(value = "/saveMessage", produces = "text/html;charset=utf-8")
    public Flux<String> saveMessage(@RequestParam String message,
                             @RequestParam Long conversationId) {

        chatMessageService.saveMessage(conversationId,"用户" ,message);

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
                        chatMessageService.saveMessage(conversationId,"AI助手" ,aiResponse.toString());
                    }
                });
    }
    @GetMapping
    public ResultUtil<List<ChatMessage>> getChatMessageByConversationId(@RequestParam Long conversationId) {
        return ResultUtil.success(chatMessageService.getChatMessageByConversationId(conversationId));
    }
    @DeleteMapping
    public ResultUtil<String> deleteChatMessageByConversationId(@RequestParam Long conversationId) {
        chatMessageService.deleteMessagesByConversationId(conversationId);
        return ResultUtil.success("删除成功");
    }
}
