package com.ejlchina.searcher.virtual;

import com.ejlchina.searcher.EmbedParam;
import com.ejlchina.searcher.EmbedSolution;
import com.ejlchina.searcher.SearcherException;
import com.ejlchina.searcher.Metadata;
import com.ejlchina.searcher.util.StringUtils;

import java.util.Map;


public class DefaultVirtualParamProcessor implements VirtualParamProcessor {

	
	private String virtualParamPrefix = ":";
	
	private String[] virtualParamEndFlags = new String[] {" ", "\t", "\n", "\r", "+", "-", "*", "/", "=", "!", ">", "<", ",", ")", "'", "%"};
	
	private final char[] quotations = new char[] {'\'', '"'};

	
	@Override
	public Metadata process(Metadata metadata) {
		EmbedSolution solution = resolveVirtualParams(metadata.getTalbes());
		metadata.setTalbes(solution.getSqlSnippet());
		metadata.setTableEmbedParams(solution.getParams());
		
		solution = resolveVirtualParams(metadata.getJoinCond());
		metadata.setJoinCond(solution.getSqlSnippet());
		metadata.setJoinCondEmbedParams(solution.getParams());
		
		Map<String, String> fieldDbMap = metadata.getFieldDbMap();
		for (String field : metadata.getFieldList()) {
			solution = resolveVirtualParams(fieldDbMap.get(field));
			fieldDbMap.put(field, solution.getSqlSnippet());
			metadata.putFieldEmbedParams(field, solution.getParams());
		}
		return metadata;
	}
	
	
	private EmbedSolution resolveVirtualParams(String sqlSnippet) {
		EmbedSolution solution = new EmbedSolution();
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
			EmbedParam embedParam = new EmbedParam(paramName);
			boolean endWithPrefix = paramName.endsWith(virtualParamPrefix);
			if (endWithPrefix) {
				embedParam.setName(paramName.substring(1, paramName.length() - virtualParamPrefix.length()));
			} else {
				embedParam.setName(paramName.substring(1));
			}
			int quotationCount1 = StringUtils.containCount(sqlSnippet, 0, index1, quotations);
			int quotationCount2 = StringUtils.containCount(sqlSnippet, Math.max(index1, index2), sqlSnippet.length(), quotations);
			if ((quotationCount1 + quotationCount2) % 2 != 0) {
				throw new SearcherException("这里有一个语法错误（引号不匹配）：" + sqlSnippet);
			}
			int nextIndex = index1 + paramName.length();
			// 判断虚拟参数是否不在引号内部，并且不是以 :name: 的形式
			if (quotationCount1 % 2 == 0 && !endWithPrefix) {
				embedParam.setParameterized(true);
				sqlSnippet = sqlSnippet.replaceFirst(paramName, "?");
				// sqlSnippet 长度变短，寻找下标也该相应提前
				nextIndex = nextIndex - paramName.length() + 1;
			}
			solution.addEmbedParam(embedParam);
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
