package com.ejl.searcher.implement.pagination;

import com.ejl.searcher.param.SearchParam;



public class PageNumPaginationResolver implements PaginationResolver {

	/**
	 * 开始页
	 * */
	private int startPage = 0;
	
	/**
	 * 最大条数字段参数名
	 */
	private String maxParamName = "size";

	/**
	 * 偏移条数字段参数名
	 */
	private String pageParamName = "page";
	
	
	@Override
	public boolean resolve(SearchParam searchParam, String paraName, String paraValue) {
		try {
			if (maxParamName.equals(paraName)) {
				Integer max = Integer.valueOf(paraValue);
				searchParam.setMax(max);
				Long page = searchParam.getPage();
				if (page != null) {
					searchParam.setOffset((page - startPage) * max);
				}
				return true;
			}
			if (pageParamName.equals(paraName)) {
				Long page = Long.valueOf(paraValue);
				searchParam.setPage(page);
				Integer max = searchParam.getMax();
				if (max != null) {
					searchParam.setOffset((page - startPage) * max);
				}
				return true;
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}

	@Override
	public String getMaxParamName() {
		return maxParamName;
	}
	
	@Override
	public int getStartPage() {
		return startPage;
	}

	public void setMaxParamName(String maxParamName) {
		this.maxParamName = maxParamName;
	}

	public void setPageParamName(String pageParamName) {
		this.pageParamName = pageParamName;
	}

	public void setStartPage(int startPage) {
		this.startPage = startPage;
	}


}
