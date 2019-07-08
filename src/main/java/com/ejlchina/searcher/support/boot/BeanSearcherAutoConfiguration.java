package com.ejlchina.searcher.support.boot;

import javax.sql.DataSource;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ejlchina.searcher.SearchParamResolver;
import com.ejlchina.searcher.SearchResultResolver;
import com.ejlchina.searcher.SearchSqlExecutor;
import com.ejlchina.searcher.SearchSqlResolver;
import com.ejlchina.searcher.Searcher;
import com.ejlchina.searcher.SearcherException;
import com.ejlchina.searcher.dialect.Dialect;
import com.ejlchina.searcher.dialect.MySqlDialect;
import com.ejlchina.searcher.dialect.OracleDialect;
import com.ejlchina.searcher.dialect.PostgreSqlDialect;
import com.ejlchina.searcher.dialect.SqlServerDialect;
import com.ejlchina.searcher.implement.MainSearchParamResolver;
import com.ejlchina.searcher.implement.MainSearchResultResolver;
import com.ejlchina.searcher.implement.MainSearchSqlExecutor;
import com.ejlchina.searcher.implement.MainSearchSqlResolver;
import com.ejlchina.searcher.implement.convertor.DefaultFieldConvertor;
import com.ejlchina.searcher.implement.convertor.FieldConvertor;
import com.ejlchina.searcher.implement.pagination.MaxOffsetPagination;
import com.ejlchina.searcher.implement.pagination.PageNumPagination;
import com.ejlchina.searcher.implement.pagination.Pagination;
import com.ejlchina.searcher.implement.parafilter.ParamFilter;
import com.ejlchina.searcher.implement.processor.DefaultParamProcessor;
import com.ejlchina.searcher.implement.processor.ParamProcessor;
import com.ejlchina.searcher.support.boot.BeanSearcherProperties.FieldConvertorProps;

import com.ejlchina.searcher.support.boot.BeanSearcherProperties.ParamsPorps;
import com.ejlchina.searcher.support.boot.BeanSearcherProperties.SqlProps;
import com.ejlchina.searcher.support.spring.SpringSearcher;
import com.ejlchina.searcher.util.StringUtils;



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
	@ConditionalOnMissingBean(SearchParamResolver.class)
	public SearchParamResolver searchParamResolver(Pagination pagination, BeanSearcherProperties config, 
			ObjectProvider<ParamFilter[]> paramFilterProvider) {
		MainSearchParamResolver searchParamResolver = new MainSearchParamResolver();
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
	@ConditionalOnMissingBean(SearchSqlResolver.class)
	public SearchSqlResolver searchSqlResolver(Dialect dialect, ParamProcessor paramProcessor) {
		return new MainSearchSqlResolver(dialect, paramProcessor);
	}
	
	
	@Bean
	@ConditionalOnMissingBean(SearchSqlExecutor.class)
	public SearchSqlExecutor searchSqlExecutor(DataSource dataSource) {
		return new MainSearchSqlExecutor(dataSource);
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
	@ConditionalOnMissingBean(SearchResultResolver.class)
	public SearchResultResolver searchResultResolver(FieldConvertor fieldConvertor) {
		return new MainSearchResultResolver(fieldConvertor);
	}
	
	
	@Bean
	@ConditionalOnMissingBean(Searcher.class)
	public Searcher beanSearcher(SearchParamResolver searchParamResolver, 
				SearchSqlResolver searchSqlResolver, 
				SearchSqlExecutor searchSqlExecutor, 
				SearchResultResolver searchResultResolver,
				BeanSearcherProperties config) {
		String[] packages = config.getPackages();
		if (packages == null || packages.length == 0) {
			throw new SearcherException("配置项【bean-searcher.packages】不能为空 ！");
		}
		SpringSearcher searcher = new SpringSearcher();
		searcher.setScanPackages(packages);
		searcher.setPrifexSeparatorLength(config.getPrifexSeparatorLength());
		searcher.setSearchParamResolver(searchParamResolver);
		searcher.setSearchSqlResolver(searchSqlResolver);
		searcher.setSearchSqlExecutor(searchSqlExecutor);
		searcher.setSearchResultResolver(searchResultResolver);
		return searcher;
	}
	
	
}
