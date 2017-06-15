package com.ejl.searcher.support.jfinal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ejl.searcher.SearchSql;
import com.ejl.searcher.SearchSqlExecutor;
import com.ejl.searcher.SearchTmpResult;
import com.jfinal.plugin.activerecord.Db;

/**
 * Jfinal Search Sql 执行器
 * 
 * @author Troy.Zhou
 *
 */
public class DbSearchSqlExecutor implements SearchSqlExecutor {

	@Override
	public SearchTmpResult execute(SearchSql searchSql) {

		SearchTmpResult result = new SearchTmpResult();
		
		if (searchSql.isShouldQueryTotal()) {

			Number totalCount = Db.queryColumn(searchSql.getCountSqlString(), 
					searchSql.getCountSqlParams().toArray());
			
			result.setTotalCount(totalCount);
		}
		
		if (searchSql.isShouldQueryList()) {
			
			List<String> aliasList = searchSql.getAliasList();
			
			List<Object[]> list = Db.query(searchSql.getListSqlString(), 
					searchSql.getListSqlParams().toArray());
			
			for (Object[] data : list) {
				Map<String, Object> tmpData = new HashMap<>();
				for (int i = 0; i < data.length; i++) {
					tmpData.put(aliasList.get(i), data[i]);
				}
				result.addTmpData(tmpData);
			}
		}
		return result;
	}
	

}
