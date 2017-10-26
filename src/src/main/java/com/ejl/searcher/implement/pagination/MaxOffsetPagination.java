package com.ejl.searcher.implement.pagination;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ejl.searcher.param.SearchParam;


public class MaxOffsetPagination implements Pagination {

	Log log = LogFactory.getLog(PageNumPagination.class);
	
	/**
	 * 最大条数字段参数名
	 */
	private String maxParamName = "max";

	/**
	 * 偏移条数字段参数名
	 */
	private String offsetParamName = "offset";

	
	@Override
	public boolean paginate(SearchParam searchParam, String paraName, String paraValue) {
		try {
			if (maxParamName.equals(paraName)) {
				searchParam.setMax(Integer.valueOf(paraValue));
				return true;
			}
			if (offsetParamName.equals(paraName)) {
				Long offset = Long.valueOf(paraValue);
				if (offset < 0) {
					offset = 0L;
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
		return 0;
	}

	public void setMaxParamName(String maxParamName) {
		this.maxParamName = maxParamName;
	}

	public void setOffsetParamName(String offsetParamName) {
		this.offsetParamName = offsetParamName;
	}

}
