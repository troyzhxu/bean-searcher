package cn.zhxu.bs.implement;

import cn.zhxu.bs.*;
import cn.zhxu.bs.convertor.*;
import cn.zhxu.bs.filter.ArrayValueParamFilter;
import cn.zhxu.bs.filter.SizeLimitParamFilter;
import cn.zhxu.bs.group.DefaultGroupResolver;
import cn.zhxu.bs.group.Group;
import cn.zhxu.bs.group.GroupResolver;
import cn.zhxu.bs.param.FetchType;
import cn.zhxu.bs.param.FieldParam;
import cn.zhxu.bs.param.OrderBy;
import cn.zhxu.bs.param.Paging;
import cn.zhxu.bs.util.*;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static cn.zhxu.bs.group.ExprParser.*;

/**
 * @author Troy.Zhou @ 2017-03-20
 * 
 * 默认查询参数解析器
 */
public class DefaultParamResolver implements ParamResolver {

    public static final Pattern INDEX_PATTERN = Pattern.compile("\\d+");

    /**
     * 分页参数提取器
     */
    private PageExtractor pageExtractor = new PageSizeExtractor();

    /**
     * 参数过滤器
     */
    private List<ParamFilter> paramFilters = new ArrayList<>();

    /**
     * @since v3.8.0
     * 用于对参数值进行转换
     */
    private List<FieldConvertor.ParamConvertor> convertors = new ArrayList<>();

    /**
     * 字段运算符池
     */
    private FieldOpPool fieldOpPool = FieldOpPool.DEFAULT;

    /**
     * @since v3.5.0
     * 用于解析组表达式
     */
    private GroupResolver groupResolver = new DefaultGroupResolver();

    /**
     * @since v4.3.0
     * 配置参数
     */
    private Configuration configuration = new Configuration();

    public DefaultParamResolver() {
        convertors.add(new BoolParamConvertor());
        convertors.add(new NumberParamConvertor());
        convertors.add(new DateParamConvertor());
        convertors.add(new TimeParamConvertor());
        convertors.add(new DateTimeParamConvertor());
        convertors.add(new EnumParamConvertor());
        paramFilters.add(new SizeLimitParamFilter());
        paramFilters.add(new ArrayValueParamFilter());
    }

    public DefaultParamResolver(List<FieldConvertor.ParamConvertor> convertors, List<ParamFilter> paramFilters) {
        setConvertors(convertors);
        setParamFilters(paramFilters);
    }

    @Override
    public SearchParam resolve(BeanMeta<?> beanMeta, FetchType fetchType, Map<String, Object> paraMap) throws IllegalParamException {
        for (ParamFilter filter: paramFilters) {
            if (paraMap == null) {
                paraMap = new HashMap<>();
            }
            paraMap = filter.doFilter(beanMeta, paraMap);
        }
        if (paraMap == null) {
            paraMap = Collections.emptyMap();
        }
        return doResolve(beanMeta, fetchType, paraMap);
    }

    public SearchParam doResolve(BeanMeta<?> beanMeta, FetchType fetchType, Map<String, Object> paraMap) throws IllegalParamException {
        List<String> fetchFields = resolveFetchFields(beanMeta, fetchType, paraMap);
        Group<List<FieldParam>> paramsGroup = resolveParamsGroup(beanMeta.getFieldMetas(), paraMap);
        Paging paging = resolvePaging(fetchType, paraMap);
        SearchParam searchParam = new SearchParam(paraMap, fetchType, fetchFields, paramsGroup, paging);
        if (fetchType.shouldQueryList() && beanMeta.isSortable()) {
            // 只有列表检索，才需要排序
            Set<String> fieldSet = beanMeta.getFieldSet();
            for (OrderBy orderBy : resolveOrderBys(paraMap)) {
                if (orderBy.isValid(fieldSet)) {
                    searchParam.addOrderBy(orderBy);
                } else {
                    throw new IllegalParamException("Invalid " + orderBy + " on " + beanMeta.getBeanClass());
                }
            }
        }
        return searchParam;
    }

    public Paging resolvePaging(FetchType fetchType, Map<String, Object> paraMap) throws IllegalParamException {
        if (fetchType.canPaging()) {
            Paging paging = pageExtractor.extract(paraMap);
            if (fetchType.isFetchFirst()) {
                paging.setSize(1);
            }
            return paging;
        }
        return null;
    }

