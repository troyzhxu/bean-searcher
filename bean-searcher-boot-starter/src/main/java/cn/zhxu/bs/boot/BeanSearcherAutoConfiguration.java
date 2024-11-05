package cn.zhxu.bs.boot;

import cn.zhxu.bs.*;
import cn.zhxu.bs.FieldConvertor.BFieldConvertor;
import cn.zhxu.bs.FieldConvertor.MFieldConvertor;
import cn.zhxu.bs.boot.prop.BeanSearcherFieldConvertor;
import cn.zhxu.bs.boot.prop.BeanSearcherParams;
import cn.zhxu.bs.boot.prop.BeanSearcherProperties;
import cn.zhxu.bs.boot.prop.BeanSearcherSql;
import cn.zhxu.bs.convertor.*;
import cn.zhxu.bs.dialect.*;
import cn.zhxu.bs.filter.ArrayValueParamFilter;
import cn.zhxu.bs.filter.JsonArrayParamFilter;
import cn.zhxu.bs.filter.SizeLimitParamFilter;
import cn.zhxu.bs.filter.SuffixOpParamFilter;
import cn.zhxu.bs.group.DefaultGroupResolver;
import cn.zhxu.bs.group.ExprParser;
import cn.zhxu.bs.group.GroupPair;
import cn.zhxu.bs.group.GroupResolver;
import cn.zhxu.bs.implement.*;
import cn.zhxu.bs.util.LRUCache;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;

import javax.sql.DataSource;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;


