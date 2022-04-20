package com.ejlchina.searcher.boot;

import com.ejlchina.searcher.bean.InheritType;
import com.ejlchina.searcher.bean.SortType;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;


@ConfigurationProperties(prefix = "bean-searcher")
public class BeanSearcherProperties {

	// 参数配置
	private final Params params = new Params();

	// SQL 配置
	private final Sql sql = new Sql();

	// 字段转换器配置
	private final FieldConvertor fieldConvertor = new FieldConvertor();

	// 使用 MapSearcher 检索器
	private boolean useMapSearcher = true;

	// 使用 BeanSearcher 检索器
	private boolean useBeanSearcher = true;


	public Params getParams() {
		return params;
	}

	public Sql getSql() {
		return sql;
	}

	public FieldConvertor getFieldConvertor() {
		return fieldConvertor;
	}

	public static class Params {

		// 排序字段参数名
		private String sort = "sort";

		// 排序方法参数名
		private String order = "order";

		// 排序参数名
		private String orderBy = "orderBy";

		// 字段参数名分隔符
		private String separator = "-";

		// 是否忽略大小写字段参数名后缀
		private String ignoreCaseKey = "ic";

		// 检索运算符参数名后缀
		private String operatorKey = "op";

		// 用于指定只 Select 某些字段的参数名
		private String onlySelect = "onlySelect";

		// 用于指定不需要 Select 的字段的参数名
		private String selectExclude = "selectExclude";

		// 参数组
		private final Group group = new Group();

		public static class Group {

			// 是否启用参数组功能
			private boolean enable = true;

			// 组表达式参数名
			private String exprName = "gexpr";

			// 组参数分隔符
			private String separator = ".";

			// 缓存大小
			private int cacheSize = 50;

			public boolean isEnable() {
				return enable;
			}

			public void setEnable(boolean enable) {
				this.enable = enable;
			}

			public String getExprName() {
				return exprName;
			}

			public void setExprName(String exprName) {
				this.exprName = exprName;
			}

			public String getSeparator() {
				return separator;
			}

			public void setSeparator(String separator) {
				this.separator = separator;
			}

			public int getCacheSize() {
				return cacheSize;
			}

			public void setCacheSize(int cacheSize) {
				this.cacheSize = cacheSize;
			}
		}

		// 分页参数配置
		private final PaginationProps pagination = new PaginationProps();

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

		public String getOrderBy() {
			return orderBy;
		}

