package com.zk.petclinic.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 消息表
 * @TableName chat_message
 */
@TableName(value ="chat_message")
@Data
public class ChatMessage {
    /**
     * 消息ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 会话ID
     */
    @TableField(value = "conversation_id")
    private Long conversationId;

    /**
     * 角色：user/assistant/system
     */
    @TableField(value = "role")
    private String role;

    /**
     * 消息内容
     */
    @TableField(value = "content")
    private String content;

    @TableField(value = "message_type")
    private String messageType; // text / image

    @TableField(value = "media_url")
    private String mediaUrl;    // 图片URL

    @TableField(value = "extra_json")
    private String extraJson;   // {"prompt":"...","model":"..."}

    /**
     * 创建时间
     */
    @TableField(value = "created_at")
    private Date createdAt;


    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        ChatMessage other = (ChatMessage) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getConversationId() == null ? other.getConversationId() == null : this.getConversationId().equals(other.getConversationId()))
            && (this.getRole() == null ? other.getRole() == null : this.getRole().equals(other.getRole()))
            && (this.getContent() == null ? other.getContent() == null : this.getContent().equals(other.getContent()))
            && (this.getCreatedAt() == null ? other.getCreatedAt() == null : this.getCreatedAt().equals(other.getCreatedAt()))
            && (this.getMessageType() == null ? other.getMessageType() == null : this.getMessageType().equals(other.getMessageType()))
            && (this.getMediaUrl() == null ? other.getMediaUrl() == null : this.getMediaUrl().equals(other.getMediaUrl()))
            && (this.getExtraJson() == null ? other.getExtraJson() == null : this.getExtraJson().equals(other.getExtraJson()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getConversationId() == null) ? 0 : getConversationId().hashCode());
        result = prime * result + ((getRole() == null) ? 0 : getRole().hashCode());
        result = prime * result + ((getContent() == null) ? 0 : getContent().hashCode());
        result = prime * result + ((getCreatedAt() == null) ? 0 : getCreatedAt().hashCode());
        result = prime * result + ((getMessageType() == null) ? 0 : getMessageType().hashCode());
        result = prime * result + ((getMediaUrl() == null) ? 0 : getMediaUrl().hashCode());
        result = prime * result + ((getExtraJson() == null) ? 0 : getExtraJson().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", conversationId=").append(conversationId);
        sb.append(", role=").append(role);
        sb.append(", content=").append(content);
        sb.append(", messageType=").append(messageType);
        sb.append(", mediaUrl=").append(mediaUrl);
        sb.append(", extraJson=").append(extraJson);
        sb.append(", createdAt=").append(createdAt);
        sb.append("]");
        return sb.toString();
    }
}