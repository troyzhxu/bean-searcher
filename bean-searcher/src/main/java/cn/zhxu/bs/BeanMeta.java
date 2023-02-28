package cn.zhxu.bs;

import cn.zhxu.bs.util.StringUtils;

import java.util.*;

/**
 * SearchBean 的元信息
 *
 * @author Troy.Zhou @ 2017-03-20
 * @since v3.0.0
 */
public class BeanMeta<T> {

	/**
	 * Bean Class
	 */
	private final Class<T> beanClass;

	/**
	 * 所属数据源
	 */
	private final String dataSource;

	/**
	 * 需要查询的数据表
	 * */
	private final SqlSnippet tableSnippet;

	/**
	 * Where 条件
	 * */
	private final SqlSnippet whereSnippet;
	
	/**
	 * 分组字段
	 * */
	private final SqlSnippet groupBySnippet;

	/**
	 * 分组过滤
	 * */
	private final SqlSnippet havingSnippet;

	/**
	 * 排序字段
	 */
	private final SqlSnippet orderBySnippet;

	/**
	 * 是否允许使用检索参数指定排序参数
	 */
	private final boolean sortable;

	/**
	 * 是否 distinct 结果
	 * */
	private final boolean distinct;

	/**
	 * 映射: Bean属性 -> 属性元信息
	 * */
	private final Map<String, FieldMeta> fieldMetaMap = new HashMap<>();

	/**
	 * 可以被 Select 的属性集
	 * @since v4.1.0
	 */
	private final List<String> selectFields = new ArrayList<>();

	/**
	 * 单条 SQL 执行超时时间，单位：秒，0 表示永远不超时
	 */
	private final int timeout;

	public BeanMeta(Class<T> beanClass, String dataSource, SqlSnippet tableSnippet,
					SqlSnippet whereSnippet, SqlSnippet groupBySnippet, SqlSnippet havingSnippet,
					SqlSnippet orderBySnippet, boolean sortable, boolean distinct, int timeout) {
		this.beanClass = beanClass;
		this.dataSource = dataSource;
		this.tableSnippet = tableSnippet;
		this.whereSnippet = whereSnippet;
		this.groupBySnippet = groupBySnippet;
		this.havingSnippet = havingSnippet;
		this.orderBySnippet = orderBySnippet;
		this.sortable = sortable;
		this.distinct = distinct;
		this.timeout = timeout;
	}

	public void addFieldMeta(FieldMeta meta) {
		String field = meta.getName();
		if (fieldMetaMap.containsKey(field)) {
			throw new SearchException("The field [" + field + "] was already added.");
		}
		if (meta.getField() != null) {
			selectFields.add(field);
		}
		fieldMetaMap.put(field, meta);
	}

	public Class<T> getBeanClass() {
		return beanClass;
	}

	public String getDataSource() {
		return dataSource;
	}

	public SqlSnippet getTableSnippet() {
		return tableSnippet;
	}

	public String getWhere() {
		return whereSnippet.getSql();
	}

	public List<SqlSnippet.SqlPara> getWhereSqlParas() {
		return whereSnippet.getParas();
	}

	public String getGroupBy() {
		return groupBySnippet.getSql();
	}

	public List<SqlSnippet.SqlPara> getGroupBySqlParas() {
		return groupBySnippet.getParas();
	}

	public String getHaving() {
		return havingSnippet.getSql();
	}

	public List<SqlSnippet.SqlPara> getHavingSqlParas() {
		return havingSnippet.getParas();
	}

	public boolean isDistinct() {
		return distinct;
	}

	public Set<String> getFieldSet() {
		return Collections.unmodifiableSet(fieldMetaMap.keySet());
	}

	public List<String> getSelectFields() {
		return Collections.unmodifiableList(selectFields);
	}

	public int getFieldCount() {
		return fieldMetaMap.size();
	}

	public FieldMeta requireFieldMeta(String field) {
		FieldMeta meta = getFieldMeta(field);
		if (meta == null) {
			throw new IllegalStateException("No such field named: " + field);
		}
		return meta;
	}

	public FieldMeta getFieldMeta(String field) {
		if (field != null) {
			return fieldMetaMap.get(field);
		}
		return null;
	}

	/**
	 * 获取某字段的 SQL 片段
	 * @param field Java 字段名
	 * @return SQL 片段
	 */
	public String getFieldSql(String field) {
		FieldMeta meta = requireFieldMeta(field);
		return meta.getFieldSql().getSql();
	}

	public Collection<FieldMeta> getFieldMetas() {
		return Collections.unmodifiableCollection(fieldMetaMap.values());
	}

	public SqlSnippet getOrderBySnippet() {
		return orderBySnippet;
	}

	public boolean isSortable() {
		return sortable;
	}

	public boolean isDistinctOrGroupBy() {
		return distinct || StringUtils.isNotBlank(groupBySnippet.getSql());
	}

	public int getTimeout() {
		return timeout;
	}

}
