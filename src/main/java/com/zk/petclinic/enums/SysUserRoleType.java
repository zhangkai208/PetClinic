package com.zk.petclinic.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum SysUserRoleType {
    OWNER(1, "宠物主人"),
    PROVIDER(2, "服务商"),
    ADMIN(3, "管理员");
    @EnumValue
    private final Integer value;
    @JsonValue
    private final String label;

    SysUserRoleType(Integer value, String label) {
        this.value = value;
        this.label = label;
    }
    /**
     * 根据值获取枚举
     */
    public static SysUserRoleType getEnumByValue(Integer value) {
        for (SysUserRoleType type : SysUserRoleType.values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        return null;
    }
    /**
     * 根据标签获取枚举
     */
    public static SysUserRoleType getEnumByLabel(String label) {
        for (SysUserRoleType type : SysUserRoleType.values()) {
            if (type.label.equals(label)){
                return type;
            }
        }
        return null;
    }
}
