package cn.zhxu.bs.boot.prop;

import cn.zhxu.bs.BeanSearcher;
import cn.zhxu.bs.MapSearcher;
import cn.zhxu.bs.ParamResolver;
import cn.zhxu.bs.convertor.DateParamConvertor;
import cn.zhxu.bs.convertor.DateTimeParamConvertor;
import cn.zhxu.bs.convertor.TimeParamConvertor;
import cn.zhxu.bs.filter.ArrayValueParamFilter;
import cn.zhxu.bs.filter.JsonArrayParamFilter;
import cn.zhxu.bs.filter.SizeLimitParamFilter;
import cn.zhxu.bs.filter.SuffixOpParamFilter;
import cn.zhxu.bs.group.DefaultGroupResolver;
import cn.zhxu.bs.util.MapBuilder;
import cn.zhxu.bs.util.MapUtils;

import java.time.ZoneId;
import java.util.Map;


public class Params {

    /**
     * 排序字段参数名，默认为 `sort`，
     * @see ParamResolver.Configuration#sort(String)
     */
    private String sort = "sort";

    /**
     * 排序方法参数名，默认为 `order`，
     * @see ParamResolver.Configuration#order(String)
     */
    private String order = "order";

    /**
     * 排序参数名，默认为 `orderBy`，
     * @see ParamResolver.Configuration#orderBy(String)
     */
    private String orderBy = "orderBy";

    /**
     * 字段参数名分隔符，默认为 `-`，
     * @see ParamResolver.Configuration#separator(String)
     */
    private String separator = "-";

    /**
     * 是否忽略大小写字段参数名的后缀，默认为 `ic`，
     * @see ParamResolver.Configuration#ic(String)
     */
    private String ignoreCaseKey = "ic";

    /**
     * 检索运算符参数名后缀，默认为 `op`，
     * @see ParamResolver.Configuration#op(String)
     */
    private String operatorKey = "op";

    /**
     * 指定只 Select 某些字段的参数名，默认为 `onlySelect`，
     * @see ParamResolver.Configuration#onlySelect(String)
     */
    private String onlySelect = "onlySelect";

    /**
     * 指定 Select 排除某些字段的参数名，默认为 `selectExclude`，
     * @see ParamResolver.Configuration#selectExclude(String)
     */
    private String selectExclude = "selectExclude";

    /**
     * 参数非法时是否抛出异常
     * @since v4.2.3
     */
    private boolean failOnError = false;

    /**
     * 过滤器配置
     */
    private final Filter filter = new Filter();

    public static class Filter {

        /**
         * 是否启用检索参数数量限制，默认为 true，用于限制检索参数的个数，配合 {@link #maxParaMapSize } 参数一起使用
         * @see SizeLimitParamFilter
         * @since v4.3.0
         */
        private boolean useSizeLimit = true;

        /**
         * 是否启用 数组参数值，默认为 true，用于配合 {@link MapUtils#flat(Map)} 与 {@link MapUtils#flatBuilder(Map)} 方法，来兼容数组参数值的用法。例如前端传参：age=20 & age=30 & age-op=bt
         * 例如前端传参：age=20 & age=30 & age-op=bt
         * @see ArrayValueParamFilter
         * @since v4.3.0
         */
        private boolean useArrayValue = true;

        /**
         * 是否启用 Json 数组参数值，默认为 false，用于简化前端传参，例如 age=[20,30] 替代 age-0=20 & age-1=30 <br>
         * 但需要注意的是，即使该参数为 true, 也不一定能成功启用该过滤器，您必须还得添加 <a href="https://gitee.com/troyzhxu/xjsonkit">xjsonkit</a> 的 json 相关实现的依赖才可以，目前这些依赖有（你可以任选其一）：
         * <pre>
         * implementation 'cn.zhxu:xjsonkit-fastjson:最新版本' // Fastjson 实现
         * implementation 'cn.zhxu:xjsonkit-fastjson2:最新版本'// Fastjson2 实现
         * implementation 'cn.zhxu:xjsonkit-gson:最新版本'     // Gson 实现
         * implementation 'cn.zhxu:xjsonkit-jackson:最新版本'  // Jackson 实现
         * implementation 'cn.zhxu:xjsonkit-snack3:最新版本'   // Snack3 实现
         * </pre>
         * @see JsonArrayParamFilter
         * @since v4.3.0
         **/
        private boolean useJsonArray = false;

        /**
         * 是否启用后缀运算符，默认为 false，用于简化前端传参，例如 age-gt=25 替代 age=25 & age-op=gt
         * @see SuffixOpParamFilter
         * @since v4.3.0
         */
        private boolean useSuffixOp = false;

        /**
         * 检索参数的最大允许数量，用于风险控制，避免前端恶意传参生成过于复杂的 SQL
         * @see SizeLimitParamFilter
         */
        private int maxParaMapSize = 150;

        public boolean isUseSizeLimit() {
            return useSizeLimit;
        }

        public void setUseSizeLimit(boolean useSizeLimit) {
            this.useSizeLimit = useSizeLimit;
        }

        public boolean isUseArrayValue() {
            return useArrayValue;
        }

        public void setUseArrayValue(boolean useArrayValue) {
            this.useArrayValue = useArrayValue;
        }

        public boolean isUseJsonArray() {
            return useJsonArray;
        }

        public void setUseJsonArray(boolean useJsonArray) {
            this.useJsonArray = useJsonArray;
        }

