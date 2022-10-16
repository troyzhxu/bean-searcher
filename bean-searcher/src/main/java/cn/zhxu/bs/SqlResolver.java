package cn.zhxu.bs;

/**
 * @author Troy.Zhou @ 2017-03-20
 * 
 * SQL 解析器
 * */
public interface SqlResolver {

	/**
	 * @param beanMeta 元信息
	 * @param searchParam 检索参数
	 * @param <T> 泛型
	 * @return 检索 SQL
	 */
	<T> SearchSql<T> resolve(BeanMeta<T> beanMeta, SearchParam searchParam);
	
}
