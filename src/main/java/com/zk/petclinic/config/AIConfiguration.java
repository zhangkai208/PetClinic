package com.zk.petclinic.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class AIConfiguration {
    
    @Bean
    public ChatClient chatClient(ChatModel chatModel) {
        return ChatClient.builder(chatModel)
                .defaultSystem("你是一个专业的宠物诊所助手，可以回答关于宠物健康、宠物护理、预约挂号等相关问题。请用友好专业的语气回复用户。")
                .build();
    }
}
