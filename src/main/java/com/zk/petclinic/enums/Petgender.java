package com.zk.petclinic.enums;

import lombok.Data;
import lombok.Getter;

@Getter
public enum Petgender {
    UNKNOWN("0", "未知"),
    MALE("1", "公"),
    FEMALE("2", "母");
    private final String value;
    private final String label;
    Petgender(String value, String label) {
        this.value = value.toString();
        this.label = label;
    }
    //根据值获取枚举
    private static Petgender getByValue(String value) {
        for (Petgender petgender : values()) {
            if (petgender.getValue().equals(value)) {
                return petgender;
            }
        }
        return null;
    }
    //根据枚举获取值
    private static Petgender getByLabel(String label) {
        for (Petgender petgender : values()) {
            if (petgender.getLabel().equals(label)) {
                return petgender;
            }
        }
        return null;
    }
}