@Configuration
@EnableConfigurationProperties({
        BeanSearcherProperties.class,
        BeanSearcherFieldConvertor.class,
        BeanSearcherParams.class,
        BeanSearcherSql.class
})
public class BeanSearcherAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(PageExtractor.class)
    public PageExtractor pageExtractor(BeanSearcherParams config) {
        BeanSearcherParams.Pagination conf = config.getPagination();
        String type = conf.getType();
        BasePageExtractor extractor;
        if (BeanSearcherParams.Pagination.TYPE_PAGE.equals(type)) {
            PageSizeExtractor p = new PageSizeExtractor();
            p.setPageName(conf.getPage());
            extractor = p;
        }  else
        if (BeanSearcherParams.Pagination.TYPE_OFFSET.equals(type)) {
            PageOffsetExtractor p = new PageOffsetExtractor();
            p.setOffsetName(conf.getOffset());
            extractor = p;
        } else {
            throw new IllegalConfigException("Invalid config: [bean-searcher.params.pagination.type: " + type + "], only 'page' / 'offset' allowed.");
        }
        int defaultSize = conf.getDefaultSize();
        int maxAllowedSize = conf.getMaxAllowedSize();
        long maxAllowedOffset = conf.getMaxAllowedOffset();
        if (defaultSize > maxAllowedSize) {
            throw new IllegalConfigException("Invalid config: [bean-searcher.params.pagination.default-size: " + defaultSize + "] can not greater than [bean-searcher.params.pagination.max-allowed-size: " + maxAllowedSize + "].");
        }
        if (defaultSize < 1) {
            throw new IllegalConfigException("Invalid config: [bean-searcher.params.pagination.default-size: " + defaultSize + "] must greater equal 1");
        }
        if (maxAllowedOffset < 1) {
            throw new IllegalConfigException("Invalid config: [bean-searcher.params.pagination.max-allowed-offset: " + maxAllowedOffset + "] must greater equal 1");
        }
        extractor.setMaxAllowedSize(maxAllowedSize);
        extractor.setMaxAllowedOffset(maxAllowedOffset);
        extractor.setDefaultSize(defaultSize);
        extractor.setSizeName(conf.getSize());
        extractor.setStart(conf.getStart());
        return extractor;
    }

    @Bean
    @ConditionalOnMissingBean(Dialect.class)
    public Dialect dialect(BeanSearcherSql config, ObjectProvider<List<DataSourceDialect>> dialects) {
        BeanSearcherSql.Dialect defaultType = config.getDialect();
        if (defaultType == null) {
            throw new IllegalConfigException("Invalid config: [bean-searcher.sql.dialect] can not be null.");
        }
        Dialect defaultDialect = createDialect(defaultType, "dialect");
        if (config.isDialectDynamic()) {
            DynamicDialect dynamicDialect = new DynamicDialect();
            dynamicDialect.setDefaultDialect(defaultDialect);
            ifAvailable(dialects, list -> list.forEach(item -> dynamicDialect.put(item.getDataSource(), item.getDialect())));
            config.getDialects().forEach((ds, dType) ->
                    dynamicDialect.put(ds, createDialect(dType, "dialects." + ds)));
            return dynamicDialect;
        }
        return defaultDialect;
    }

    private Dialect createDialect(BeanSearcherSql.Dialect dialectType, String propKey) {
        switch (dialectType) {
            case MySQL:
                return new MySqlDialect();
            case Oracle:
                return new OracleDialect();
            case PostgreSQL:
            case PgSQL:
                return new PostgreSqlDialect();
            case SqlServer:
                return new SqlServerDialect();
        }
        throw new IllegalConfigException("Invalid config: [bean-searcher.sql." + propKey + ": " + dialectType + "]. " +
                "Please see https://bs.zhxu.cn/guide/latest/advance.html#sql-%E6%96%B9%E8%A8%80%EF%BC%88dialect%EF%BC%89 for help.");
    }

    @Bean
    @ConditionalOnProperty(name = "bean-searcher.sql.dialect-dynamic", havingValue = "true")
    @ConditionalOnMissingBean(DynamicDialectSupport.class)
    public DynamicDialectSupport dynamicDialectSupport() {
        return new DynamicDialectSupport();
    }

    @Bean
    @ConditionalOnMissingBean(FieldOpPool.class)
    public FieldOpPool fieldOpPool(Dialect dialect, ObjectProvider<List<FieldOp>> fieldOps) {
        FieldOpPool pool = FieldOpPool.DEFAULT;
        ifAvailable(fieldOps, ops -> ops.forEach(pool::addFieldOp));
        pool.setDialect(dialect);
        return pool;
    }

    @Bean
    @ConditionalOnMissingBean(GroupResolver.class)
    public GroupResolver groupResolver(BeanSearcherParams config, ObjectProvider<ExprParser.Factory> parserFactory) {
        DefaultGroupResolver groupResolver = new DefaultGroupResolver();
        BeanSearcherParams.Group conf = config.getGroup();
        groupResolver.setEnabled(conf.isEnable());
        groupResolver.setCache(new LRUCache<>(conf.getCacheSize()));
        groupResolver.setMaxExprLength(conf.getMaxExprLength());
        ifAvailable(parserFactory, groupResolver::setParserFactory);
        return groupResolver;
    }

    @Bean
    @ConditionalOnMissingBean(BoolParamConvertor.class)
    public BoolParamConvertor boolParamConvertor() {
        return new BoolParamConvertor();
    }

    @Bean
    @ConditionalOnMissingBean(NumberParamConvertor.class)
    public NumberParamConvertor numberParamConvertor() {
        return new NumberParamConvertor();
    }

    @Bean
    @ConditionalOnMissingBean(DateParamConvertor.class)
    public DateParamConvertor dateParamConvertor(BeanSearcherParams config) {
        return new DateParamConvertor(config.getConvertor().getDateTarget());
    }

    @Bean
    @ConditionalOnMissingBean(TimeParamConvertor.class)
    public TimeParamConvertor timeParamConvertor(BeanSearcherParams config) {
        return new TimeParamConvertor(config.getConvertor().getTimeTarget());
    }

    @Bean
    @ConditionalOnMissingBean(DateTimeParamConvertor.class)
    public DateTimeParamConvertor dateTimeParamConvertor(BeanSearcherParams config) {
        BeanSearcherParams.Convertor conf = config.getConvertor();
        DateTimeParamConvertor convertor = new DateTimeParamConvertor(conf.getDateTimeTarget());
        convertor.setZoneId(conf.getZoneId());
        return convertor;
    }

    @Bean
    @ConditionalOnMissingBean(EnumParamConvertor.class)
    public EnumParamConvertor enumParamConvertor() {
        return new EnumParamConvertor();
    }

    @Bean
    @Order(-100)
    @ConditionalOnMissingBean(SizeLimitParamFilter.class)
    @ConditionalOnProperty(name = "bean-searcher.params.filter.use-size-limit", havingValue = "true", matchIfMissing = true)
    public SizeLimitParamFilter sizeLimitParamFilter(BeanSearcherParams config) {
        return new SizeLimitParamFilter(config.getFilter().getMaxParaMapSize());
    }

    @Bean
    @Order(100)
    @ConditionalOnMissingBean(ArrayValueParamFilter.class)
    @ConditionalOnProperty(name = "bean-searcher.params.filter.use-array-value", havingValue = "true", matchIfMissing = true)
    public ArrayValueParamFilter arrayValueParamFilter(BeanSearcherParams config) {
        return new ArrayValueParamFilter(config.getSeparator());
    }

    @Bean
    @Order(200)
    @ConditionalOnMissingBean(SuffixOpParamFilter.class)
    @ConditionalOnProperty(name = "bean-searcher.params.filter.use-suffix-op", havingValue = "true")
    public SuffixOpParamFilter suffixOpParamFilter(FieldOpPool fieldOpPool, BeanSearcherParams config) {
        return new SuffixOpParamFilter(fieldOpPool, config.getSeparator(), config.getOperatorKey());
    }

    @Bean
    @ConditionalOnMissingBean(ParamResolver.class)
    public ParamResolver paramResolver(PageExtractor pageExtractor,
                                       FieldOpPool fieldOpPool,
                                       List<ParamFilter> paramFilters,
                                       List<FieldConvertor.ParamConvertor> convertors,
                                       GroupResolver groupResolver,
                                       BeanSearcherParams config) {
        DefaultParamResolver paramResolver = new DefaultParamResolver(convertors, paramFilters);
        paramResolver.setPageExtractor(pageExtractor);
        paramResolver.setFieldOpPool(fieldOpPool);
        paramResolver.setGroupResolver(groupResolver);
        BeanSearcherParams.Group group = config.getGroup();
        paramResolver.getConfiguration()
                .gexprMerge(group.isMergeable())
                .groupSeparator(group.getSeparator())
                .gexpr(group.getExprName())
                .selectExclude(config.getSelectExclude())
                .onlySelect(config.getOnlySelect())
                .separator(config.getSeparator())
                .op(config.getOperatorKey())
                .ic(config.getIgnoreCaseKey())
                .orderBy(config.getOrderBy())
                .order(config.getOrder())
                .sort(config.getSort());
        return paramResolver;
    }

    @Bean
    @ConditionalOnMissingBean(SqlResolver.class)
    public SqlResolver sqlResolver(Dialect dialect, ObjectProvider<GroupPair.Resolver> groupPairResolver,
                                   ObjectProvider<JoinParaSerializer> joinParaSerializer) {
        DefaultSqlResolver resolver = new DefaultSqlResolver(dialect);
        ifAvailable(groupPairResolver, resolver::setGroupPairResolver);
        ifAvailable(joinParaSerializer, resolver::setJoinParaSerializer);
        return resolver;
    }

    @Bean
    @ConditionalOnMissingBean(SqlExecutor.class)
    public SqlExecutor sqlExecutor(ObjectProvider<DataSource> dataSource,
                                   ObjectProvider<List<NamedDataSource>> namedDataSources,
                                   ObjectProvider<SqlExecutor.SlowListener> slowListener,
                                   BeanSearcherSql config) {
        SpringSqlExecutor executor = new SpringSqlExecutor(dataSource.getIfAvailable());
        ifAvailable(namedDataSources, ndsList -> {
            for (NamedDataSource nds: ndsList) {
                executor.setDataSource(nds.getName(), nds.getDataSource());
            }
        });
        ifAvailable(slowListener, executor::setSlowListener);
        executor.setSlowSqlThreshold(config.getSlowSqlThreshold());
        return executor;
    }

    @Bean
    @ConditionalOnProperty(name = "bean-searcher.field-convertor.use-number", havingValue = "true", matchIfMissing = true)
    @ConditionalOnMissingBean(NumberFieldConvertor.class)
    public NumberFieldConvertor numberFieldConvertor() {
        return new NumberFieldConvertor();
    }

    @Bean
    @ConditionalOnProperty(name = "bean-searcher.field-convertor.use-str-num", havingValue = "true", matchIfMissing = true)
    @ConditionalOnMissingBean(StrNumFieldConvertor.class)
    public StrNumFieldConvertor strNumFieldConvertor() {
        return new StrNumFieldConvertor();
    }

    @Bean
    @ConditionalOnProperty(name = "bean-searcher.field-convertor.use-bool-num", havingValue = "true", matchIfMissing = true)
    @ConditionalOnMissingBean(BoolNumFieldConvertor.class)
    public BoolNumFieldConvertor boolNumFieldConvertor() {
        return new BoolNumFieldConvertor();
    }

    @Bean
    @ConditionalOnProperty(name = "bean-searcher.field-convertor.use-bool", havingValue = "true", matchIfMissing = true)
    @ConditionalOnMissingBean(BoolFieldConvertor.class)
    public BoolFieldConvertor boolFieldConvertor(BeanSearcherFieldConvertor config) {
        String[] falseValues = config.getBoolFalseValues();
        BoolFieldConvertor convertor = new BoolFieldConvertor();
        if (falseValues != null) {
            convertor.addFalseValues(falseValues);
        }
        return convertor;
    }

    @Bean
    @ConditionalOnProperty(name = "bean-searcher.field-convertor.use-date", havingValue = "true", matchIfMissing = true)
    @ConditionalOnMissingBean(DateFieldConvertor.class)
    public DateFieldConvertor dateFieldConvertor(BeanSearcherFieldConvertor config) {
        DateFieldConvertor convertor = new DateFieldConvertor();
        ZoneId zoneId = config.getZoneId();
        if (zoneId != null) {
            convertor.setZoneId(zoneId);
        }
        return convertor;
    }

    @Bean
    @ConditionalOnProperty(name = "bean-searcher.field-convertor.use-time", havingValue = "true", matchIfMissing = true)
    @ConditionalOnMissingBean(TimeFieldConvertor.class)
    public TimeFieldConvertor timeFieldConvertor() {
        return new TimeFieldConvertor();
    }

    @Bean
    @ConditionalOnProperty(name = "bean-searcher.field-convertor.use-enum", havingValue = "true", matchIfMissing = true)
    @ConditionalOnMissingBean(EnumFieldConvertor.class)
    public EnumFieldConvertor enumFieldConvertor(BeanSearcherFieldConvertor config) {
        EnumFieldConvertor convertor = new EnumFieldConvertor();
        convertor.setFailOnError(config.isEnumFailOnError());
        convertor.setIgnoreCase(config.isEnumIgnoreCase());
        return convertor;
    }

    /**
     * 注解 @ConditionalOnClass 不能与 @Bean 放在一起，否则当没有条件中的 Class 时，会出现错误：
     * java.lang.ArrayStoreException: sun.reflect.annotation.TypeNotPresentExceptionProxy
     */
    @Configuration
    @ConditionalOnClass(cn.zhxu.xjson.JsonKit.class)
    public static class BeanSearcherConfigOnJsonKit {

        @Bean
        @ConditionalOnProperty(name = "bean-searcher.field-convertor.use-json", havingValue = "true", matchIfMissing = true)
        @ConditionalOnMissingBean(JsonFieldConvertor.class)
        public JsonFieldConvertor jsonFieldConvertor(BeanSearcherFieldConvertor config) {
            return new JsonFieldConvertor(config.isJsonFailOnError());
        }

        @Bean
        @Order(300)
        @ConditionalOnMissingBean(JsonArrayParamFilter.class)
        @ConditionalOnProperty(name = "bean-searcher.params.filter.use-json-array", havingValue = "true")
        public JsonArrayParamFilter jsonArrayParamFilter(BeanSearcherParams config) {
            return new JsonArrayParamFilter(config.getSeparator());
        }

    }

    @Configuration
    @ConditionalOnClass(oracle.sql.TIMESTAMP.class)
    public static class BeanSearcherConfigOnOracle {

        @Bean
        @ConditionalOnProperty(name = "bean-searcher.field-convertor.use-oracle-timestamp", havingValue = "true", matchIfMissing = true)
        @ConditionalOnMissingBean(OracleTimestampFieldConvertor.class)
        public OracleTimestampFieldConvertor oracleTimestampFieldConvertor() {
            return new OracleTimestampFieldConvertor();
        }

    }

    @Bean
    @ConditionalOnProperty(name = "bean-searcher.field-convertor.use-list", havingValue = "true", matchIfMissing = true)
    @ConditionalOnMissingBean(ListFieldConvertor.class)
    public ListFieldConvertor listFieldConvertor(BeanSearcherFieldConvertor config,
                ObjectProvider<List<ListFieldConvertor.Convertor<?>>> convertorsProvider) {
        ListFieldConvertor convertor = new ListFieldConvertor(config.getListItemSeparator());
        ifAvailable(convertorsProvider, convertor::setConvertors);
        return convertor;
    }

    @Bean
    @ConditionalOnMissingBean(BeanReflector.class)
    public BeanReflector beanReflector(ObjectProvider<List<BFieldConvertor>> convertorsProvider) {
        List<BFieldConvertor> convertors = convertorsProvider.getIfAvailable();
        if (convertors != null) {
            return new DefaultBeanReflector(convertors);
        }
        return new DefaultBeanReflector();
    }

    @Bean
    @ConditionalOnMissingBean(DbMapping.class)
    public DbMapping dbMapping(BeanSearcherSql config) {
        DefaultDbMapping mapping = new DefaultDbMapping();
        BeanSearcherSql.DefaultMapping conf = config.getDefaultMapping();
        mapping.setTablePrefix(conf.getTablePrefix());
        mapping.setUpperCase(conf.isUpperCase());
        mapping.setUnderlineCase(conf.isUnderlineCase());
        mapping.setRedundantSuffixes(conf.getRedundantSuffixes());
        mapping.setIgnoreFields(conf.getIgnoreFields());
        mapping.setDefaultInheritType(conf.getInheritType());
        mapping.setDefaultSortType(conf.getSortType());
        mapping.setAroundChar(conf.getAroundChar());
        return mapping;
    }

    @Bean
    @ConditionalOnMissingBean(MetaResolver.class)
    public MetaResolver metaResolver(DbMapping dbMapping, ObjectProvider<SnippetResolver> snippetResolver) {
        DefaultMetaResolver metaResolver = new DefaultMetaResolver(dbMapping);
        ifAvailable(snippetResolver, metaResolver::setSnippetResolver);
        return metaResolver;
    }

    @Bean
    @ConditionalOnMissingBean(BeanSearcher.class)
    @ConditionalOnProperty(name = "bean-searcher.use-bean-searcher", havingValue = "true", matchIfMissing = true)
    public BeanSearcher beanSearcher(MetaResolver metaResolver,
                                     ParamResolver paramResolver,
                                     SqlResolver sqlResolver,
                                     SqlExecutor sqlExecutor,
                                     BeanReflector beanReflector,
                                     ObjectProvider<List<SqlInterceptor>> interceptors,
                                     ObjectProvider<List<ResultFilter>> processors,
                                     BeanSearcherParams config) {
        DefaultBeanSearcher searcher = new DefaultBeanSearcher();
        searcher.setMetaResolver(metaResolver);
        searcher.setParamResolver(paramResolver);
        searcher.setSqlResolver(sqlResolver);
        searcher.setSqlExecutor(sqlExecutor);
        searcher.setBeanReflector(beanReflector);
        searcher.setFailOnParamError(config.isFailOnError());
        ifAvailable(interceptors, searcher::setInterceptors);
        ifAvailable(processors, searcher::setResultFilters);
        return searcher;
    }

    @Bean
    @ConditionalOnProperty(name = "bean-searcher.field-convertor.use-date-format", havingValue = "true", matchIfMissing = true)
    @ConditionalOnMissingBean(DateFormatFieldConvertor.class)
    public DateFormatFieldConvertor dateFormatFieldConvertor(BeanSearcherFieldConvertor config) {
        DateFormatFieldConvertor convertor = new DateFormatFieldConvertor();
        Map<String, String> dateFormats = config.getDateFormats();
        if (dateFormats != null) {
            dateFormats.forEach((key, value) -> {
                // 由于在 yml 的 key 中的 `:` 会被自动过滤，所以这里做下特殊处理，在 yml 中可以用 `-` 替代
                String scope = key.replace('-', ':');
                convertor.setFormat(scope, value);
            });
        }
        ZoneId zoneId = config.getZoneId();
        if (zoneId != null) {
            convertor.setZoneId(zoneId);
        }
        return convertor;
    }

    @Bean
    @ConditionalOnProperty(name = "bean-searcher.field-convertor.use-b2-m", havingValue = "true")
    @ConditionalOnMissingBean(B2MFieldConvertor.class)
    public B2MFieldConvertor b2mFieldConvertor(ObjectProvider<List<BFieldConvertor>> convertors) {
        List<BFieldConvertor> list = convertors.getIfAvailable();
        if (list != null) {
            return new B2MFieldConvertor(list);
        }
        return new B2MFieldConvertor(Collections.emptyList());
    }

    @Bean @Primary
    @ConditionalOnMissingBean(MapSearcher.class)
    @ConditionalOnProperty(name = "bean-searcher.use-map-searcher", havingValue = "true", matchIfMissing = true)
    public MapSearcher mapSearcher(MetaResolver metaResolver,
                                   ParamResolver paramResolver,
                                   SqlResolver sqlResolver,
                                   SqlExecutor sqlExecutor,
                                   ObjectProvider<List<MFieldConvertor>> convertors,
                                   ObjectProvider<List<SqlInterceptor>> interceptors,
                                   ObjectProvider<List<ResultFilter>> resultFilters,
                                   BeanSearcherParams config) {
        DefaultMapSearcher searcher = new DefaultMapSearcher();
        searcher.setMetaResolver(metaResolver);
        searcher.setParamResolver(paramResolver);
        searcher.setSqlResolver(sqlResolver);
        searcher.setSqlExecutor(sqlExecutor);
        searcher.setFailOnParamError(config.isFailOnError());
        List<MFieldConvertor> list = convertors.getIfAvailable();
        if (list != null) {
            List<MFieldConvertor> newList = new ArrayList<>(list);
            // 让 DateFormatFieldConvertor 排在前面
            newList.sort((o1, o2) -> {
                if (o1 instanceof DateFormatFieldConvertor) {
                    return -1;
                }
                if (o2 instanceof DateFormatFieldConvertor) {
                    return 1;
                }
                return 0;
            });
            searcher.setConvertors(newList);
        }
        ifAvailable(interceptors, searcher::setInterceptors);
        ifAvailable(resultFilters, searcher::setResultFilters);
        return searcher;
    }

    private <T> void ifAvailable(ObjectProvider<T> provider, Consumer<T> consumer) {
        // 为了兼容 1.x 的 SpringBoot，最低兼容到 v1.4
        // 不直接使用 ObjectProvider.ifAvailable 方法
        T dependency = provider.getIfAvailable();
        if (dependency != null) {
            consumer.accept(dependency);
        }
    }

}
