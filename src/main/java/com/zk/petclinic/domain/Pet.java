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
    private Long owner_id;

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
    private Date birth_date;

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
    private String medical_history;

    /**
     * 过敏史
     */
    @TableField(value = "allergy")
    private String allergy;

    /**
     * 
     */
    @TableField(value = "create_time")
    private Date create_time;

    /**
     * 
     */
    @TableField(value = "update_time")
    private Date update_time;

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
            && (this.getOwner_id() == null ? other.getOwner_id() == null : this.getOwner_id().equals(other.getOwner_id()))
            && (this.getName() == null ? other.getName() == null : this.getName().equals(other.getName()))
            && (this.getType() == null ? other.getType() == null : this.getType().equals(other.getType()))
            && (this.getBreed() == null ? other.getBreed() == null : this.getBreed().equals(other.getBreed()))
            && (this.getBirth_date() == null ? other.getBirth_date() == null : this.getBirth_date().equals(other.getBirth_date()))
            && (this.getWeight() == null ? other.getWeight() == null : this.getWeight().equals(other.getWeight()))
            && (this.getGender() == null ? other.getGender() == null : this.getGender().equals(other.getGender()))
            && (this.getAvatar() == null ? other.getAvatar() == null : this.getAvatar().equals(other.getAvatar()))
            && (this.getMedical_history() == null ? other.getMedical_history() == null : this.getMedical_history().equals(other.getMedical_history()))
            && (this.getAllergy() == null ? other.getAllergy() == null : this.getAllergy().equals(other.getAllergy()))
            && (this.getCreate_time() == null ? other.getCreate_time() == null : this.getCreate_time().equals(other.getCreate_time()))
            && (this.getUpdate_time() == null ? other.getUpdate_time() == null : this.getUpdate_time().equals(other.getUpdate_time()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getOwner_id() == null) ? 0 : getOwner_id().hashCode());
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getType() == null) ? 0 : getType().hashCode());
        result = prime * result + ((getBreed() == null) ? 0 : getBreed().hashCode());
        result = prime * result + ((getBirth_date() == null) ? 0 : getBirth_date().hashCode());
        result = prime * result + ((getWeight() == null) ? 0 : getWeight().hashCode());
        result = prime * result + ((getGender() == null) ? 0 : getGender().hashCode());
        result = prime * result + ((getAvatar() == null) ? 0 : getAvatar().hashCode());
        result = prime * result + ((getMedical_history() == null) ? 0 : getMedical_history().hashCode());
        result = prime * result + ((getAllergy() == null) ? 0 : getAllergy().hashCode());
        result = prime * result + ((getCreate_time() == null) ? 0 : getCreate_time().hashCode());
        result = prime * result + ((getUpdate_time() == null) ? 0 : getUpdate_time().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", owner_id=").append(owner_id);
        sb.append(", name=").append(name);
        sb.append(", type=").append(type);
        sb.append(", breed=").append(breed);
        sb.append(", birth_date=").append(birth_date);
        sb.append(", weight=").append(weight);
        sb.append(", gender=").append(gender);
        sb.append(", avatar=").append(avatar);
        sb.append(", medical_history=").append(medical_history);
        sb.append(", allergy=").append(allergy);
        sb.append(", create_time=").append(create_time);
        sb.append(", update_time=").append(update_time);
        sb.append("]");
        return sb.toString();
    }
}