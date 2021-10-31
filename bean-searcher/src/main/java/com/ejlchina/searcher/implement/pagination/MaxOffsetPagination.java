package com.ejlchina.searcher.implement.pagination;

import com.ejlchina.searcher.param.PageParam;
import com.ejlchina.searcher.util.MapBuilder;
import com.ejlchina.searcher.util.ObjectUtils;

import java.util.Map;


public class MaxOffsetPagination implements Pagination {

	/**
	 * 开始偏移
	 * */
	private int startOffset = 0;
	
	/**
	 * 最大条数字段参数名
	 */
	private String sizeParamName = "size";

	/**
	 * 偏移条数字段参数名
	 */
	private String offsetParamName = "offset";

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
		long offset = toOffset(paraMap.get(offsetParamName));
		int size = toSize(paraMap.get(sizeParamName));
		return new PageParam(size, offset);
	}

	protected long toOffset(Object value) {
		Long offset = ObjectUtils.toLong(value);
		if (offset == null) {
			return 0;
		}
		return Math.max(offset - startOffset, 0);
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
		return startOffset;
	}

	public void setSizeParamName(String sizeParamName) {
		MapBuilder.config(MapBuilder.MAX, sizeParamName);
		this.sizeParamName = sizeParamName;
	}

	public void setOffsetParamName(String offsetParamName) {
		MapBuilder.config(MapBuilder.OFFSET, offsetParamName);
		this.offsetParamName = offsetParamName;
	}

	public void setMaxAllowedSize(int maxAllowedSize) {
		this.maxAllowedSize = maxAllowedSize;
	}

	public int getStartOffset() {
		return startOffset;
	}

	public void setStartOffset(int startOffset) {
		this.startOffset = startOffset;
	}


	public String getOffsetParamName() {
		return offsetParamName;
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
