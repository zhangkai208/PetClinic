package com.zk.petclinic.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 服务商状态枚举
 */
@Getter
public enum ServiceProviderStatus {
    PENDING_REVIEW(0, "待审核"),
    APPROVED(1, "已通过"),
    REJECTED(2, "已拒绝");

    @EnumValue  // MyBatis-Plus 数据库存储值
    private final Integer value;

    @JsonValue  // JSON序列化时返回的值
    private final String label;

    ServiceProviderStatus(Integer value, String label) {
        this.value = value;
        this.label = label;
    }

    /**
     * 根据值获取枚举
     */
    public static ServiceProviderStatus getByValue(Integer value) {
        for (ServiceProviderStatus status : values()) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        return null;
    }

    /**
     * 根据标签获取枚举
     */
    public static ServiceProviderStatus getByLabel(String label) {
        for (ServiceProviderStatus status : values()) {
            if (status.getLabel().equals(label)) {
                return status;
            }
        }
        return null;
    }
}
