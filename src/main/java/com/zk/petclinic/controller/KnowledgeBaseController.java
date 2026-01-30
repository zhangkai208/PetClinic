package com.zk.petclinic.controller;

import com.zk.petclinic.service.KnowledgeBaseService;
import com.zk.petclinic.util.ResultUtil;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/knowledge")
public class KnowledgeBaseController {
    @Autowired
    private KnowledgeBaseService knowledgeBaseService;

    @PostMapping("/add")
    public ResultUtil<?> addKnowledge(@RequestBody Map<String,String> request){
        String content = request.get("content");
        if (content == null || content.isEmpty()) {
            return ResultUtil.fail("知识内容不能为空");
        }
        knowledgeBaseService.addKnowledge(content);
        return ResultUtil.success("知识内容添加成功");
    }

    @PostMapping("/batch-add")
    public ResultUtil<?> batchAddKnowledge(@RequestBody List<String> contents){
        if (contents == null || contents.isEmpty()) {
            return ResultUtil.fail("知识内容不能为空");
        }
        knowledgeBaseService.addKnowledgeList(contents);
        return ResultUtil.success("批量添加成功，共 " + contents.size() + " 条");
    }

    @GetMapping("/search")
    public ResultUtil<?> searchKnowledge(
            @RequestParam String query,
            @RequestParam(defaultValue = "5") int topK){
        List<Document> results = knowledgeBaseService.searchKnowledge(query, topK);
        // 转换为前端友好的格式
        List<Map<String, Object>> response = results.stream()
                .map(doc -> Map.of(
                        "id", doc.getId(),
                        "content", doc.getText(),
                        "metadata", doc.getMetadata()
                ))
                .toList();
        return ResultUtil.success(response);
    }


    @GetMapping("/list")
    public ResultUtil<?> listKnowledge(@RequestParam(defaultValue = "100") int limit){
        // 直接从 Redis 获取所有知识文档
        List<Map<String, Object>> results = knowledgeBaseService.listAllKnowledge(limit);
        return ResultUtil.success(results);
    }

    @DeleteMapping("/delete")
    public ResultUtil<?> deleteKnowledge(@RequestBody List<String> documentIds){
        if (documentIds == null || documentIds.isEmpty()) {
            return ResultUtil.fail("文档ID不能为空");
        }
        knowledgeBaseService.deleteKnowledge(documentIds);
        return ResultUtil.success("删除成功");
    }
}
