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
    private Long pet_id;

    /**
     * 服务商ID
     */
    @TableField(value = "provider_id")
    private Long provider_id;

    /**
     * 服务类型
     */
    @TableField(value = "service_type")
    private String service_type;

    /**
     * 预约时间
     */
    @TableField(value = "appointment_time")
    private Date appointment_time;

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
        Appointment other = (Appointment) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getPet_id() == null ? other.getPet_id() == null : this.getPet_id().equals(other.getPet_id()))
            && (this.getProvider_id() == null ? other.getProvider_id() == null : this.getProvider_id().equals(other.getProvider_id()))
            && (this.getService_type() == null ? other.getService_type() == null : this.getService_type().equals(other.getService_type()))
            && (this.getAppointment_time() == null ? other.getAppointment_time() == null : this.getAppointment_time().equals(other.getAppointment_time()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getRemark() == null ? other.getRemark() == null : this.getRemark().equals(other.getRemark()))
            && (this.getCreate_time() == null ? other.getCreate_time() == null : this.getCreate_time().equals(other.getCreate_time()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getPet_id() == null) ? 0 : getPet_id().hashCode());
        result = prime * result + ((getProvider_id() == null) ? 0 : getProvider_id().hashCode());
        result = prime * result + ((getService_type() == null) ? 0 : getService_type().hashCode());
        result = prime * result + ((getAppointment_time() == null) ? 0 : getAppointment_time().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getRemark() == null) ? 0 : getRemark().hashCode());
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
        sb.append(", provider_id=").append(provider_id);
        sb.append(", service_type=").append(service_type);
        sb.append(", appointment_time=").append(appointment_time);
        sb.append(", status=").append(status);
        sb.append(", remark=").append(remark);
        sb.append(", create_time=").append(create_time);
        sb.append("]");
        return sb.toString();
    }
}