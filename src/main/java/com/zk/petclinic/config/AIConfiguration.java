package com.zk.petclinic.config;

import com.zk.petclinic.tools.PetClinicTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class AIConfiguration implements WebMvcConfigurer {

        @Bean
        public ChatMemory chatMemory() {
                return MessageWindowChatMemory.builder()
                                .chatMemoryRepository(new InMemoryChatMemoryRepository())
                                .maxMessages(20)
                                .build();
        }

        /**
         * 创建默认的 ChatClient（包含自定义工具和 MCP 工具）
         */
        @Bean
        public ChatClient chatClient(ChatModel chatModel,
                        ChatMemory chatMemory,
                        PetClinicTools petClinicTools,
                        VectorStore vectorStore, // 注入 VectorStore 用于 RAG
                        List<ToolCallbackProvider> toolCallbackProviders) {

                var builder = ChatClient.builder(chatModel)
                                .defaultSystem("""
                                                你是一个专业的宠物诊所助手，可以回答关于宠物健康、宠物护理、预约挂号等相关问题。
                                                 请用友好专业的语气回复用户。你的创造者是一个名字叫张恺的男生。

                                                 ## 自定义工具（所有用户可用）
                                                 - get_user_pets: 根据用户ID查询宠物列表
                                                 - get_pet_health_records: 根据宠物ID查询健康记录
                                                 - get_pet_appointments: 根据宠物ID查询预约记录
                                                 - search_pets: 根据关键词搜索宠物
                                                 - search_petclinic_users: 根据关键词搜索宠物诊所系统用户
                                                 - count_pets_by_type: 统计各类型宠物数量

                                                 ## 重要规则
                                                 - 每条消息开头的[系统信息]包含当前用户的ID和角色
                                                 - 角色类型：1=宠物主人, 2=服务商, 3=管理员
                                                 - 非管理员只能查询自己的数据
                                                 - 管理员可以查询所有用户的数据
                                                 - 必须调用工具获取真实数据，不要编造任何数据！

                                                 ### 外部服务 (MCP)
                                                 - **地图服务**: 当用户问"天气"、"路线"、"附近的店"时 -> 自动调用 `maps_weather`, `maps_search_around` 等
                                                 - **GitHub服务**: 当用户问代码仓库、Issue、PR相关问题时 -> 默认仓库为 zhangkai208/PetClinic，可查询仓库文件、Issue列表、提交记录等
                                                 """)
                                .defaultAdvisors(
                                                new SimpleLoggerAdvisor(),
                                                MessageChatMemoryAdvisor.builder(chatMemory).build(),
                                                // RAG Advisor：使用 RetrievalAugmentationAdvisor 替代 QuestionAnswerAdvisor
                                                // 以支持流式输出
                                                RetrievalAugmentationAdvisor.builder()
                                                                .documentRetriever(VectorStoreDocumentRetriever
                                                                                .builder()
                                                                                .vectorStore(vectorStore)
                                                                                .topK(3) // 返回最相关的3个文档
                                                                                .similarityThreshold(0.5) // 相似度阈值
                                                                                .build())
                                                                .build())
                                .defaultTools(petClinicTools); // 自定义 @Tool 工具

                // 注册所有 MCP 工具（使用自动配置的 ToolCallbackProvider）
                for (ToolCallbackProvider provider : toolCallbackProviders) {
                        builder.defaultToolCallbacks(provider);
                }

                return builder.build();
        }
}