        public boolean isUseSuffixOp() {
            return useSuffixOp;
        }

        public void setUseSuffixOp(boolean useSuffixOp) {
            this.useSuffixOp = useSuffixOp;
        }

        public int getMaxParaMapSize() {
            return maxParaMapSize;
        }

        public void setMaxParaMapSize(int maxParaMapSize) {
            this.maxParaMapSize = maxParaMapSize;
        }

    }

    /**
     * 参数组相关配置
     */
    private final Group group = new Group();

    public static class Group {

        /**
         * 是否启用参数组功能，默认为 true
         */
        private boolean enable = true;

        /**
         * 组表达式参数名，默认为 `gexpr`，
         * @see ParamResolver.Configuration#gexpr(String)
         */
        private String exprName = "gexpr";

        /**
         * 用于控制参数构建器中使用 `groupExpr(..)` 方法指定的组表达式是否合并或覆盖前端参数传来的组表达式
         * @see ParamResolver.Configuration#gexprMerge(boolean)
         */
        private boolean mergeable = true;

        /**
         * 组参数分隔符，默认为 `.`，
         * @see ParamResolver.Configuration#separator(String)
         */
        private String separator = ".";

        /**
         * 组表达式缓存大小，默认为 50
         */
        private int cacheSize = 50;

        /**
         * 表达式最大允许长度，风险控制，用于避免前端恶意传参生成过于复杂的 SQL
         * @see DefaultGroupResolver#setMaxExprLength(int)
         */
        private int maxExprLength = 50;

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

        public boolean isMergeable() {
            return mergeable;
        }

        public void setMergeable(boolean mergeable) {
            this.mergeable = mergeable;
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

        public int getMaxExprLength() {
            return maxExprLength;
        }

        public void setMaxExprLength(int maxExprLength) {
            this.maxExprLength = maxExprLength;
        }

    }

    private final Convertor convertor = new Convertor();

    public static class Convertor {

        private DateParamConvertor.Target dateTarget = DateParamConvertor.Target.SQL_DATE;
        private DateTimeParamConvertor.Target dateTimeTarget = DateTimeParamConvertor.Target.SQL_TIMESTAMP;
        private TimeParamConvertor.Target timeTarget = TimeParamConvertor.Target.SQL_TIME;

        /**
         * 时区 ID，将作为 {@link DateTimeParamConvertor } 的参数，默认取值：{@link ZoneId#systemDefault() }
         * @see DateTimeParamConvertor#setZoneId(ZoneId)
         */
        private ZoneId zoneId = null;

        public DateParamConvertor.Target getDateTarget() {
            return dateTarget;
        }

        public void setDateTarget(DateParamConvertor.Target dateTarget) {
            this.dateTarget = dateTarget;
        }

        public DateTimeParamConvertor.Target getDateTimeTarget() {
            return dateTimeTarget;
        }

        public void setDateTimeTarget(DateTimeParamConvertor.Target dateTimeTarget) {
            this.dateTimeTarget = dateTimeTarget;
        }

        public TimeParamConvertor.Target getTimeTarget() {
            return timeTarget;
        }

        public void setTimeTarget(TimeParamConvertor.Target timeTarget) {
            this.timeTarget = timeTarget;
        }

        public ZoneId getZoneId() {
            return zoneId;
        }

        public void setZoneId(ZoneId zoneId) {
            this.zoneId = zoneId;
        }

    }

    /**
     * 分页相关配置
     */
    private final Pagination pagination = new Pagination();

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

    public Convertor getConvertor() {
        return convertor;
    }

    public boolean isFailOnError() {
        return failOnError;
    }

    public void setFailOnError(boolean failOnError) {
        this.failOnError = failOnError;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public static class Pagination {

        public static final String TYPE_PAGE = "page";

        public static final String TYPE_OFFSET = "offset";

        /**
         * 默认分页大小，默认为 15
         */
        private int defaultSize = 15;

        /**
         * 分页类型，可选：`page` 和 `offset`，默认为 `page`
         * */
        private String type = TYPE_PAGE;

        /**
         * 分页大小参数名，默认为 `size`
         */
        private String size = "size";

        /**
         * 页码参数名（仅在 type = `page` 时有效），默认为 `page`
         */
        private String page = "page";

        /**
         * 页偏移参数名（仅在 type = `offset` 时有效），默认为 `offset`
         */
        private String offset = "offset";

        /**
         * 起始页码 或 起始页偏移，默认为 0，
         * 注意：该配置对方法 {@link MapBuilder#page(long, int)} } 与 {@link MapBuilder#limit(long, int)} 无效
         */
        private int start = 0;

        /**
         * 分页保护：每页最大允许查询条数，默认为 100，
         * 注意：该配置对 {@link BeanSearcher#searchAll(Class)} 与 {@link MapSearcher#searchAll(Class)} 方法无效
         */
        private int maxAllowedSize = 100;

        /**
         * 分页保护：最大允许偏移量，如果是 page 分页，则最大允许页码是 maxAllowedOffset / size
         */
        private long maxAllowedOffset = 20000;

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

        public long getMaxAllowedOffset() {
            return maxAllowedOffset;
        }

        public void setMaxAllowedOffset(long maxAllowedOffset) {
            this.maxAllowedOffset = maxAllowedOffset;
        }

    }

    public Filter getFilter() {
        return filter;
    }

}
