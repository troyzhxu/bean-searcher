package com.ejlchina.searcher.implement;

import com.ejlchina.searcher.util.ObjectUtils;

import java.util.Map;


public class PageOffsetExtractor extends BasePageExtractor {

	/**
	 * 偏移条数字段参数名
	 */
	private String offsetName = "offset";

	@Override
	protected long toOffset(Map<String, Object> paraMap, int size) {
		Object value = paraMap.get(offsetName);
		Long offset = ObjectUtils.toLong(value);
		if (offset == null) {
			return 0;
		}
		return Math.max(offset - getStart(), 0);
	}

	public void setOffsetName(String offsetName) {
		this.offsetName = offsetName;
	}

	public String getOffsetName() {
		return offsetName;
	}

}
