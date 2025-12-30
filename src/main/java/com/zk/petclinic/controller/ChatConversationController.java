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
    @PostMapping("/create")
    public ResultUtil<String> createConversation(@Validated @RequestBody ChatConversation chatConversation){
        String userIdStr = ThreadLocalUtil.get();
        if (userIdStr == null || userIdStr.isEmpty()) {
            return ResultUtil.fail("请先登录");
        }
        chatConversationService.createConversation(chatConversation, Long.valueOf(userIdStr));
        return ResultUtil.success("创建成功");
    }
    @GetMapping
    public ResultUtil<ChatConversation> getConversationById(@RequestParam Long id){
        return ResultUtil.success(chatConversationService.getConversationById(id));
    }
    @GetMapping
    public ResultUtil<List<ChatConversation>> getConversationsByUserId(){
        String userIdStr = ThreadLocalUtil.get();
        if (userIdStr == null || userIdStr.isEmpty()) {
            return ResultUtil.fail("请先登录");
        }
        return ResultUtil.success(chatConversationService.getConversationsByUserId(Long.valueOf(userIdStr)));
    }
    @DeleteMapping
    public ResultUtil<String> deleteConversation(@RequestParam Long id){
        chatConversationService.deleteConversation(id);
        return ResultUtil.success("删除成功");
    }
}
