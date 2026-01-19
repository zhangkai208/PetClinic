package com.zk.petclinic.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum AppointmentStatus {
    Pending_Confirmation(0, "待确认"),
    Booked(1, "已预约"),
    Completed(2, "已完成"),
    Canceled(3, "已取消");

    @EnumValue  // MyBatis-Plus 数据库存储值
    private final Integer value;

    @JsonValue  // JSON序列化时返回的值
    private final String label;


    AppointmentStatus(Integer value, String label) {
        this.value = value;
        this.label = label;
    }

    /**
     * 根据值获取枚举
     */
    public static AppointmentStatus getEnumByValue(Integer value) {
        for (AppointmentStatus status : AppointmentStatus.values()) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        return null;
    }

    /**
     * 根据标签获取枚举
     */
    public static AppointmentStatus getEnumByLabel(String label) {
        for (AppointmentStatus status : AppointmentStatus.values()) {
            if (status.getLabel().equals(label)) {
                return status;
            }
        }
        return null;
    }
}