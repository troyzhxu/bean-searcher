package com.example;

import lombok.Getter;

/**
 * 性别
 */
@Getter
public enum Gender {

    /**
     * 男性
     */
    Male("男"),

    /**
     * 女性
     */
    Female("女");

    private final String label;

    Gender(String label) {
        this.label = label;
    }

}
