package com.ejlchina.searcher.implement.pagination;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ejlchina.searcher.param.SearchParam;
import com.ejlchina.searcher.util.ObjectUtils;


public class MaxOffsetPagination implements Pagination {


	protected Logger log = LoggerFactory.getLogger(MaxOffsetPagination.class);
	
	/**
	 * 最大条数字段参数名
	 */
	private String maxParamName = "max";

	/**
	 * 偏移条数字段参数名
	 */
	private String offsetParamName = "offset";

	
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
			return true;
		}
		if (offsetParamName.equals(paraName)) {
			Long offset = ObjectUtils.toLong(paraValue);
			if (offset == null) {
				return false;
			}
			if (offset < 0) {
				offset = 0L;
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
		return 0;
	}

	public void setMaxParamName(String maxParamName) {
		this.maxParamName = maxParamName;
	}

	public void setOffsetParamName(String offsetParamName) {
		this.offsetParamName = offsetParamName;
	}

	public void setMaxAllowedSize(int maxAllowedSize) {
		this.maxAllowedSize = maxAllowedSize;
	}

}
