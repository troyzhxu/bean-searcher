package com.ejl.searcher.implement.pagination;

import com.ejl.searcher.param.SearchParam;


public class MaxOffsetPaginationResolver implements PaginationResolver {

	/**
	 * 最大条数字段参数名
	 */
	private String maxParamName = "max";

	/**
	 * 偏移条数字段参数名
	 */
	private String offsetParamName = "offset";

	
	@Override
	public boolean resolve(SearchParam searchParam, String paraName, String paraValue) {
		try {
			if (maxParamName.equals(paraName)) {
				searchParam.setMax(Integer.valueOf(paraValue));
				return true;
			}
			if (offsetParamName.equals(paraName)) {
				searchParam.setOffset(Long.valueOf(paraValue));
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
		return 0;
	}

	public void setMaxParamName(String maxParamName) {
		this.maxParamName = maxParamName;
	}

	public void setOffsetParamName(String offsetParamName) {
		this.offsetParamName = offsetParamName;
	}

}
