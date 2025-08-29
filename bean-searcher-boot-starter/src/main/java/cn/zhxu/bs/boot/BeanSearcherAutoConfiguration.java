package cn.zhxu.bs.boot;

import cn.zhxu.bs.*;
import cn.zhxu.bs.FieldConvertor.BFieldConvertor;
import cn.zhxu.bs.FieldConvertor.MFieldConvertor;
import cn.zhxu.bs.boot.prop.BeanSearcherFieldConvertor;
import cn.zhxu.bs.boot.prop.BeanSearcherParams;
import cn.zhxu.bs.boot.prop.BeanSearcherProperties;
import cn.zhxu.bs.boot.prop.BeanSearcherSql;
import cn.zhxu.bs.convertor.DateFormatFieldConvertor;
import cn.zhxu.bs.dialect.*;
import cn.zhxu.bs.group.DefaultGroupResolver;
import cn.zhxu.bs.group.ExprParser;
import cn.zhxu.bs.group.GroupPair;
import cn.zhxu.bs.group.GroupResolver;
import cn.zhxu.bs.implement.*;
import cn.zhxu.bs.util.LRUCache;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Configuration
@EnableConfigurationProperties({
        BeanSearcherProperties.class,
        BeanSearcherFieldConvertor.class,
        BeanSearcherParams.class,
        BeanSearcherSql.class
})
@Import({
        BeanSearcherConvertors.class,
        BeanSearcherParamFilters.class,
        BeanSearcherConfigOnJsonKit.class,
        BeanSearcherConfigOnOracle.class,
        BeanSearcherConfigOnLabel.class,
        BeanSearcherConfigOnExporter.class
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

    static <T> void ifAvailable(ObjectProvider<T> provider, Consumer<T> consumer) {
        // 为了兼容 1.x 的 SpringBoot，最低兼容到 v1.4
        // 不直接使用 ObjectProvider.ifAvailable 方法
        T dependency = provider.getIfAvailable();
        if (dependency != null) {
            consumer.accept(dependency);
        }
    }

}
