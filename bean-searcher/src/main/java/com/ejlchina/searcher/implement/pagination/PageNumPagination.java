package com.ejlchina.searcher.implement.pagination;

import com.ejlchina.searcher.param.PageParam;
import com.ejlchina.searcher.util.MapBuilder;
import com.ejlchina.searcher.util.ObjectUtils;

import java.util.Map;


public class PageNumPagination implements Pagination {

	/**
	 * 开始页
	 * */
	private int startPage = 0;
	
	/**
	 * 最大条数字段参数名
	 */
	private String sizeParamName = "size";

	/**
	 * 偏移条数字段参数名
	 */
	private String pageParamName = "page";

	/**
	 * 最大允许查询条数
	 */
	private int maxAllowedSize = 100;

	/**
	 * 默认分页大小
	 */
	private int defaultSize = 15;


	@Override
	public PageParam paginate(Map<String, Object> paraMap) {
		long page = toPage(paraMap.get(pageParamName));
		int size = toSize(paraMap.get(sizeParamName));
		return new PageParam(size, page * size);
	}

	protected long toPage(Object value) {
		Long page = ObjectUtils.toLong(value);
		if (page == null) {
			return 0;
		}
		return Math.max(page - startPage, 0);
	}

	protected int toSize(Object value) {
		Integer size = ObjectUtils.toInt(value);
		if (size == null) {
			return defaultSize;
		}
		return Math.min(Math.max(size, 0), maxAllowedSize);
	}


	@Override
	public String getSizeParamName() {
		return sizeParamName;
	}
	
	@Override
	public int getStartPage() {
		return startPage;
	}

	public void setSizeParamName(String sizeParamName) {
		MapBuilder.config(MapBuilder.SIZE, sizeParamName);
		this.sizeParamName = sizeParamName;
	}

	public void setPageParamName(String pageParamName) {
		MapBuilder.config(MapBuilder.PAGE, pageParamName);
		this.pageParamName = pageParamName;
	}

	public void setStartPage(int startPage) {
		this.startPage = startPage;
	}

	public void setMaxAllowedSize(int maxAllowedSize) {
		this.maxAllowedSize = maxAllowedSize;
	}

	public String getPageParamName() {
		return pageParamName;
	}

	public int getMaxAllowedSize() {
		return maxAllowedSize;
	}

	public int getDefaultSize() {
		return defaultSize;
	}

	public void setDefaultSize(int defaultSize) {
		this.defaultSize = defaultSize;
	}
}
