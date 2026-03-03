package com.zk.petclinic.domain.dto;

public class IntentResult {
    public final boolean image;
    public final String prompt;

    public IntentResult(boolean image, String prompt) {
        this.image = image;
        this.prompt = prompt;
    }
}
