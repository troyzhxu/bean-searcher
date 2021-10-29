package com.ejlchina.searcher.virtual;

import com.ejlchina.searcher.SearcherException;
import com.ejlchina.searcher.implement.SearchBeanMap;
import com.ejlchina.searcher.util.StringUtils;

import java.util.Map;


public class DefaultVirtualParamProcessor implements VirtualParamProcessor {

	
	private String virtualParamPrefix = ":";
	
	private String[] virtualParamEndFlags = new String[] {" ", "\t", "\n", "\r", "+", "-", "*", "/", "=", "!", ">", "<", ",", ")", "'", "%"};
	
	private final char[] quotations = new char[] {'\'', '"'};

	
	@Override
	public SearchBeanMap process(SearchBeanMap searchBeanMap) {
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
			String paramName;
			if (index2 > 0) {
				paramName = sqlSnippet.substring(index1, index2);
			} else {
				paramName = sqlSnippet.substring(index1);
			}
			if (StringUtils.isBlank(paramName) || paramName.length() < 2) {
				throw new SearcherException("这里有一个语法错误（虚拟参数名）：" + sqlSnippet);
			}
			VirtualParam virtualParam = new VirtualParam(paramName);
			boolean endWithPrefix = paramName.endsWith(virtualParamPrefix);
			if (endWithPrefix) {
				virtualParam.setName(paramName.substring(1, paramName.length() - virtualParamPrefix.length()));
			} else {
				virtualParam.setName(paramName.substring(1));
			}
			int quotationCount1 = StringUtils.containCount(sqlSnippet, 0, index1, quotations);
			int quotationCount2 = StringUtils.containCount(sqlSnippet, Math.max(index1, index2), sqlSnippet.length(), quotations);
			if ((quotationCount1 + quotationCount2) % 2 != 0) {
				throw new SearcherException("这里有一个语法错误（引号不匹配）：" + sqlSnippet);
			}
			int nextIndex = index1 + paramName.length();
			// 判断虚拟参数是否不在引号内部，并且不是以 :name: 的形式
			if (quotationCount1 % 2 == 0 && !endWithPrefix) {
				virtualParam.setParameterized(true);
				sqlSnippet = sqlSnippet.replaceFirst(paramName, "?");
				// sqlSnippet 长度变短，寻找下标也该相应提前
				nextIndex = nextIndex - paramName.length() + 1;
			}
			solution.addVirtualParam(virtualParam);
			index1 = sqlSnippet.indexOf(virtualParamPrefix, nextIndex);
		}
		solution.setSqlSnippet(sqlSnippet);
		return solution;
	}
	

	private int findVitualParamEndIndex(String sqlSnippet, int fromIndex) {
		int index = -1;
		for (String flag : virtualParamEndFlags) {
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


	public String[] getVirtualParamEndFlags() {
		return virtualParamEndFlags;
	}


	public void setVirtualParamEndFlags(String[] virtualParamEndFlags) {
		this.virtualParamEndFlags = virtualParamEndFlags;
	}
	
}
