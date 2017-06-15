package com.ejl.searcher.support.spring;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

import com.ejl.searcher.SearchSql;
import com.ejl.searcher.SearchSqlExecutor;
import com.ejl.searcher.SearchTmpResult;


/**
 * Spring Search Sql 执行器
 * 
 * @author Troy.Zhou
 *
 */
public class JdbcSearchSqlExecutor implements SearchSqlExecutor {

	
	private JdbcTemplate jdbcTemplate;
	
	
	@Override
	public SearchTmpResult execute(SearchSql searchSql) {
		if (jdbcTemplate == null) {
			throw new RuntimeException("请为SpringSearchSqlExecutor配置dataSource或jdbcTemplate");
		}
		SearchTmpResult result = new SearchTmpResult();
		
		if (searchSql.isShouldQueryTotal()) {

			Number totalCount = jdbcTemplate.queryForObject(searchSql.getCountSqlString(), 
					searchSql.getCountSqlParams().toArray(), Number.class);
			
			result.setTotalCount(totalCount);
		}
		if (searchSql.isShouldQueryList()) {
			
			List<Map<String, Object>> list = jdbcTemplate.queryForList(searchSql.getListSqlString(), 
					searchSql.getListSqlParams().toArray());
			
			result.setTmpDataList(list);
		}
		return result;
	}

	/**
	 * 设置数据源
	 * dataSource 与 jdbcTemplate 设置一个就可以
	 * @param dataSource 数据源
	 */
	public void setDataSource(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
	}

	
}
