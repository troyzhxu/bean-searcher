package cn.zhxu.bs;

import cn.zhxu.bs.param.FetchType;

import java.util.Map;
import java.util.Objects;

/**
 * 请求参数解析器接口
 * @author Troy.Zhou @ 2017-03-20
 * */
public interface ParamResolver {

	/**
	 * @param beanMeta 元数据
	 * @param fetchType Fetch 类型
	 * @param paraMap 原始检索参数
	 * @return SearchParam
	 * @throws IllegalParamException 抛出非法参数异常后将终止 SQL 查询
	 * */
	SearchParam resolve(BeanMeta<?> beanMeta, FetchType fetchType, Map<String, Object> paraMap) throws IllegalParamException;

	/**
	 * 配置类
	 * @since v4.3
	 */
	class Configuration {

		/**
		 * 排序字段参数名
		 */
		private String sortName = "sort";

		/**
		 * 排序方法字段参数名
		 */
		private String orderName = "order";

		/**
		 * 排序参数名（该参数与 {@link #sortName } 和 {@link #orderName } 指定的参数互斥）
		 * 该参数可指定多个排序字段，例如：orderBy=age:desc,dateCreate:asc
		 */
		private String orderByName = "orderBy";

		/**
		 * 参数名分割符
		 */
		private String separator = "-";

		/**
		 * 忽略大小写参数名后缀
		 * 带上该参数会导致字段索引不被使用，查询速度降低，在大数据量下建议使用数据库本身的字符集实现忽略大小写功能。
		 */
		private String ignoreCaseSuffix = "ic";

		/**
		 * 过滤运算符参数名后缀
		 */
		private String operatorSuffix = "op";

		/**
		 * 用于指定只 Select 某些字段的参数名
		 */
		private String onlySelectName = "onlySelect";

		/**
		 * 用于指定不需要 Select 的字段的参数名
		 */
		private String selectExcludeName = "selectExclude";

		/**
		 * 用于指定组表达式参数名
		 */
		private String gexprName = "gexpr";

		/**
		 * 用于控制参数构建器中使用 `groupExpr(..)` 方法指定的组表达式是否合并或覆盖前端参数传来的组表达式
		 */
		private boolean gexprMerge = true;

		/**
		 * 组分割符
		 */
		private String groupSeparator = ".";

		public String separatorOpSuffix() {
			return separator + operatorSuffix;
		}

		public String separatorIcSuffix() {
			return separator + ignoreCaseSuffix;
		}

		public String getSortName() {
			return sortName;
		}

		public void setSortName(String sortName) {
			this.sortName = Objects.requireNonNull(sortName);
		}

		public String getOrderName() {
			return orderName;
		}

		public void setOrderName(String orderName) {
			this.orderName = Objects.requireNonNull(orderName);
		}

		public String getOrderByName() {
			return orderByName;
		}

		public void setOrderByName(String orderByName) {
			this.orderByName = Objects.requireNonNull(orderByName);
		}

		public String getSeparator() {
			return separator;
		}

		public void setSeparator(String separator) {
			this.separator = Objects.requireNonNull(separator);
		}

		public String getIgnoreCaseSuffix() {
			return ignoreCaseSuffix;
		}

		public void setIgnoreCaseSuffix(String ignoreCaseSuffix) {
			this.ignoreCaseSuffix = Objects.requireNonNull(ignoreCaseSuffix);
		}

		public String getOperatorSuffix() {
			return operatorSuffix;
		}

		public void setOperatorSuffix(String operatorSuffix) {
			this.operatorSuffix = Objects.requireNonNull(operatorSuffix);
		}

		public String getOnlySelectName() {
			return onlySelectName;
		}

		public void setOnlySelectName(String onlySelectName) {
			this.onlySelectName = Objects.requireNonNull(onlySelectName);
		}

		public String getSelectExcludeName() {
			return selectExcludeName;
		}

		public void setSelectExcludeName(String selectExcludeName) {
			this.selectExcludeName = Objects.requireNonNull(selectExcludeName);
		}

		public String getGexprName() {
			return gexprName;
		}

		public void setGexprName(String gexprName) {
			this.gexprName = Objects.requireNonNull(gexprName);
		}

		public boolean isGexprMerge() {
			return gexprMerge;
		}

		public void setGexprMerge(boolean gexprMerge) {
			this.gexprMerge = gexprMerge;
		}

		public String getGroupSeparator() {
			return groupSeparator;
		}

		public void setGroupSeparator(String groupSeparator) {
			this.groupSeparator = Objects.requireNonNull(groupSeparator);
		}

	}

}
