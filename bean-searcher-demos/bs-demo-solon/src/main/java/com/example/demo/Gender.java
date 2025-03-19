package com.example.demo;

/**
 * 枚举：性别
 */
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

    public String getLabel() {
        return label;
    }

}
