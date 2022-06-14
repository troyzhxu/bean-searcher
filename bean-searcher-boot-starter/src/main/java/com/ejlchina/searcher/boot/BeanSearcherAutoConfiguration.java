package com.ejlchina.searcher.boot;

import com.ejlchina.searcher.*;
import com.ejlchina.searcher.FieldConvertor.BFieldConvertor;
import com.ejlchina.searcher.FieldConvertor.MFieldConvertor;
import com.ejlchina.searcher.boot.BeanSearcherProperties.Params;
import com.ejlchina.searcher.boot.BeanSearcherProperties.Sql;
import com.ejlchina.searcher.convertor.B2MFieldConvertor;
import com.ejlchina.searcher.convertor.BoolFieldConvertor;
import com.ejlchina.searcher.convertor.BoolNumFieldConvertor;
import com.ejlchina.searcher.convertor.DateFieldConvertor;
import com.ejlchina.searcher.convertor.DateFormatFieldConvertor;
import com.ejlchina.searcher.convertor.EnumFieldConvertor;
import com.ejlchina.searcher.convertor.NumberFieldConvertor;
import com.ejlchina.searcher.convertor.StrNumFieldConvertor;
import com.ejlchina.searcher.convertor.TimeFieldConvertor;
import com.ejlchina.searcher.dialect.*;
import com.ejlchina.searcher.group.DefaultGroupResolver;
import com.ejlchina.searcher.group.DefaultParserFactory;
import com.ejlchina.searcher.group.ExprParser;
import com.ejlchina.searcher.group.GroupResolver;
import com.ejlchina.searcher.implement.*;
import com.ejlchina.searcher.util.LRUCache;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;


