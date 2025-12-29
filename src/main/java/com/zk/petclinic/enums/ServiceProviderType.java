package com.zk.petclinic.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 服务商类型枚举
 */
@Getter
public enum ServiceProviderType {
    HOSPITALS(0, "医院"),
    BEAUTY_SALONS(1, "美容店"),
    FOSTER_CARE(2, "寄养"),
    TRAINING(3, "训练");

    @EnumValue  // MyBatis-Plus 数据库存储值
    private final Integer value;

    @JsonValue  // JSON序列化时返回的值
    private final String label;

    ServiceProviderType(Integer value, String label) {
        this.value = value;
        this.label = label;
    }

    /**
     * 根据值获取枚举
     */
    public static ServiceProviderType getByValue(Integer value) {
        for (ServiceProviderType type : values()) {
            if (type.getValue().equals(value)) {
                return type;
            }
        }
        return null;
    }

    /**
     * 根据标签获取枚举
     */
    public static ServiceProviderType getByLabel(String label) {
        for (ServiceProviderType type : values()) {
            if (type.getLabel().equals(label)) {
                return type;
            }
        }
        return null;
    }
}
