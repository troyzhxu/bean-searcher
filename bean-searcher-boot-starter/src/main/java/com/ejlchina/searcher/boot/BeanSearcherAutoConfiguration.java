package com.ejlchina.searcher.boot;

import com.ejlchina.searcher.*;
import com.ejlchina.searcher.FieldConvertor.BFieldConvertor;
import com.ejlchina.searcher.FieldConvertor.MFieldConvertor;
import com.ejlchina.searcher.boot.BeanSearcherProperties.Params;
import com.ejlchina.searcher.boot.BeanSearcherProperties.Sql;
import com.ejlchina.searcher.dialect.Dialect;
import com.ejlchina.searcher.dialect.MySqlDialect;
import com.ejlchina.searcher.dialect.OracleDialect;
import com.ejlchina.searcher.implement.*;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;


@Configuration
@ConditionalOnBean(DataSource.class)
@AutoConfigureAfter({ DataSourceAutoConfiguration.class })
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
	@ConditionalOnMissingBean(ParamResolver.class)
	public ParamResolver paramResolver(PageExtractor pageExtractor,
									   ObjectProvider<ParamFilter[]> paramFilters,
									   BeanSearcherProperties config) {
		DefaultParamResolver paramResolver = new DefaultParamResolver();
		paramResolver.setPageExtractor(pageExtractor);
		ifAvailable(paramFilters, paramResolver::setParamFilters);
		Params conf = config.getParams();
		paramResolver.setOperatorSuffix(conf.getOperatorKey());
		paramResolver.setIgnoreCaseSuffix(conf.getIgnoreCaseKey());
		paramResolver.setOrderName(conf.getOrder());
		paramResolver.setSortName(conf.getSort());
		paramResolver.setSeparator(conf.getSeparator());
		paramResolver.setOnlySelectName(conf.getOnlySelect());
		paramResolver.setSelectExcludeName(conf.getSelectExclude());
		return paramResolver;
	}

	@Bean
	@ConditionalOnMissingBean(Dialect.class)
	public Dialect dialect(BeanSearcherProperties config) {
		String dialect = config.getSql().getDialect();
		if (dialect == null) {
			throw new SearchException("配置项【bean-searcher.sql.dialect】不能为空");
		}
		switch (dialect.toLowerCase()) {
		case Sql.DIALECT_MYSQL:
			return new MySqlDialect();
		case Sql.DIALECT_ORACLE:
			return new OracleDialect();
		}
		throw new SearchException("配置项【bean-searcher.sql.dialect】只能为  MySql | Oracle 中的一个，若需支持其它方言，可自己注入一个 com.ejlchina.searcher.dialect.Dialect 类型的 Bean！");
	}
	
	@Bean
	@ConditionalOnMissingBean(DateValueCorrector.class)
	public DateValueCorrector dateValueCorrector() {
		return new DateValueCorrector();
	}

	@Bean
	@ConditionalOnMissingBean(SqlResolver.class)
	public SqlResolver sqlResolver(Dialect dialect, DateValueCorrector dateValueCorrector) {
		return new DefaultSqlResolver(dialect, dateValueCorrector);
	}

	@Bean
	@ConditionalOnMissingBean(SqlExecutor.class)
	public SqlExecutor sqlExecutor(DataSource dataSource, ObjectProvider<List<NamedDataSource>> namedDataSources) {
		DefaultSqlExecutor executor = new DefaultSqlExecutor(dataSource);
		ifAvailable(namedDataSources, ndsList -> {
			for (NamedDataSource nds: ndsList) {
				executor.setDataSource(nds.getName(), nds.getDataSource());
			}
		});
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
	@ConditionalOnProperty(name = "bean-searcher.field-convertor.use-enum", havingValue = "true", matchIfMissing = true)
	@ConditionalOnMissingBean(EnumFieldConvertor.class)
	public EnumFieldConvertor enumFieldConvertor() {
		return new EnumFieldConvertor();
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
									 ObjectProvider<List<SqlInterceptor>> interceptors) {
		DefaultBeanSearcher searcher = new DefaultBeanSearcher();
		searcher.setMetaResolver(metaResolver);
		searcher.setParamResolver(paramResolver);
		searcher.setSqlResolver(sqlResolver);
		searcher.setSqlExecutor(sqlExecutor);
		searcher.setBeanReflector(beanReflector);
		ifAvailable(interceptors, searcher::setInterceptors);
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

	@Bean @Primary
	@ConditionalOnMissingBean(MapSearcher.class)
	@ConditionalOnProperty(name = "bean-searcher.use-map-searcher", havingValue = "true", matchIfMissing = true)
	public MapSearcher mapSearcher(MetaResolver metaResolver,
								   ParamResolver paramResolver,
								   SqlResolver sqlResolver,
								   SqlExecutor sqlExecutor,
								   ObjectProvider<List<SqlInterceptor>> interceptors,
								   ObjectProvider<List<MFieldConvertor>> convertors) {
		DefaultMapSearcher searcher = new DefaultMapSearcher();
		searcher.setMetaResolver(metaResolver);
		searcher.setParamResolver(paramResolver);
		searcher.setSqlResolver(sqlResolver);
		searcher.setSqlExecutor(sqlExecutor);
		ifAvailable(interceptors, searcher::setInterceptors);
		ifAvailable(convertors, searcher::setConvertors);
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
