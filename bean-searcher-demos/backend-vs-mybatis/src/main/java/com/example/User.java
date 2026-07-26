package com.example;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 用户检索实体（对应 users 表 + dept 表的关联字段）。
 *
 * 对比 Bean Searcher 版本：Bean Searcher 通过 @SearchBean / @DbField / @LabelFor 等注解
 * 声明多表映射与枚举标签，这里改为普通 POJO，字段含义与 Bean Searcher 输出保持一致：
 * - gender       : Gender 枚举（数据库存储 'Male' / 'Female'），由 GenderTypeHandler 转换，
 *                  序列化输出为枚举名，与 Bean Searcher 枚举序列化结果一致
 * - genderName   : 展示用中文（'男' / '女'），直接由 gender.getLabel() 派生，等价于 @LabelFor
 * - department   : 来自 dept 表的 name 字段，等价于 @DbField("d.name")
 * - entryDate    : 由 @JsonFormat 控制 JSON 输出格式，等价于 Bean Searcher 的 @Export(format=...)
 */
@Getter
@Setter
public class User {

	private long id;

	private String name;

	private int age;

	private Gender gender;

	private String department;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
	private LocalDateTime entryDate;

	/**
	 * 性别中文名，由枚举 label 派生（等价于 Bean Searcher 的 @LabelFor("gender")）。
	 */
	public String getGenderName() {
		return gender == null ? null : gender.getLabel();
	}

}
