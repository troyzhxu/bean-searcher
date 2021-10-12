package com.ejlchina.searcher.virtual;

import com.ejlchina.searcher.SearcherException;
import com.ejlchina.searcher.beanmap.SearchBeanMap;
import com.ejlchina.searcher.util.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class DefaultVirtualParamProcessor implements VirtualParamProcessor {

	
	private String virtualParamPrefix = ":";
	
	private String[] vertualParamEndFlags = new String[] {" ", "\t", "\n", "\r", "+", "-", "*", "/", "=", "!", ">", "<", ",", ")", "'", "%"};
	
	private final char[] quotations = new char[] {'\'', '"'};
	
	private final Map<SearchBeanMap, Object> processed = new ConcurrentHashMap<>();
	
	
	@Override
	public SearchBeanMap process(SearchBeanMap searchBeanMap) {
		if (processed.get(searchBeanMap) != null) {
			return searchBeanMap;
		}
		processed.put(searchBeanMap, new Object());
		return doProcess(searchBeanMap);
	}
	
	
	private SearchBeanMap doProcess(SearchBeanMap searchBeanMap) {
		
		VirtualSolution solution = resolveVirtualParams(searchBeanMap.getTalbes());
		searchBeanMap.setTalbes(solution.getSqlSnippet());
		searchBeanMap.setTableVirtualParams(solution.getVirtualParams());
		
		solution = resolveVirtualParams(searchBeanMap.getJoinCond());
		searchBeanMap.setJoinCond(solution.getSqlSnippet());
		searchBeanMap.setJoinCondVirtualParams(solution.getVirtualParams());
		
		Map<String, String> fieldDbMap = searchBeanMap.getFieldDbMap();
		for (String field : searchBeanMap.getFieldList()) {
			solution = resolveVirtualParams(fieldDbMap.get(field));
			fieldDbMap.put(field, solution.getSqlSnippet());
			searchBeanMap.putFieldVirtualParam(field, solution.getVirtualParams());
		}
		
		return searchBeanMap;
	}
	
	
	private VirtualSolution resolveVirtualParams(String sqlSnippet) {
		VirtualSolution solution = new VirtualSolution();
		int index1 = sqlSnippet.indexOf(virtualParamPrefix);
		while (index1 >= 0) {
			int index2 = findVitualParamEndIndex(sqlSnippet, index1);
			String paramName = null;
			if (index2 > 0) {
				paramName = sqlSnippet.substring(index1, index2);
			} else {
				paramName = sqlSnippet.substring(index1);
			}
			if (StringUtils.isBlank(paramName) || paramName.length() < 2) {
				throw new SearcherException("这里有一个语法错误（虚拟参数名）：" + sqlSnippet);
			}
			VirtualParam virtualParam = new VirtualParam();
			
			virtualParam.setName(paramName.substring(1));
			virtualParam.setSqlName(paramName);
			
			int quotationCount1 = StringUtils.containCount(sqlSnippet, 0, index1, quotations);
			int quotationCount2 = StringUtils.containCount(sqlSnippet, Math.max(index1, index2), sqlSnippet.length(), quotations);
			if ((quotationCount1 + quotationCount2) % 2 != 0) {
				throw new SearcherException("这里有一个语法错误（引号不匹配）：" + sqlSnippet);
			}
			// 判断虚拟参数是否在引号内部
			if (quotationCount1 % 2 == 0) {
				virtualParam.setParameterized(true);
				sqlSnippet = sqlSnippet.replaceFirst(paramName, "?");
			}
			solution.addVirtualParam(virtualParam);
			index1 = sqlSnippet.indexOf(virtualParamPrefix, index1 + 1);
		}
		solution.setSqlSnippet(sqlSnippet);
		return solution;
	}
	

	private int findVitualParamEndIndex(String sqlSnippet, int fromIndex) {
		int index = -1;
		for (String flag : vertualParamEndFlags) {
			int index0 = sqlSnippet.indexOf(flag, fromIndex);
			if (index < 0) {
				index = index0;
			} else if (index0 > 0) {
				index = Math.min(index, index0);
			}
		}
		return index;
	}


	public String getVirtualParamPrefix() {
		return virtualParamPrefix;
	}


	public void setVirtualParamPrefix(String virtualParamPrefix) {
		this.virtualParamPrefix = virtualParamPrefix;
	}


	public String[] getVertualParamEndFlags() {
		return vertualParamEndFlags;
	}


	public void setVertualParamEndFlags(String[] vertualParamEndFlags) {
		this.vertualParamEndFlags = vertualParamEndFlags;
	}
	
}
