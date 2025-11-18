package com.zk.petclinic.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 预约表
 * @TableName appointment
 */
@TableName(value ="appointment")
@Data
public class Appointment {
    /**
     * 
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 宠物ID
     */
    @TableField(value = "pet_id")
    private Long petId;

    /**
     * 服务商ID
     */
    @TableField(value = "provider_id")
    private Long providerId;

    /**
     * 服务类型
     */
    @TableField(value = "service_type")
    private String serviceType;

    /**
     * 预约时间
     */
    @TableField(value = "appointment_time")
    private Date appointmentTime;

    /**
     * 状态：0-待确认，1-已预约，2-已完成，3-已取消
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 备注
     */
    @TableField(value = "remark")
    private String remark;

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
        Appointment other = (Appointment) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getPetId() == null ? other.getPetId() == null : this.getPetId().equals(other.getPetId()))
            && (this.getProviderId() == null ? other.getProviderId() == null : this.getProviderId().equals(other.getProviderId()))
            && (this.getServiceType() == null ? other.getServiceType() == null : this.getServiceType().equals(other.getServiceType()))
            && (this.getAppointmentTime() == null ? other.getAppointmentTime() == null : this.getAppointmentTime().equals(other.getAppointmentTime()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getRemark() == null ? other.getRemark() == null : this.getRemark().equals(other.getRemark()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getPetId() == null) ? 0 : getPetId().hashCode());
        result = prime * result + ((getProviderId() == null) ? 0 : getProviderId().hashCode());
        result = prime * result + ((getServiceType() == null) ? 0 : getServiceType().hashCode());
        result = prime * result + ((getAppointmentTime() == null) ? 0 : getAppointmentTime().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getRemark() == null) ? 0 : getRemark().hashCode());
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
        sb.append(", providerId=").append(providerId);
        sb.append(", serviceType=").append(serviceType);
        sb.append(", appointmentTime=").append(appointmentTime);
        sb.append(", status=").append(status);
        sb.append(", remark=").append(remark);
        sb.append(", createTime=").append(createTime);
        sb.append("]");
        return sb.toString();
    }
}