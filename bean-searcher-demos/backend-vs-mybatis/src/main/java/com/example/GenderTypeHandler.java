package com.example;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 性别枚举 <-> 数据库 varchar 的类型处理器。
 *
 * 对比 Bean Searcher 版本：Bean Searcher 通过 {@code EnumLabelLoader} 自动识别
 * 枚举字段并完成「数据库值 <-> 枚举」的转换；MyBatis 需要手写这个 TypeHandler，
 * 并通过 {@link MappedTypes} 注册，框架才会在读写 Gender 字段时自动调用它。
 */
@MappedTypes(Gender.class)
public class GenderTypeHandler extends BaseTypeHandler<Gender> {

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, Gender parameter, JdbcType jdbcType) throws SQLException {
		ps.setString(i, parameter.name());
	}

	@Override
	public Gender getNullableResult(ResultSet rs, String columnName) throws SQLException {
		return Gender.fromCode(rs.getString(columnName));
	}

	@Override
	public Gender getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		return Gender.fromCode(rs.getString(columnIndex));
	}

	@Override
	public Gender getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		return Gender.fromCode(cs.getString(columnIndex));
	}

}
