package com.ejlchina.searcher.virtual;

import java.util.ArrayList;
import java.util.List;

public class VirtualSolution {

	
	String sqlSnippet;
	
	List<VirtualParam> virtualParams = new ArrayList<>();

	
	public String getSqlSnippet() {
		return sqlSnippet;
	}

	public void setSqlSnippet(String sqlSnippet) {
		this.sqlSnippet = sqlSnippet;
	}

	public List<VirtualParam> getVirtualParams() {
		return virtualParams;
	}

	public void addVirtualParam(VirtualParam virtualParam) {
		this.virtualParams.add(virtualParam);
	}
	
}
