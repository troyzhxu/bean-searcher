package com.ejlchina.searcher.boot;

import com.ejlchina.searcher.*;
import com.ejlchina.searcher.dialect.*;
import com.ejlchina.searcher.implement.*;
import com.ejlchina.searcher.implement.convertor.DefaultFieldConvertor;
import com.ejlchina.searcher.implement.convertor.FieldConvertor;
import com.ejlchina.searcher.implement.pagination.MaxOffsetPagination;
import com.ejlchina.searcher.implement.pagination.PageNumPagination;
import com.ejlchina.searcher.implement.pagination.Pagination;
import com.ejlchina.searcher.implement.parafilter.ParamFilter;
import com.ejlchina.searcher.implement.processor.DefaultParamProcessor;
import com.ejlchina.searcher.implement.processor.ParamProcessor;
import com.ejlchina.searcher.boot.BeanSearcherProperties.FieldConvertorProps;
import com.ejlchina.searcher.boot.BeanSearcherProperties.ParamsPorps;
import com.ejlchina.searcher.boot.BeanSearcherProperties.SqlProps;
import com.ejlchina.searcher.util.StringUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;


@Configuration
@ConditionalOnBean(DataSource.class)
@AutoConfigureAfter({ DataSourceAutoConfiguration.class })
@EnableConfigurationProperties(BeanSearcherProperties.class)
public class BeanSearcherAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean(Pagination.class)
	public Pagination pagination(BeanSearcherProperties config) {
		ParamsPorps.PaginationPorps conf = config.getParams().getPagination();
		String type = conf.getType();
		if (ParamsPorps.PaginationPorps.TYPE_PAGE.equals(type)) {
			PageNumPagination p = new PageNumPagination();
			p.setMaxAllowedSize(conf.getMaxAllowedSize());
			p.setMaxParamName(conf.getSize());
			p.setPageParamName(conf.getPage());
			p.setStartPage(conf.getStart());
			return p;
		} 
		if (ParamsPorps.PaginationPorps.TYPE_OFFSET.equals(type)) {
			MaxOffsetPagination p = new MaxOffsetPagination();
			p.setMaxAllowedSize(conf.getMaxAllowedSize());
			p.setMaxParamName(conf.getMax());
			p.setOffsetParamName(conf.getOffset());
			p.setStartOffset(conf.getStart());
			return p;
		}
		throw new SearcherException("配置项【bean-searcher.params.pagination.type】只能为 page 或  offset！");
	}

	@Bean
	@ConditionalOnMissingBean(ParamResolver.class)
	public ParamResolver searchParamResolver(Pagination pagination, BeanSearcherProperties config,
											 ObjectProvider<ParamFilter[]> paramFilterProvider) {
		DefaultParamResolver searchParamResolver = new DefaultParamResolver();
		searchParamResolver.setPagination(pagination);
		ParamsPorps conf = config.getParams();
		searchParamResolver.setDefaultMax(conf.getPagination().getDefaultSize());
		searchParamResolver.setFilterOperationParamNameSuffix(conf.getOperatorKey());
		searchParamResolver.setIgnoreCaseParamNameSuffix(conf.getIgnoreCaseKey());
		searchParamResolver.setOrderParamName(conf.getOrder());
		searchParamResolver.setSortParamName(conf.getSort());
		searchParamResolver.setParamNameSeparator(conf.getSeparator());
		ParamFilter[] paramFilters = paramFilterProvider.getIfAvailable();
		if (paramFilters != null) {
			searchParamResolver.setParamFilters(paramFilters);
		}
		return searchParamResolver;
	}

	@Bean
	@ConditionalOnMissingBean(Dialect.class)
	public Dialect dialect(BeanSearcherProperties config) {
		String dialect = config.getSql().getDialect();
		if (dialect == null) {
			throw new SearcherException("配置项【bean-searcher.sql.dialect】不能为空");
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
		throw new SearcherException("配置项【bean-searcher.sql.dialect】只能为  MySql|Oracle|PostgreSql|SqlServer 中的一个 ！");
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

	@Bean
	@ConditionalOnMissingBean(FieldConvertor.class)
	public FieldConvertor fieldConvertor(BeanSearcherProperties config) {
		DefaultFieldConvertor convertor = new DefaultFieldConvertor();
		FieldConvertorProps conf = config.getFieldConvertor();
		convertor.setIgnoreCase(conf.isIgnoreCase());
		if (conf.isIgnoreCase()) {
			convertor.setTrues(StringUtils.toUpperCase(conf.getTrues()));
			convertor.setFalses(StringUtils.toUpperCase(conf.getFalses()));
		} else {
			convertor.setTrues(conf.getTrues());
			convertor.setFalses(conf.getFalses());
		}
		return convertor;
	}
	
	@Bean
	@ConditionalOnMissingBean(BeanReflector.class)
	public BeanReflector searchResultResolver(FieldConvertor fieldConvertor) {
		return new DefaultBeanReflector(fieldConvertor);
	}

	@Bean
	@ConditionalOnMissingBean(BeanSearcher.class)
	public BeanSearcher beanSearcher(ParamResolver paramResolver,
									 SqlResolver sqlResolver,
									 SqlExecutor sqlExecutor,
									 BeanReflector beanReflector) {
		DefaultBeanSearcher searcher = new DefaultBeanSearcher();
		searcher.setSearchParamResolver(paramResolver);
		searcher.setSearchSqlResolver(sqlResolver);
		searcher.setSearchSqlExecutor(sqlExecutor);
		searcher.setSearchResultResolver(beanReflector);
		return searcher;
	}

	@Bean
	@ConditionalOnMissingBean(MapSearcher.class)
	public MapSearcher mapSearcher(ParamResolver paramResolver,
								   SqlResolver sqlResolver,
								   SqlExecutor sqlExecutor) {
		DefaultMapSearcher searcher = new DefaultMapSearcher();
		searcher.setSearchParamResolver(paramResolver);
		searcher.setSearchSqlResolver(sqlResolver);
		searcher.setSearchSqlExecutor(sqlExecutor);
		return searcher;
	}

}
