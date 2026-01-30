package com.zk.petclinic.service;


import org.springframework.ai.document.Document;
import java.util.List;
import java.util.Map;

public interface KnowledgeBaseService {
    /**
     * 添加单条知识
     * @param content 知识内容
     */
    public void addKnowledge(String content);
    /**
     * 添加带元数据的知识
     * @param content 知识内容
     * @param metadata 元数据（如分类、来源等）
     */
    public void addKnowledge(String content, Map<String, Object> metadata);
    /**
     * 批量添加知识
     * @param contents 知识内容列表
     */
    public void addKnowledgeList(List<String> contents);
    /**
     * 搜索相关知识
     * @param query 查询内容
     * @param topK 返回结果数量
     * @return 相关文档列表
     */
    public List<Document> searchKnowledge(String query, int topK);
    /**
     * 删除知识（通过文档ID）
     * @param documentIds 文档ID列表
     */
    public void deleteKnowledge(List<String> documentIds);
    /**
     * 列出所有知识文档
     * 直接扫描 Redis 中以 "doc:" 为前缀的 key，然后获取其内容
     */
    public List<Map<String, Object>> listAllKnowledge(int limit);
}
