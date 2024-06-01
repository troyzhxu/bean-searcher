package cn.zhxu.bs.dialect;


import cn.zhxu.bs.SqlWrapper;
import cn.zhxu.bs.param.Paging;

/**
 * MySql 方言实现
 * @author Troy.Zhou
 * */
public class MySqlDialect implements Dialect {

	@Override
	public SqlWrapper<Object> forPaginate(String fieldSelectSql, String fromWhereSql, Paging paging) {
		SqlWrapper<Object> wrapper = new SqlWrapper<>();
		StringBuilder ret = new StringBuilder();
		ret.append(fieldSelectSql).append(fromWhereSql);
		if (paging != null) {
			ret.append(" limit ?, ?");
			wrapper.addPara(paging.getOffset());
			wrapper.addPara(paging.getSize());
		}
		wrapper.setSql(ret.toString());
		return wrapper;
	}

	@Override
	public boolean allowHavingAlias() {
		return true;
	}

}
