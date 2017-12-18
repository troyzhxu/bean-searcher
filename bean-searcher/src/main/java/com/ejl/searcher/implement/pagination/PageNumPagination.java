package com.ejl.searcher.implement.pagination;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ejl.searcher.param.SearchParam;



public class PageNumPagination implements Pagination {

	Log log = LogFactory.getLog(PageNumPagination.class);
	
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
	public boolean paginate(SearchParam searchParam, String paraName, String paraValue) {
		try {
			if (maxParamName.equals(paraName)) {
				Integer max = Integer.valueOf(paraValue);
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
				Long page = Long.valueOf(paraValue);
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
		} catch (Exception e) {
			log.error("解析分页参数异常：", e);
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

	public void setMaxAllowedSize(int maxAllowedSize) {
		this.maxAllowedSize = maxAllowedSize;
	}

}