		public void setOrderBy(String orderBy) {
			this.orderBy = orderBy;
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

		public String getOnlySelect() {
			return onlySelect;
		}

		public void setOnlySelect(String onlySelect) {
			this.onlySelect = onlySelect;
		}

		public String getSelectExclude() {
			return selectExclude;
		}

		public void setSelectExclude(String selectExclude) {
			this.selectExclude = selectExclude;
		}

		public Group getGroup() {
			return group;
		}

		public PaginationProps getPagination() {
			return pagination;
		}

		public static class PaginationProps {

			public static final String TYPE_PAGE = "page";

			public static final String TYPE_OFFSET = "offset";

			/**
			 * 默认分页大小
			 */
			private int defaultSize = 15;

			/**
			 * 分页类型: page 和 offset
			 * */
			private String type = TYPE_PAGE;

			/**
			 * 默认分页大小参数名
			 */
			private String size = "size";

			/**
			 * 页数参数名
			 * 在 type = page 时有效
			 */
			private String page = "page";

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

	public static class Sql {

		public static final String DIALECT_MYSQL = "MYSQL";
		public static final String DIALECT_ORACLE = "ORACLE";
		public static final String DIALECT_POSTGRESQL = "POSTGRESQL";
		public static final String DIALECT_PGSQL = "PGSQL";		// alias for POSTGRESQL

		/**
		 * 数据库方言，默认MySQL，可选：Oracle、PostgreSql、SqlServer
		 */
		private String dialect = DIALECT_MYSQL;

		private final DefaultMapping defaultMapping = new DefaultMapping();

		// 是否使用 DateValueCorrector
		private boolean useDateValueCorrector = true;

		public String getDialect() {
			return dialect;
		}

		public void setDialect(String dialect) {
			this.dialect = dialect;
		}

		public DefaultMapping getDefaultMapping() {
			return defaultMapping;
		}

		public static class DefaultMapping {

			private boolean upperCase = false;
			private String tablePrefix = null;
			private String[] redundantSuffixes;
			private String[] ignoreFields;
			private InheritType inheritType;
			private SortType sortType;

			public boolean isUpperCase() {
				return upperCase;
			}

			public void setUpperCase(boolean upperCase) {
				this.upperCase = upperCase;
			}

			public String getTablePrefix() {
				return tablePrefix;
			}

			public void setTablePrefix(String tablePrefix) {
				this.tablePrefix = tablePrefix;
			}

			public String[] getRedundantSuffixes() {
				return redundantSuffixes;
			}

			public void setRedundantSuffixes(String[] redundantSuffixes) {
				this.redundantSuffixes = redundantSuffixes;
			}

			public String[] getIgnoreFields() {
				return ignoreFields;
			}

			public void setIgnoreFields(String[] ignoreFields) {
				this.ignoreFields = ignoreFields;
			}

			public InheritType getInheritType() {
				return inheritType;
			}

			public void setInheritType(InheritType inheritType) {
				this.inheritType = inheritType;
			}

			public SortType getSortType() {
				return sortType;
			}

			public void setSortType(SortType sortType) {
				this.sortType = sortType;
			}

		}

		public boolean isUseDateValueCorrector() {
			return useDateValueCorrector;
		}

		public void setUseDateValueCorrector(boolean useDateValueCorrector) {
			this.useDateValueCorrector = useDateValueCorrector;
		}

	}

	public static class FieldConvertor {

		/**
		 * 使用 Number to Number 的字段转换器
		 */
		private boolean useNumber = true;

		/**
		 * 使用 String to Number 的字段转换器
		 */
		private boolean useStrNum = true;

		/**
		 * 使用 String | Number to Boolean 的字段转换器
		 */
		private boolean useBool = true;

		/**
		 * BoolFieldConvertor 的假值
		 */
		private String[] boolFalseValues;

		/**
		 * 使用日期字段转换器
		 */
		private boolean useDate = true;

		/**
		 * 使用日期格式化字段转换器
		 */
		private boolean useDateFormat = true;

		/**
		 * 使用时间字段转换器
		 */
		private boolean useTime = true;

		/**
		 * 时区
		 */
		private ZoneId zoneId = null;

		/**
		 * 使用日期格式化字段转换器的格式
		 */
		private Map<String, String> dateFormats = new HashMap<>();

		/**
		 * 使用枚举字段转换器
		 */
		private boolean useEnum = true;

		/**
		 * 是否使用 B2M 字段转换器
		 */
		private boolean useB2M = false;

		public boolean isUseNumber() {
			return useNumber;
		}

		public void setUseNumber(boolean useNumber) {
			this.useNumber = useNumber;
		}

		public boolean isUseStrNum() {
			return useStrNum;
		}

		public void setUseStrNum(boolean useStrNum) {
			this.useStrNum = useStrNum;
		}

		public boolean isUseBool() {
			return useBool;
		}

		public void setUseBool(boolean useBool) {
			this.useBool = useBool;
		}

		public String[] getBoolFalseValues() {
			return boolFalseValues;
		}

		public void setBoolFalseValues(String[] boolFalseValues) {
			this.boolFalseValues = boolFalseValues;
		}

		public boolean isUseDate() {
			return useDate;
		}

		public void setUseDate(boolean useDate) {
			this.useDate = useDate;
		}

		public boolean isUseDateFormat() {
			return useDateFormat;
		}

		public void setUseDateFormat(boolean useDateFormat) {
			this.useDateFormat = useDateFormat;
		}

		public Map<String, String> getDateFormats() {
			return dateFormats;
		}

		public void setDateFormats(Map<String, String> dateFormats) {
			this.dateFormats = dateFormats;
		}

		public boolean isUseTime() {
			return useTime;
		}

		public void setUseTime(boolean useTime) {
			this.useTime = useTime;
		}

		public ZoneId getZoneId() {
			return zoneId;
		}

		public void setZoneId(ZoneId zoneId) {
			this.zoneId = zoneId;
		}

		public boolean isUseEnum() {
			return useEnum;
		}

		public void setUseEnum(boolean useEnum) {
			this.useEnum = useEnum;
		}

		public boolean isUseB2M() {
			return useB2M;
		}

		public void setUseB2M(boolean useB2M) {
			this.useB2M = useB2M;
		}

	}

	public boolean isUseMapSearcher() {
		return useMapSearcher;
	}

	public void setUseMapSearcher(boolean useMapSearcher) {
		this.useMapSearcher = useMapSearcher;
	}

	public boolean isUseBeanSearcher() {
		return useBeanSearcher;
	}

	public void setUseBeanSearcher(boolean useBeanSearcher) {
		this.useBeanSearcher = useBeanSearcher;
	}

}
