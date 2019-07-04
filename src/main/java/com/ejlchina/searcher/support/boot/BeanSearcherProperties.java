package com.ejlchina.searcher.support.boot;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.ejlchina.searcher.implement.convertor.DefaultFieldConvertor;


@ConfigurationProperties(prefix = "spring.bean-searcher")
public class BeanSearcherProperties {

	
	private String[] packages;

	private int prifexSeparatorLength = 1;
	
	private final ParamsPorps params = new ParamsPorps();
	
	private final SqlProps sql = new SqlProps();

	private final FieldConvertorProps fieldConvertor = new FieldConvertorProps();
	
	public String[] getPackages() {
		return packages;
	}

	public void setPackages(String[] packages) {
		this.packages = packages;
	}

	public int getPrifexSeparatorLength() {
		return prifexSeparatorLength;
	}

	public void setPrifexSeparatorLength(int prifexSeparatorLength) {
		this.prifexSeparatorLength = prifexSeparatorLength;
	}
	
	public ParamsPorps getParams() {
		return params;
	}

	public SqlProps getSql() {
		return sql;
	}

	public FieldConvertorProps getFieldConvertor() {
		return fieldConvertor;
	}


	public static class ParamsPorps {
		
		
		private String sort = "sort";
		
		private String order = "order";
		
		private String separator = "-";
		
		private String ignoreCaseKey = "ic";
		
		private String operatorKey = "op";

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
		 * 最大允许分页大小
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
	
	
	public static class SqlProps {
		
		public static final String DIALECT_MYSQL = "mysql";
		public static final String DIALECT_ORACLE = "oracle";
		public static final String DIALECT_POSTGRE_SQL = "postgresql";
		public static final String DIALECT_SQL_SERVER = "sqlserver";
		
		private String dialect = DIALECT_MYSQL;

		public String getDialect() {
			return dialect;
		}

		public void setDialect(String dialect) {
			this.dialect = dialect;
		}
		
	}
	
	
	public static class FieldConvertorProps {
		
		
		private String[] trues = DefaultFieldConvertor.DEFAULT_TRUES;
		
		private String[] falses = DefaultFieldConvertor.DEFAULT_FALSES;

		
		public String[] getTrues() {
			return trues;
		}

		public void setTrues(String[] trues) {
			this.trues = trues;
		}

		public String[] getFalses() {
			return falses;
		}

		public void setFalses(String[] falses) {
			this.falses = falses;
		}
		
	}
	
	
	
}
