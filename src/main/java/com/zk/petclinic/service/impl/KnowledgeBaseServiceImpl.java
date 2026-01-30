package com.zk.petclinic.service.impl;

import com.zk.petclinic.service.KnowledgeBaseService;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.redis.RedisVectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.json.Path2;
import redis.clients.jedis.search.*;

import java.util.*;

@Service
public class KnowledgeBaseServiceImpl implements KnowledgeBaseService {
    @Autowired
    private VectorStore vectorStore;

    @Override
    public void addKnowledge(String content) {
        Document doc = new Document(content);
        vectorStore.add(List.of(doc));
    }

    @Override
    public void addKnowledge(String content, Map<String, Object> metadata) {
        Document doc = new Document(content, metadata);
        vectorStore.add(List.of(doc));
    }

    @Override
    public void addKnowledgeList(List<String> contents) {
        List<Document> docs = contents.stream().map(Document::new).toList();
        vectorStore.add(docs);
    }

    @Override
    public List<Document> searchKnowledge(String query, int topK) {
        SearchRequest searchRequest = SearchRequest.builder()
                .query(query)
                .topK(topK)
                .similarityThreshold(0.8)  // 只返回相似度 >= 0.8 的结果 (distance <= 0.2)
                .build();
        return vectorStore.similaritySearch(searchRequest);
    }

    @Override
    public void deleteKnowledge(List<String> documentIds) {
        vectorStore.delete(documentIds);
    }

    /**
     * 列出所有知识文档
     * 使用 Redis 原生客户端通过 FT.SEARCH 查询所有文档
     */
    @Override
    public List<Map<String, Object>> listAllKnowledge(int limit) {
        List<Map<String, Object>> results = new ArrayList<>();

        // 获取原生 Jedis 客户端
        if (vectorStore instanceof RedisVectorStore redisVectorStore) {
            Optional<JedisPooled> nativeClient = redisVectorStore.getNativeClient();

            if (nativeClient.isPresent()) {
                JedisPooled jedis = nativeClient.get();
                try {
                    // 使用 FT.SEARCH 查询所有文档 ("*" 匹配所有)
                    Query query = new Query("*")
                            .limit(0, limit)
                            .returnFields("content");

                    // 索引名称需要与配置一致 (petclinic-vectors)
                    SearchResult searchResult = jedis.ftSearch("petclinic-vectors", query);

                    for (redis.clients.jedis.search.Document doc : searchResult.getDocuments()) {
                        Map<String, Object> result = new HashMap<>();
                        // 文档 ID 格式: doc:uuid，需要去掉前缀
                        String id = doc.getId().replace("doc:", "");
                        result.put("id", id);
                        result.put("content", doc.get("content") != null ? doc.get("content").toString() : "");
                        result.put("metadata", new HashMap<>());
                        results.add(result);
                    }
                } catch (Exception e) {
                    System.err.println("查询知识库失败: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }

        return results;
    }
}
