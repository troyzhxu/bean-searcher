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

    private final String chinese;

    Gender(String chinese) {
        this.chinese = chinese;
    }

    public String getChinese() {
        return chinese;
    }

}
