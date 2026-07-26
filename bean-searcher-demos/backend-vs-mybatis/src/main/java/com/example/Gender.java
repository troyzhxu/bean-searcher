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

	/**
	 * 由数据库存储的编码（枚举名，如 "Male" / "Female"）解析为枚举。
	 */
	public static Gender fromCode(String code) {
		if (code == null) return null;
		for (Gender g : values()) {
			if (g.name().equals(code)) {
				return g;
			}
		}
		return null;
	}

}
