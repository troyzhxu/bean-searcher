package com.ejlchina.searcher;

import java.util.ArrayList;
import java.util.List;


/**
 * SQL 片段
 * @author Troy.Zhou @ 2021-10-30
 * @since v3.0.0
 */
public class SqlSnippet {

	/**
	 * SQL 片段
	 */
	private String snippet;

	/**
	 * 内嵌参数
	 */
	private final List<EmbedParam> params = new ArrayList<>();
	
	public String getSnippet() {
		return snippet;
	}

	public void setSnippet(String snippet) {
		this.snippet = snippet;
	}

	public List<EmbedParam> getParams() {
		return params;
	}

	public void addEmbedParam(EmbedParam embedParam) {
		this.params.add(embedParam);
	}
	
}