@Configuration
@EnableConfigurationProperties(BeanSearcherProperties.class)
public class BeanSearcherAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean(PageExtractor.class)
	public PageExtractor pageExtractor(BeanSearcherProperties config) {
		Params.PaginationProps conf = config.getParams().getPagination();
		String type = conf.getType();
		BasePageExtractor extractor;
		if (Params.PaginationProps.TYPE_PAGE.equals(type)) {
			PageSizeExtractor p = new PageSizeExtractor();
			p.setPageName(conf.getPage());
			extractor = p;
		}  else
		if (Params.PaginationProps.TYPE_OFFSET.equals(type)) {
			PageOffsetExtractor p = new PageOffsetExtractor();
			p.setOffsetName(conf.getOffset());
			extractor = p;
		} else {
			throw new SearchException("配置项 [bean-searcher.params.pagination.type] 只能为 page 或 offset！");
		}
		extractor.setMaxAllowedSize(conf.getMaxAllowedSize());
		extractor.setSizeName(conf.getSize());
		extractor.setStart(conf.getStart());
		extractor.setDefaultSize(conf.getDefaultSize());
		return extractor;
	}

	@Bean
	@ConditionalOnMissingBean(Dialect.class)
	public Dialect dialect(BeanSearcherProperties config) {
		Sql.Dialect dialect = config.getSql().getDialect();
		if (dialect == null) {
			throw new SearchException("配置项【bean-searcher.sql.dialect】不能为空");
		}
		switch (dialect) {
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
		throw new SearchException("配置项【bean-searcher.sql.dialect】只能为  MySql | Oracle 中的一个，若需支持其它方言，可自己注入一个 com.ejlchina.searcher.dialect.Dialect 类型的 Bean！");
	}

	@Bean
	@ConditionalOnMissingBean(FieldOpPool.class)
	public FieldOpPool fieldOpPool(Dialect dialect, ObjectProvider<List<FieldOp>> fieldOps) {
		FieldOpPool pool = new FieldOpPool();
		ifAvailable(fieldOps, ops -> ops.forEach(pool::addFieldOp));
		pool.setDialect(dialect);
		return pool;
	}

	@Bean
	@ConditionalOnMissingBean(ExprParser.Factory.class)
	public ExprParser.Factory parserFactory() {
		return new DefaultParserFactory();
	}

	@Bean
	@ConditionalOnMissingBean(GroupResolver.class)
	public GroupResolver groupResolver(BeanSearcherProperties config, ExprParser.Factory parserFactory) {
		DefaultGroupResolver groupResolver = new DefaultGroupResolver();
		Params.Group conf = config.getParams().getGroup();
		groupResolver.setEnabled(conf.isEnable());
		groupResolver.setCache(new LRUCache<>(conf.getCacheSize()));
		groupResolver.setParserFactory(parserFactory);
		return groupResolver;
	}

	@Bean
	@ConditionalOnMissingBean(ParamResolver.class)
	public ParamResolver paramResolver(PageExtractor pageExtractor,
									   FieldOpPool fieldOpPool,
									   ObjectProvider<ParamFilter[]> paramFilters,
									   ObjectProvider<List<ParamResolver.Convertor>> convertors,
									   GroupResolver groupResolver,
									   BeanSearcherProperties config) {
		DefaultParamResolver paramResolver = new DefaultParamResolver();
		paramResolver.setPageExtractor(pageExtractor);
		paramResolver.setFieldOpPool(fieldOpPool);
		ifAvailable(paramFilters, paramResolver::setParamFilters);
		ifAvailable(convertors, l -> l.forEach(paramResolver::addConvertor));
		Params conf = config.getParams();
		paramResolver.setOperatorSuffix(conf.getOperatorKey());
		paramResolver.setIgnoreCaseSuffix(conf.getIgnoreCaseKey());
		paramResolver.setOrderName(conf.getOrder());
		paramResolver.setSortName(conf.getSort());
		paramResolver.setOrderByName(conf.getOrderBy());
		paramResolver.setSeparator(conf.getSeparator());
		paramResolver.setOnlySelectName(conf.getOnlySelect());
		paramResolver.setSelectExcludeName(conf.getSelectExclude());
		Params.Group group = conf.getGroup();
		paramResolver.setGexprName(group.getExprName());
		paramResolver.setGroupSeparator(group.getSeparator());
		paramResolver.setGroupResolver(groupResolver);
		return paramResolver;
	}

	@Bean
	@ConditionalOnMissingBean(DateValueCorrector.class)
	@ConditionalOnProperty(name = "bean-searcher.sql.use-date-value-corrector", havingValue = "true", matchIfMissing = true)
	public DateValueCorrector dateValueCorrector() {
		return new DateValueCorrector();
	}

	@Bean
	@ConditionalOnMissingBean(SqlResolver.class)
	public SqlResolver sqlResolver(Dialect dialect, ObjectProvider<DateValueCorrector> dateValueCorrector) {
		return new DefaultSqlResolver(dialect, dateValueCorrector.getIfAvailable());
	}

	@Bean
	@ConditionalOnMissingBean(SqlExecutor.class)
	public SqlExecutor sqlExecutor(ObjectProvider<DataSource> dataSource,
								   ObjectProvider<List<NamedDataSource>> namedDataSources,
								   ObjectProvider<SqlExecutor.SlowListener> slowListener,
								   BeanSearcherProperties config) {
		DefaultSqlExecutor executor = new DefaultSqlExecutor(dataSource.getIfAvailable());
		ifAvailable(namedDataSources, ndsList -> {
			for (NamedDataSource nds: ndsList) {
				executor.setDataSource(nds.getName(), nds.getDataSource());
			}
		});
		ifAvailable(slowListener, executor::setSlowListener);
		executor.setSlowSqlThreshold(config.getSql().getSlowSqlThreshold());
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
	public BoolFieldConvertor boolFieldConvertor(BeanSearcherProperties config) {
		String[] falseValues = config.getFieldConvertor().getBoolFalseValues();
		BoolFieldConvertor convertor = new BoolFieldConvertor();
		if (falseValues != null) {
			convertor.addFalseValues(falseValues);
		}
		return convertor;
	}

	@Bean
	@ConditionalOnProperty(name = "bean-searcher.field-convertor.use-date", havingValue = "true", matchIfMissing = true)
	@ConditionalOnMissingBean(DateFieldConvertor.class)
	public DateFieldConvertor dateFieldConvertor(BeanSearcherProperties config) {
		DateFieldConvertor convertor = new DateFieldConvertor();
		ZoneId zoneId = config.getFieldConvertor().getZoneId();
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
	public EnumFieldConvertor enumFieldConvertor(BeanSearcherProperties config) {
		BeanSearcherProperties.FieldConvertor conf = config.getFieldConvertor();
		EnumFieldConvertor convertor = new EnumFieldConvertor();
		convertor.setFailOnError(conf.isEnumFailOnError());
		convertor.setIgnoreCase(conf.isEnumIgnoreCase());
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
	public DbMapping dbMapping(BeanSearcherProperties config) {
		DefaultDbMapping mapping = new DefaultDbMapping();
		Sql.DefaultMapping conf = config.getSql().getDefaultMapping();
		mapping.setTablePrefix(conf.getTablePrefix());
		mapping.setUpperCase(conf.isUpperCase());
		mapping.setUnderlineCase(conf.isUnderlineCase());
		mapping.setRedundantSuffixes(conf.getRedundantSuffixes());
		mapping.setIgnoreFields(conf.getIgnoreFields());
		mapping.setDefaultInheritType(conf.getInheritType());
		mapping.setDefaultSortType(conf.getSortType());
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
									 ObjectProvider<List<ResultFilter>> processors) {
		DefaultBeanSearcher searcher = new DefaultBeanSearcher();
		searcher.setMetaResolver(metaResolver);
		searcher.setParamResolver(paramResolver);
		searcher.setSqlResolver(sqlResolver);
		searcher.setSqlExecutor(sqlExecutor);
		searcher.setBeanReflector(beanReflector);
		ifAvailable(interceptors, searcher::setInterceptors);
		ifAvailable(processors, searcher::setResultFilters);
		return searcher;
	}

	@Bean
	@ConditionalOnProperty(name = "bean-searcher.field-convertor.use-date-format", havingValue = "true", matchIfMissing = true)
	@ConditionalOnMissingBean(DateFormatFieldConvertor.class)
	public DateFormatFieldConvertor dateFormatFieldConvertor(BeanSearcherProperties config) {
		BeanSearcherProperties.FieldConvertor conf = config.getFieldConvertor();
		Map<String, String> dateFormats = conf.getDateFormats();
		ZoneId zoneId = conf.getZoneId();
		DateFormatFieldConvertor convertor = new DateFormatFieldConvertor();
		if (dateFormats != null) {
			dateFormats.forEach(convertor::setFormat);
		}
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
								   ObjectProvider<List<ResultFilter>> resultFilters) {
		DefaultMapSearcher searcher = new DefaultMapSearcher();
		searcher.setMetaResolver(metaResolver);
		searcher.setParamResolver(paramResolver);
		searcher.setSqlResolver(sqlResolver);
		searcher.setSqlExecutor(sqlExecutor);
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
