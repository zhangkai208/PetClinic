package com.zk.petclinic.enums;

import lombok.Getter;

@Getter
public enum ServiceProviderStatus {
    //状态：0-待审核，1-已通过，2-已拒绝
    PENDING_REVIEW("0","待审核"),
    APPROVED("1","已通过"),
    REJECTED("2","已拒绝");
    private final String value;
    private final String label;
    ServiceProviderStatus(String value, String label) {
        this.value = value;
        this.label = label;
    }
    private static ServiceProviderStatus getByValue(String value) {
        for (ServiceProviderStatus serviceProviderStatus : values()) {
            if (serviceProviderStatus.getValue().equals(value)) {
                return serviceProviderStatus;
            }
        }
        return null;
    }
    private static ServiceProviderStatus getByLabel(String label) {
        for (ServiceProviderStatus serviceProviderStatus : values()) {
            if (serviceProviderStatus.getLabel().equals(label)) {
                return serviceProviderStatus;
            }
        }
        return null;
    }
}
