package com.zk.petclinic.enums;

import lombok.Getter;

@Getter
public enum ServiceProviderType {

    HOSPITALS("0","医院"),
    BEAUTY_SALONS("1","美容店"),
    FOSTER_CARE("2","寄养"),
    TRAINING("3","训练");
    private final String value;
    private final String label;
    ServiceProviderType(String value, String label) {
        this.value = value;
        this.label = label;
    }
    private static ServiceProviderType getByValue(String value) {
        for (ServiceProviderType serviceProviderType : values()) {
            if (serviceProviderType.getValue().equals(value)) {
                return serviceProviderType;
            }
        }
        return null;
    }
    private static ServiceProviderType getByLabel(String label) {
        for (ServiceProviderType serviceProviderType : values()) {
            if (serviceProviderType.getLabel().equals(label)) {
                return serviceProviderType;
            }
        }
        return null;
    }
}
