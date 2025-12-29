package com.zk.petclinic.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 宠物性别枚举
 */
@Getter
public enum Petgender {
    UNKNOWN(0, "未知"),
    MALE(1, "公"),
    FEMALE(2, "母");

    @EnumValue  // MyBatis-Plus 数据库存储值
    private final Integer value;

    @JsonValue  // JSON序列化时返回的值
    private final String label;

    Petgender(Integer value, String label) {
        this.value = value;
        this.label = label;
    }

    /**
     * 根据值获取枚举
     */
    public static Petgender getByValue(Integer value) {
        for (Petgender petgender : values()) {
            if (petgender.getValue().equals(value)) {
                return petgender;
            }
        }
        return null;
    }

    /**
     * 根据标签获取枚举
     */
    public static Petgender getByLabel(String label) {
        for (Petgender petgender : values()) {
            if (petgender.getLabel().equals(label)) {
                return petgender;
            }
        }
        return null;
    }
}
