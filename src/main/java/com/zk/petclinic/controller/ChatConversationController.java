package com.zk.petclinic.controller;

import com.zk.petclinic.domain.ChatConversation;
import com.zk.petclinic.service.ChatConversationService;
import com.zk.petclinic.util.ResultUtil;
import com.zk.petclinic.util.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ChatConversation")
public class ChatConversationController {
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

    @PostMapping("/create")
    public ResultUtil<String> createConversation(@Validated @RequestBody ChatConversation chatConversation){
        Long userId = getCurrentUserId();
        if (userId == null) {
            return ResultUtil.fail("请先登录");
        }
        chatConversationService.createConversation(chatConversation, userId);
        return ResultUtil.success("创建成功");
    }

    @GetMapping("/{id}")
    public ResultUtil<ChatConversation> getConversationById(@PathVariable Long id){
        Long userId = getCurrentUserId();
        if (userId == null) {
            return ResultUtil.fail("请先登录");
        }
        ChatConversation conversation = chatConversationService.getConversationById(id);
        if (conversation == null) {
            return ResultUtil.fail("会话不存在");
        }
        if (!conversation.getUserId().equals(userId)) {
            return ResultUtil.fail("无权访问该会话");
        }
        return ResultUtil.success(conversation);
    }

    @GetMapping("/list")
    public ResultUtil<List<ChatConversation>> getConversationsByUserId(){
        Long userId = getCurrentUserId();
        if (userId == null) {
            return ResultUtil.fail("请先登录");
        }
        List<ChatConversation> conversations = chatConversationService.getConversationsByUserId(userId);
        return ResultUtil.success(conversations);
    }

    @DeleteMapping("/{id}")
    public ResultUtil<String> deleteConversation(@PathVariable Long id){
        Long userId = getCurrentUserId();
        if (userId == null) {
            return ResultUtil.fail("请先登录");
        }
        if (!isConversationOwner(id, userId)) {
            return ResultUtil.fail("无权删除该会话");
        }
        chatConversationService.deleteConversation(id);
        return ResultUtil.success("删除成功");
    }
}
