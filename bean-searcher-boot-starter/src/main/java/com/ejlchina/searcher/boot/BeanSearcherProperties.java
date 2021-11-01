package com.ejlchina.searcher.boot;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.ejlchina.searcher.implement.BoolFieldConvertor;


@ConfigurationProperties(prefix = "bean-searcher")
public class BeanSearcherProperties {

	/**
	 * 参数配置
	 */
	private final ParamsProps params = new ParamsProps();

	/**
	 * SQL配置
	 */
	private final SqlProps sql = new SqlProps();

	public ParamsProps getParams() {
		return params;
	}

	public SqlProps getSql() {
		return sql;
	}


	public static class ParamsProps {

		/**
		 * 排序字段参数名
		 */
		private String sort = "sort";

		/**
		 * 排序方法参数名
		 */
		private String order = "order";

		/**
		 * 字段参数名分隔符
		 */
		private String separator = "-";

		/**
		 * 是否忽略大小写字段参数名后缀
		 */
		private String ignoreCaseKey = "ic";

		/**
		 * 检索运算符参数名后缀
		 */
		private String operatorKey = "op";

		/**
		 * 分页参数配置
		 */
		private final PaginationPorps pagination = new PaginationPorps();
		
		
		public String getSort() {
			return sort;
		}

		public void setSort(String sort) {
			this.sort = sort;
		}

		public String getOrder() {
			return order;
		}

		public void setOrder(String order) {
			this.order = order;
		}

		public String getSeparator() {
			return separator;
		}

		public void setSeparator(String separator) {
			this.separator = separator;
		}

		public String getIgnoreCaseKey() {
			return ignoreCaseKey;
		}

		public void setIgnoreCaseKey(String ignoreCaseKey) {
			this.ignoreCaseKey = ignoreCaseKey;
		}

		public String getOperatorKey() {
			return operatorKey;
		}

		public void setOperatorKey(String operatorKey) {
			this.operatorKey = operatorKey;
		}

		public PaginationPorps getPagination() {
			return pagination;
		}


		public static class PaginationPorps {

			public static final String TYPE_PAGE = "page";

			public static final String TYPE_OFFSET = "offset";

			/**
			 * 默认分页大小
			 */
			private int defaultSize = 15;

			/**
			 * 分页类型:page 和  offset
			 * */
			private String type = TYPE_PAGE;

			/**
			 * 默认分页大小参数名
			 * 在 type = page 时有效
			 */
			private String size = "size";

			/**
			 * 页数参数名
			 * 在 type = page 时有效
			 */
			private String page = "page";

			/**
			 * 分页大小参数名
			 * 在 type = offset 时有效
			 */
			private String max = "max";

			/**
			 * 偏移分页参数名
			 * 在 type = offset 时有效
			 */
			private String offset = "offset";

			/**
			 * 起始页码或起始偏移量
			 */
			private int start = 0;

			/**
			 * 最大允许每页大小
			 */
			private int maxAllowedSize = 100;


			public int getDefaultSize() {
				return defaultSize;
			}

			public void setDefaultSize(int defaultSize) {
				this.defaultSize = defaultSize;
			}

			public String getType() {
				return type;
			}

			public void setType(String type) {
				this.type = type;
			}

			public String getSize() {
				return size;
			}

			public void setSize(String size) {
				this.size = size;
			}

			public String getPage() {
				return page;
			}

			public void setPage(String page) {
				this.page = page;
			}

			public String getMax() {
				return max;
			}

			public void setMax(String max) {
				this.max = max;
			}

			public String getOffset() {
				return offset;
			}

			public void setOffset(String offset) {
				this.offset = offset;
			}

			public int getStart() {
				return start;
			}

			public void setStart(int start) {
				this.start = start;
			}

			public int getMaxAllowedSize() {
				return maxAllowedSize;
			}

			public void setMaxAllowedSize(int maxAllowedSize) {
				this.maxAllowedSize = maxAllowedSize;
			}

		}

	}
	
	public static class SqlProps {
		
		public static final String DIALECT_MYSQL = "mysql";
		public static final String DIALECT_ORACLE = "oracle";

		/**
		 * 数据库方言，默认MySQL，可选：Oracle、PostgreSql、SqlServer
		 */
		private String dialect = DIALECT_MYSQL;

		public String getDialect() {
			return dialect;
		}

		public void setDialect(String dialect) {
			this.dialect = dialect;
		}
		
	}
	
}
