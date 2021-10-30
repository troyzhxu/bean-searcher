package com.ejlchina.searcher.virtual;

import java.util.ArrayList;
import java.util.List;

public class VirtualSolution {

	
	String sqlSnippet;
	
	List<EmbedParam> params = new ArrayList<>();

	
	public String getSqlSnippet() {
		return sqlSnippet;
	}

	public void setSqlSnippet(String sqlSnippet) {
		this.sqlSnippet = sqlSnippet;
	}

	public List<EmbedParam> getParams() {
		return params;
	}

	public void addVirtualParam(EmbedParam embedParam) {
		this.params.add(embedParam);
	}
	
}
