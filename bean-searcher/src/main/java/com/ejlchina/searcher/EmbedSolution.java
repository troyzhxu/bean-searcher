package com.ejlchina.searcher;

import java.util.ArrayList;
import java.util.List;


/**
 * 内嵌参数解析结果
 */
public class EmbedSolution {

	private String sqlSnippet;

	private final List<EmbedParam> params = new ArrayList<>();
	
	public String getSqlSnippet() {
		return sqlSnippet;
	}

	public void setSqlSnippet(String sqlSnippet) {
		this.sqlSnippet = sqlSnippet;
	}

	public List<EmbedParam> getParams() {
		return params;
	}

	public void addEmbedParam(EmbedParam embedParam) {
		this.params.add(embedParam);
	}
	
}
