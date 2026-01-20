package com.zk.petclinic.config;

import com.zk.petclinic.mcp.PetClinicTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class AIConfiguration implements WebMvcConfigurer {
    @Bean
    public ChatMemory chatMemory(){
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(new InMemoryChatMemoryRepository())
                .maxMessages(20)  // 保留最近20条消息
                .build();
    }

    @Bean
    public ChatClient chatClient(ChatModel chatModel,
                                 ChatMemory chatMemory,
                                 PetClinicTools petClinicTools) {
        return ChatClient.builder(chatModel)
                .defaultSystem("""
                    你是一个专业的宠物诊所助手，可以回答关于宠物健康、宠物护理、预约挂号等相关问题。
                    请用友好专业的语气回复用户。你的创造者是一个名字叫张恺的男生。
                    
                    你可以使用以下工具来获取数据库中的真实信息：
                    - get_user_pets: 根据用户ID查询宠物列表
                    - get_pet_health_records: 根据宠物ID查询健康记录
                    - get_pet_appointments: 根据宠物ID查询预约记录
                    - search_pets: 根据关键词搜索宠物
                    - search_users: 根据关键词搜索用户
                    - count_pets_by_type: 统计各类型宠物数量
                    
                    重要规则：
                    - 每条消息开头的[系统信息]包含当前用户的ID和角色
                    - 如果用户是管理员，可以使用任意用户ID查询数据
                    - 如果用户不是管理员，只能使用他们自己的用户ID查询数据
                    - 当普通用户问"我的宠物"时，使用[系统信息]中的用户ID调用get_user_pets
                    - 当管理员要求查询某个用户时，可以使用指定的用户ID
                    - 必须调用工具获取真实数据，不要编造任何数据！
                    """)
                .defaultAdvisors(
                        new SimpleLoggerAdvisor(),
                        MessageChatMemoryAdvisor.builder(chatMemory).build()
                )
                .defaultTools(petClinicTools)
                .build();
    }
}
