package com.ejlchina.searcher.boot;

import com.ejlchina.searcher.*;
import com.ejlchina.searcher.boot.BeanSearcherProperties.ParamsProps;
import com.ejlchina.searcher.boot.BeanSearcherProperties.SqlProps;
import com.ejlchina.searcher.dialect.*;
import com.ejlchina.searcher.implement.*;
import com.ejlchina.searcher.implement.processor.DefaultParamProcessor;
import com.ejlchina.searcher.implement.processor.ParamProcessor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;


@Configuration
@ConditionalOnBean(DataSource.class)
@AutoConfigureAfter({ DataSourceAutoConfiguration.class })
@EnableConfigurationProperties(BeanSearcherProperties.class)
public class BeanSearcherAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean(PageExtractor.class)
	public PageExtractor pagination(BeanSearcherProperties config) {
		ParamsProps.PaginationPorps conf = config.getParams().getPagination();
		String type = conf.getType();
		if (ParamsProps.PaginationPorps.TYPE_PAGE.equals(type)) {
			PageSizeExtractor p = new PageSizeExtractor();
			p.setMaxAllowedSize(conf.getMaxAllowedSize());
			p.setSizeName(conf.getSize());
			p.setPageName(conf.getPage());
			p.setStart(conf.getStart());
			p.setDefaultSize(conf.getDefaultSize());
			return p;
		} 
		if (ParamsProps.PaginationPorps.TYPE_OFFSET.equals(type)) {
			PageOffsetExtractor p = new PageOffsetExtractor();
			p.setMaxAllowedSize(conf.getMaxAllowedSize());
			p.setSizeName(conf.getMax());
			p.setOffsetName(conf.getOffset());
			p.setStart(conf.getStart());
			p.setDefaultSize(conf.getDefaultSize());
			return p;
		}
		throw new SearchException("配置项【bean-searcher.params.pagination.type】只能为 page 或  offset！");
	}

	@Bean
	@ConditionalOnMissingBean(ParamResolver.class)
	public ParamResolver searchParamResolver(PageExtractor pageExtractor, BeanSearcherProperties config,
											 ObjectProvider<ParamFilter[]> paramFilterProvider) {
		DefaultParamResolver searchParamResolver = new DefaultParamResolver();
		searchParamResolver.setPageExtractor(pageExtractor);
		ParamsProps conf = config.getParams();
		searchParamResolver.setOperatorSuffix(conf.getOperatorKey());
		searchParamResolver.setIgnoreCaseSuffix(conf.getIgnoreCaseKey());
		searchParamResolver.setOrderName(conf.getOrder());
		searchParamResolver.setSortName(conf.getSort());
		searchParamResolver.setSeparator(conf.getSeparator());
		ParamFilter[] paramFilters = paramFilterProvider.getIfAvailable();
		if (paramFilters != null) {
			searchParamResolver.setFilters(paramFilters);
		}
		return searchParamResolver;
	}

	@Bean
	@ConditionalOnMissingBean(Dialect.class)
	public Dialect dialect(BeanSearcherProperties config) {
		String dialect = config.getSql().getDialect();
		if (dialect == null) {
			throw new SearchException("配置项【bean-searcher.sql.dialect】不能为空");
		}
		switch (dialect.toLowerCase()) {
		case SqlProps.DIALECT_MYSQL:
			return new MySqlDialect();
		case SqlProps.DIALECT_ORACLE:
			return new OracleDialect();
		case SqlProps.DIALECT_POSTGRE_SQL:
			return new PostgreSqlDialect();
		case SqlProps.DIALECT_SQL_SERVER:
			return new SqlServerDialect();
		}
		throw new SearchException("配置项【bean-searcher.sql.dialect】只能为  MySql|Oracle|PostgreSql|SqlServer 中的一个 ！");
	}
	
	@Bean
	@ConditionalOnMissingBean(ParamProcessor.class)
	public ParamProcessor paramProcessor() {
		return new DefaultParamProcessor();
	}

	@Bean
	@ConditionalOnMissingBean(SqlResolver.class)
	public SqlResolver searchSqlResolver(Dialect dialect, ParamProcessor paramProcessor) {
		return new DefaultSqlResolver(dialect, paramProcessor);
	}

	@Bean
	@ConditionalOnMissingBean(SqlExecutor.class)
	public SqlExecutor searchSqlExecutor(DataSource dataSource) {
		return new DefaultSqlExecutor(dataSource);
	}

//	@Bean
//	@ConditionalOnMissingBean(FieldConvertor.class)
//	public FieldConvertor fieldConvertor(BeanSearcherProperties config) {
//		BoolFieldConvertor convertor = new BoolFieldConvertor();
//		FieldConvertorProps conf = config.getFieldConvertor();
//		convertor.setIgnoreCase(conf.isIgnoreCase());
//		if (conf.isIgnoreCase()) {
//			convertor.setTrues(StringUtils.toUpperCase(conf.getTrues()));
//			convertor.setFalses(StringUtils.toUpperCase(conf.getFalses()));
//		} else {
//			convertor.setTrues(conf.getTrues());
//			convertor.setFalses(conf.getFalses());
//		}
//		return convertor;
//	}
	
	@Bean
	@ConditionalOnMissingBean(BeanReflector.class)
	public BeanReflector searchResultResolver(FieldConvertor fieldConvertor) {
		List<FieldConvertor> fieldConvertors = new ArrayList<>();
		fieldConvertors.add(fieldConvertor);
		return new DefaultBeanReflector(fieldConvertors);
	}

	@Bean
	@ConditionalOnMissingBean(BeanSearcher.class)
	public BeanSearcher beanSearcher(ParamResolver paramResolver,
									 SqlResolver sqlResolver,
									 SqlExecutor sqlExecutor,
									 BeanReflector beanReflector) {
		DefaultBeanSearcher searcher = new DefaultBeanSearcher();
		searcher.setParamResolver(paramResolver);
		searcher.setSqlResolver(sqlResolver);
		searcher.setSqlExecutor(sqlExecutor);
		searcher.setBeanReflector(beanReflector);
		return searcher;
	}

	@Bean
	@ConditionalOnMissingBean(MapSearcher.class)
	public MapSearcher mapSearcher(ParamResolver paramResolver,
								   SqlResolver sqlResolver,
								   SqlExecutor sqlExecutor) {
		DefaultMapSearcher searcher = new DefaultMapSearcher();
		searcher.setParamResolver(paramResolver);
		searcher.setSqlResolver(sqlResolver);
		searcher.setSqlExecutor(sqlExecutor);
		return searcher;
	}

}
