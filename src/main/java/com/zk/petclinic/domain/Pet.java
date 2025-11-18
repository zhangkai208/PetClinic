package com.zk.petclinic.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * 宠物表
 * @TableName pet
 */
@TableName(value ="pet")
@Data
public class Pet {
    /**
     * 
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 主人ID
     */
    @TableField(value = "owner_id")
    private Long ownerId;

    /**
     * 宠物昵称
     */
    @TableField(value = "name")
    private String name;

    /**
     * 宠物类型：猫、狗等
     */
    @TableField(value = "type")
    private String type;

    /**
     * 品种
     */
    @TableField(value = "breed")
    private String breed;

    /**
     * 生日
     */
    @TableField(value = "birth_date")
    private Date birthDate;

    /**
     * 体重(kg)
     */
    @TableField(value = "weight")
    private BigDecimal weight;

    /**
     * 性别：0-未知，1-公，2-母
     */
    @TableField(value = "gender")
    private Integer gender;

    /**
     * 头像
     */
    @TableField(value = "avatar")
    private String avatar;

    /**
     * 过往病史
     */
    @TableField(value = "medical_history")
    private String medicalHistory;

    /**
     * 过敏史
     */
    @TableField(value = "allergy")
    private String allergy;

    /**
     * 
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 
     */
    @TableField(value = "update_time")
    private Date updateTime;

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
        Pet other = (Pet) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getOwnerId() == null ? other.getOwnerId() == null : this.getOwnerId().equals(other.getOwnerId()))
            && (this.getName() == null ? other.getName() == null : this.getName().equals(other.getName()))
            && (this.getType() == null ? other.getType() == null : this.getType().equals(other.getType()))
            && (this.getBreed() == null ? other.getBreed() == null : this.getBreed().equals(other.getBreed()))
            && (this.getBirthDate() == null ? other.getBirthDate() == null : this.getBirthDate().equals(other.getBirthDate()))
            && (this.getWeight() == null ? other.getWeight() == null : this.getWeight().equals(other.getWeight()))
            && (this.getGender() == null ? other.getGender() == null : this.getGender().equals(other.getGender()))
            && (this.getAvatar() == null ? other.getAvatar() == null : this.getAvatar().equals(other.getAvatar()))
            && (this.getMedicalHistory() == null ? other.getMedicalHistory() == null : this.getMedicalHistory().equals(other.getMedicalHistory()))
            && (this.getAllergy() == null ? other.getAllergy() == null : this.getAllergy().equals(other.getAllergy()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getOwnerId() == null) ? 0 : getOwnerId().hashCode());
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getType() == null) ? 0 : getType().hashCode());
        result = prime * result + ((getBreed() == null) ? 0 : getBreed().hashCode());
        result = prime * result + ((getBirthDate() == null) ? 0 : getBirthDate().hashCode());
        result = prime * result + ((getWeight() == null) ? 0 : getWeight().hashCode());
        result = prime * result + ((getGender() == null) ? 0 : getGender().hashCode());
        result = prime * result + ((getAvatar() == null) ? 0 : getAvatar().hashCode());
        result = prime * result + ((getMedicalHistory() == null) ? 0 : getMedicalHistory().hashCode());
        result = prime * result + ((getAllergy() == null) ? 0 : getAllergy().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getUpdateTime() == null) ? 0 : getUpdateTime().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", ownerId=").append(ownerId);
        sb.append(", name=").append(name);
        sb.append(", type=").append(type);
        sb.append(", breed=").append(breed);
        sb.append(", birthDate=").append(birthDate);
        sb.append(", weight=").append(weight);
        sb.append(", gender=").append(gender);
        sb.append(", avatar=").append(avatar);
        sb.append(", medicalHistory=").append(medicalHistory);
        sb.append(", allergy=").append(allergy);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append("]");
        return sb.toString();
    }
}