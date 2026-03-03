package com.zk.petclinic.service.impl;

import com.zk.petclinic.domain.dto.IntentResult;
import com.zk.petclinic.util.QiniuOssUtil;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Base64;
import java.util.UUID;

@Service
public class ChatAiServiceImpl {
    @Autowired
    private ChatClient chatClient;
    @Autowired
    private ImageModel imageModel;
    public IntentResult detectIntent(String userMessage) {
        try {
            String raw = chatClient.prompt()
                    .system("你是意图分类器。只输出一行：text| 或 image|<中文绘图提示词>。不要输出其他内容。")
                    .user(userMessage)
                    .call()
                    .content();
            if (raw == null) {
                return new IntentResult(false, "");
            }
            String line = raw.trim();
            if (line.startsWith("image|")) {
                return new IntentResult(true, line.substring("image|".length()).trim());
            }
        } catch (Exception e) {
            // 分类失败兜底走文本
        }
        return new IntentResult(false, "");
    }

    public String generateImageAndGetUrl(String prompt, Long conversationId) {
        ImageResponse response = imageModel.call(new ImagePrompt(prompt));
        if (response == null || response.getResult() == null || response.getResult().getOutput() == null) {
            throw new RuntimeException("模型未返回图片");
        }

        String objectName = "ai-chat/" + conversationId + "/" + UUID.randomUUID() + ".png";

        String url = response.getResult().getOutput().getUrl();
        if (url != null && !url.isEmpty()) {
            try (InputStream in = new URL(url).openStream()) {
                String uploadedUrl = QiniuOssUtil.uploadFile(objectName, in);
                if (uploadedUrl == null || uploadedUrl.isEmpty()) {
                    throw new RuntimeException("模型URL转存OSS失败");
                }
                return uploadedUrl;
            } catch (Exception e) {
                throw new RuntimeException("下载模型图片并上传OSS失败: " + e.getMessage(), e);
            }
        }

        String b64 = response.getResult().getOutput().getB64Json();
        if (b64 == null || b64.isEmpty()) {
            throw new RuntimeException("未返回URL或base64");
        }

        int idx = b64.indexOf(",");
        if (idx > -1) {
            b64 = b64.substring(idx + 1);
        }

        byte[] bytes = Base64.getDecoder().decode(b64);
        InputStream in = new ByteArrayInputStream(bytes);
        String uploadedUrl = QiniuOssUtil.uploadFile(objectName, in);
        if (uploadedUrl == null || uploadedUrl.isEmpty()) {
            throw new RuntimeException("上传OSS失败");
        }
        return uploadedUrl;
    }

    public String buildImageExtraJson(String prompt) {
        return "{\"prompt\":\"" + escapeJson(prompt) + "\",\"model\":\"Kwai-Kolors/Kolors\"}";
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
