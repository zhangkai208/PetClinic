package com.zk.petclinic.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 健康记录表
 * @TableName health_record
 */
@TableName(value ="health_record")
@Data
public class HealthRecord {
    /**
     * 
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 
     */
    @TableField(value = "pet_id")
    private Long petId;

    /**
     * 记录类型：1-疫苗，2-驱虫，3-用药，4-健康笔记
     */
    @TableField(value = "record_type")
    private Integer recordType;

    /**
     * 标题
     */
    @TableField(value = "title")
    private String title;

    /**
     * 内容
     */
    @TableField(value = "content")
    private String content;

    /**
     * 记录日期
     */
    @TableField(value = "record_date")
    private Date recordDate;

    /**
     * 下次提醒日期（疫苗、驱虫）
     */
    @TableField(value = "next_date")
    private Date nextDate;

    /**
     * 
     */
    @TableField(value = "create_time")
    private Date createTime;

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
        HealthRecord other = (HealthRecord) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getPetId() == null ? other.getPetId() == null : this.getPetId().equals(other.getPetId()))
            && (this.getRecordType() == null ? other.getRecordType() == null : this.getRecordType().equals(other.getRecordType()))
            && (this.getTitle() == null ? other.getTitle() == null : this.getTitle().equals(other.getTitle()))
            && (this.getContent() == null ? other.getContent() == null : this.getContent().equals(other.getContent()))
            && (this.getRecordDate() == null ? other.getRecordDate() == null : this.getRecordDate().equals(other.getRecordDate()))
            && (this.getNextDate() == null ? other.getNextDate() == null : this.getNextDate().equals(other.getNextDate()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getPetId() == null) ? 0 : getPetId().hashCode());
        result = prime * result + ((getRecordType() == null) ? 0 : getRecordType().hashCode());
        result = prime * result + ((getTitle() == null) ? 0 : getTitle().hashCode());
        result = prime * result + ((getContent() == null) ? 0 : getContent().hashCode());
        result = prime * result + ((getRecordDate() == null) ? 0 : getRecordDate().hashCode());
        result = prime * result + ((getNextDate() == null) ? 0 : getNextDate().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", petId=").append(petId);
        sb.append(", recordType=").append(recordType);
        sb.append(", title=").append(title);
        sb.append(", content=").append(content);
        sb.append(", recordDate=").append(recordDate);
        sb.append(", nextDate=").append(nextDate);
        sb.append(", createTime=").append(createTime);
        sb.append("]");
        return sb.toString();
    }
}