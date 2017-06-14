package com.ejl.searcher.temp;

import java.util.HashMap;


/**
 * 默认检索中间对象
 * @author Troy.Zhou
 *
 */
public class DefaultSearchTmpData extends HashMap<String, Object> implements SearchTmpData {


	/**
	 * 
	 */
	private static final long serialVersionUID = -5220682438028591902L;

	@Override
	public Object get(String dbAlias) {
		return get((Object)dbAlias);
	}


}
