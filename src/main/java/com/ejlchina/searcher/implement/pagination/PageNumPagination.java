package com.ejlchina.searcher.implement.pagination;

import com.ejlchina.searcher.param.SearchParam;
import com.ejlchina.searcher.util.ObjectUtils;


public class PageNumPagination implements Pagination {

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

	/**
	 * 最大允许查询条数
	 */
	private int maxAllowedSize = 100;
	
	
	@Override
	public boolean paginate(SearchParam searchParam, String paraName, Object paraValue) {
		if (maxParamName.equals(paraName)) {
			Integer max = ObjectUtils.toInt(paraValue);
			if (max == null) {
				return false;
			}
			if (max > maxAllowedSize) {
				max = maxAllowedSize;
			}
			searchParam.setMax(max);
			Long page = searchParam.getPage();
			if (page == null) {
				return true;
			}
			long offset = (page - startPage) * max;
			if (offset < 0) {
				offset = 0;
			}
			searchParam.setOffset(offset);
			return true;
		}
		if (pageParamName.equals(paraName)) {
			Long page = ObjectUtils.toLong(paraValue);
			if (page == null) {
				return false;
			}
			searchParam.setPage(page);
			Integer max = searchParam.getMax();
			if (max == null) {
				return true;
			}
			long offset = (page - startPage) * max;
			if (offset < 0) {
				offset = 0;
			}
			searchParam.setOffset(offset);
			return true;
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

	public void setMaxAllowedSize(int maxAllowedSize) {
		this.maxAllowedSize = maxAllowedSize;
	}

}
