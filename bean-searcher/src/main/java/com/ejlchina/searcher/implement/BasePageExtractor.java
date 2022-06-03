package com.ejlchina.searcher.implement;

import com.ejlchina.searcher.PageExtractor;
import com.ejlchina.searcher.param.Paging;
import com.ejlchina.searcher.util.MapBuilder;
import com.ejlchina.searcher.util.ObjectUtils;

import java.util.Map;


public abstract class BasePageExtractor implements PageExtractor {

	/**
	 * 开始页，或开始条数
	 * */
	private int start = 0;
	
	/**
	 * 最大条数字段参数名
	 */
	private String sizeName = "size";

	/**
	 * 最大允许查询条数
	 */
	private int maxAllowedSize = 100;

	/**
	 * 默认分页大小
	 */
	private int defaultSize = 15;

	@Override
	public Paging extract(Map<String, Object> paraMap) {
		Object value = paraMap.get(MapBuilder.PAGING);
		if (value instanceof MapBuilder.Page) {
			return toPaging((MapBuilder.Page) value);
		}
		if (value instanceof MapBuilder.Limit) {
			return toPaging((MapBuilder.Limit) value);
		}
		int size = toSize(paraMap.get(getSizeName()));
		return new Paging(size, toOffset(paraMap, size));
	}

	protected abstract long toOffset(Map<String, Object> paraMap, int size);

	protected int toSize(Object value) {
		Integer size = ObjectUtils.toInt(value);
		if (size == null) {
			return defaultSize;
		}
		return allowSize(size);
	}

	protected int allowSize(int size) {
		return Math.min(Math.max(size, 0), maxAllowedSize);
	}

	protected Paging toPaging(MapBuilder.Page page) {
		int size = allowSize(page.getSize());
		long pageNo = Math.max(page.getPage() - start, 0);
		return new Paging(size, size * pageNo);
	}

	protected Paging toPaging(MapBuilder.Limit limit) {
		int size = allowSize(limit.getSize());
		long offset = Math.max(limit.getOffset() - start, 0);
		return new Paging(size, offset);
	}

	@Override
	public String getSizeName() {
		return sizeName;
	}
	
	public int getStart() {
		return start;
	}

	public void setSizeName(String sizeName) {
		this.sizeName = sizeName;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public void setMaxAllowedSize(int maxAllowedSize) {
		this.maxAllowedSize = maxAllowedSize;
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
