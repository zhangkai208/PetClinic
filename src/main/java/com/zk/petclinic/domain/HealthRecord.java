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
    private Long pet_id;

    /**
     * 记录类型：1-疫苗，2-驱虫，3-用药，4-健康笔记
     */
    @TableField(value = "record_type")
    private Integer record_type;

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
    private Date record_date;

    /**
     * 下次提醒日期（疫苗、驱虫）
     */
    @TableField(value = "next_date")
    private Date next_date;

    /**
     * 
     */
    @TableField(value = "create_time")
    private Date create_time;

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
            && (this.getPet_id() == null ? other.getPet_id() == null : this.getPet_id().equals(other.getPet_id()))
            && (this.getRecord_type() == null ? other.getRecord_type() == null : this.getRecord_type().equals(other.getRecord_type()))
            && (this.getTitle() == null ? other.getTitle() == null : this.getTitle().equals(other.getTitle()))
            && (this.getContent() == null ? other.getContent() == null : this.getContent().equals(other.getContent()))
            && (this.getRecord_date() == null ? other.getRecord_date() == null : this.getRecord_date().equals(other.getRecord_date()))
            && (this.getNext_date() == null ? other.getNext_date() == null : this.getNext_date().equals(other.getNext_date()))
            && (this.getCreate_time() == null ? other.getCreate_time() == null : this.getCreate_time().equals(other.getCreate_time()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getPet_id() == null) ? 0 : getPet_id().hashCode());
        result = prime * result + ((getRecord_type() == null) ? 0 : getRecord_type().hashCode());
        result = prime * result + ((getTitle() == null) ? 0 : getTitle().hashCode());
        result = prime * result + ((getContent() == null) ? 0 : getContent().hashCode());
        result = prime * result + ((getRecord_date() == null) ? 0 : getRecord_date().hashCode());
        result = prime * result + ((getNext_date() == null) ? 0 : getNext_date().hashCode());
        result = prime * result + ((getCreate_time() == null) ? 0 : getCreate_time().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", pet_id=").append(pet_id);
        sb.append(", record_type=").append(record_type);
        sb.append(", title=").append(title);
        sb.append(", content=").append(content);
        sb.append(", record_date=").append(record_date);
        sb.append(", next_date=").append(next_date);
        sb.append(", create_time=").append(create_time);
        sb.append("]");
        return sb.toString();
    }
}