    public List<String> resolveFetchFields(BeanMeta<?> beanMeta, FetchType fetchType, Map<String, Object> paraMap) {
        if (fetchType.shouldQueryList() || beanMeta.isDistinctOrGroupBy()) {
            List<String> fieldList = beanMeta.getSelectFields();
            List<String> onlySelect = ObjectUtils.toList(getOnlySelect(paraMap))
                    .stream().filter(fieldList::contains)
                    .collect(Collectors.toList());
            List<String> selectExclude = ObjectUtils.toList(getSelectExclude(paraMap));
            return (onlySelect.isEmpty() ? fieldList : onlySelect).stream()
                    .filter(f -> !selectExclude.contains(f))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    protected Object getSelectExclude(Map<String, Object> paraMap) {
        Object value = paraMap.get(MapBuilder.SELECT_EXCLUDE);
        if (value != null) {
            return value;
        }
        return paraMap.get(configuration.selectExclude());
    }

    protected Object getOnlySelect(Map<String, Object> paraMap) {
        Object value = paraMap.get(MapBuilder.ONLY_SELECT);
        if (value != null) {
            return value;
        }
        return paraMap.get(configuration.onlySelect());
    }

    public Group<List<FieldParam>> resolveParamsGroup(Collection<FieldMeta> fieldMetas, Map<String, Object> paraMap) throws IllegalParamException {
        Map<String, List<FieldParam>> holder = new HashMap<>();
        return groupResolver.resolve(getGroupExpr(paraMap))
                .transform(gKey -> {
                    List<FieldParam> params = holder.get(gKey);
                    if (params == null) {
                        MapWrapper mapWrapper;
                        if (gKey != null) {
                            mapWrapper = new MapWrapper(paraMap, gKey, configuration.groupSeparator());
                        } else {
                            mapWrapper = new MapWrapper(paraMap);
                        }
                        params = extractFieldParams(fieldMetas, mapWrapper);
                        holder.put(gKey, params);
                    }
                    return params;
                })
                .filter(list -> !list.isEmpty());
    }

    protected String getGroupExpr(Map<String, Object> paraMap) throws IllegalParamException {
        String gExpr = ObjectUtils.string(paraMap.get(MapBuilder.GROUP_EXPR));
        String expr = ObjectUtils.string(paraMap.get(configuration.gexpr()));
        if (StringUtils.isBlank(gExpr)) {
            gExpr = expr;
        } else if (configuration.gexprMerge() && StringUtils.isNotBlank(expr)) {
            gExpr = BRACKET_LEFT + gExpr + BRACKET_RIGHT + AND_OP + BRACKET_LEFT + expr + BRACKET_RIGHT;
        }
        if (StringUtils.isNotBlank(gExpr)) {
            if (gExpr.contains(Builder.ROOT_GROUP)) {
                throw new IllegalParamException("Invalid groupExpr [" + gExpr + "] because of containing '" + Builder.ROOT_GROUP + "'.");
            }
            gExpr = Builder.ROOT_GROUP + AND_OP + BRACKET_LEFT + gExpr + BRACKET_RIGHT;
        }
        return gExpr;
    }

    private List<FieldParam> extractFieldParams(Collection<FieldMeta> fieldMetas, MapWrapper paraMap) {
        Map<String, Set<Integer>> fieldIndicesMap = new HashMap<>();
        for (String key : paraMap.keySet()) {
            int index = key.lastIndexOf(configuration.separator());
            if (index > 0 && key.length() > index + 1) {
                String suffix = key.substring(index + 1);
                if (INDEX_PATTERN.matcher(suffix).matches()) {
                    String field = key.substring(0, index);
                    mapFieldIndex(fieldIndicesMap, field, Integer.parseInt(suffix));
                }
            }
            mapFieldIndex(fieldIndicesMap, key, 0);
        }
        List<FieldParam> fieldParams = new ArrayList<>();
        for (FieldMeta meta : fieldMetas) {
            if (!meta.isConditional()) {
                continue;
            }
            Set<Integer> indices = fieldIndicesMap.get(meta.getName());
            FieldParam param = toFieldParam(meta, indices, paraMap);
            if (param != null) {
                fieldParams.add(param);
            }
        }
        return fieldParams;
    }

    protected void mapFieldIndex(Map<String, Set<Integer>> fieldIndicesKeysMap, String field, int index) {
        fieldIndicesKeysMap.computeIfAbsent(field, k -> new HashSet<>(2)).add(index);
    }

    protected FieldParam getFieldParam(MapWrapper paraMap, String field) {
        Object value = paraMap.get0(MapBuilder.FIELD_PARAM + field);
        if (value instanceof FieldParam) {
            return (FieldParam) value;
        }
        return null;
    }

    protected FieldParam toFieldParam(FieldMeta meta, Set<Integer> indices, MapWrapper paraMap) {
        String field = meta.getName();
        FieldParam param = getFieldParam(paraMap, field);
        FieldOp operator = allowedOperator(toOperator(field, paraMap, param), meta.getOnlyOn());
        if (operator == null) {
            // 表示该字段不支持 op 的检索
            return null;
        }
        if (operator.lonely()) {
            if (param == null) {
                return new FieldParam(field, operator);
            }
            return new FieldParam(field, operator, param.getValueList(), param.isIgnoreCase());
        }
        if ((indices == null || indices.isEmpty()) && param == null) {
            return null;
        }
        List<FieldParam.Value> values = param != null ? param.getValueList() : new ArrayList<>();
        if (values.isEmpty() && indices != null) {
            for (int index : indices) {
                Object value = paraMap.get1(field + configuration.separator() + index);
                if (index == 0 && value == null) {
                    value = paraMap.get1(field);
                }
                value = convertParamValue(meta, value);
                values.add(new FieldParam.Value(value, index));
            }
        } else if (!values.isEmpty()) {
            for (int index = 0; index < values.size(); index++) {
                FieldParam.Value value = values.get(index);
                if (value != null) {
                    Object v = convertParamValue(meta, value.getValue());
                    values.set(index, new FieldParam.Value(v, index));
                }
            }
        }
        if (isAllEmpty(values)) {
            return null;
        }
        Boolean ignoreCase = null;
        if (param != null) {
            ignoreCase = param.isIgnoreCase();
        }
        if (ignoreCase == null) {
            ignoreCase = ObjectUtils.toBoolean(paraMap.get1(field + configuration.icSuffix()));
        }
        return new FieldParam(field, operator, values, ignoreCase);
    }

    protected Object convertParamValue(FieldMeta meta, Object value) {
        if (value == null) {
            return null;
        }
        Class<?> type = meta.getDbType().getType();
        if (type != null && type.isInstance(value)) {
            return value;
        }
        Class<?> vType = value.getClass();
        for (FieldConvertor.ParamConvertor convertor : convertors) {
            if (convertor.supports(meta, vType)) {
                return convertor.convert(meta, value);
            }
        }
        return value;
    }

    protected boolean isAllEmpty(List<FieldParam.Value> values) {
        for (FieldParam.Value value : values) {
            if (!value.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    protected FieldOp toOperator(String field, MapWrapper paraMap, FieldParam param) {
        if (param != null) {
            Object op = param.getOperator();
            if (op instanceof FieldOp) {
                FieldOp fieldOp = (FieldOp) op;
                if (fieldOp.isNonPublic()) {
                    return fieldOp;
                }
            }
            if (op != null) {
                return fieldOpPool.getFieldOp(op);
            }
        }
        Object value = paraMap.get1(field + configuration.opSuffix());
        return fieldOpPool.getFieldOp(value);
    }

    protected FieldOp allowedOperator(FieldOp op, Class<? extends FieldOp>[] onlyOn) {
        if (op == null) {
            Object tOp = onlyOn.length == 0 ? FieldOps.Equal : onlyOn[0];
            return fieldOpPool.getFieldOp(tOp);
        }
        if (onlyOn.length == 0) {
            return op;
        }
        Class<? extends FieldOp> opCls = op.getClass();
        for (Class<? extends FieldOp> clazz : onlyOn) {
            if (clazz == opCls) {
                return op;
            }
        }
        // 不在 onlyOn 的允许范围内时，则使用 onlyOn 中的第一个
        return fieldOpPool.getFieldOp(onlyOn[0]);
    }

    public List<OrderBy> resolveOrderBys(Map<String, Object> paraMap) {
        Object list = paraMap.get(MapBuilder.ORDER_BY);
        if (list instanceof List) {
            @SuppressWarnings("all")
            List<OrderBy> orderBys = (List<OrderBy>) list;
            return orderBys;
        }
        String string = ObjectUtils.string(paraMap.get(configuration.orderBy()));
        if (StringUtils.isNotBlank(string)) {
            return Arrays.stream(string.split(","))
                    .map(str -> {
                        String[] splits = str.split(":");
                        if (splits.length == 1 && StringUtils.isNotBlank(splits[0])) {
                            return new OrderBy(splits[0], null);
                        }
                        if (splits.length == 2 && StringUtils.isNotBlank(splits[0])) {
                            return new OrderBy(splits[0], splits[1]);
                        }
                        return null;
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }
        String sort = ObjectUtils.string(paraMap.get(configuration.sort()));
        String order = ObjectUtils.string(paraMap.get(configuration.order()));
        if (StringUtils.isNotBlank(sort)) {
            return Collections.singletonList(new OrderBy(sort, order));
        }
        return Collections.emptyList();
    }

    public PageExtractor getPageExtractor() {
        return pageExtractor;
    }

    public void setPageExtractor(PageExtractor pageExtractor) {
        this.pageExtractor = Objects.requireNonNull(pageExtractor);
    }

    public List<ParamFilter> getParamFilters() {
        return paramFilters;
    }

    public void addParamFilter(ParamFilter paramFilter) {
        paramFilters.add(Objects.requireNonNull(paramFilter));
    }

    public void setParamFilters(List<ParamFilter> paramFilters) {
        this.paramFilters = Objects.requireNonNull(paramFilters);
    }

    public FieldOpPool getFieldOpPool() {
        return fieldOpPool;
    }

    public void setFieldOpPool(FieldOpPool fieldOpPool) {
        this.fieldOpPool = Objects.requireNonNull(fieldOpPool);
    }

    public GroupResolver getGroupResolver() {
        return groupResolver;
    }

    public void setGroupResolver(GroupResolver groupResolver) {
        this.groupResolver = Objects.requireNonNull(groupResolver);
    }

    public List<FieldConvertor.ParamConvertor> getConvertors() {
        return convertors;
    }

    public void setConvertors(List<FieldConvertor.ParamConvertor> convertors) {
        this.convertors = Objects.requireNonNull(convertors) ;
    }

    public void addConvertor(FieldConvertor.ParamConvertor convertor) {
        this.convertors.add(Objects.requireNonNull(convertor));
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = Objects.requireNonNull(configuration);
    }

}
