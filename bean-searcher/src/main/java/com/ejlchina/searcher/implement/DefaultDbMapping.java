package com.ejlchina.searcher.implement;

import com.ejlchina.searcher.DbMapping;
import com.ejlchina.searcher.util.StringUtils;

import java.lang.reflect.Field;

/***
 * 默认的数据库映射解析器
 * @author Troy.Zhou @ 2021-10-30
 * @since v3.1.0 从 DefaultMetaResolver 里分离出来
 */
public class DefaultDbMapping implements DbMapping {

    @Override
    public String table(Class<?> beanClass) {
        // 默认使用连字符风格的表名映射
        return StringUtils.toUnderline(beanClass.getSimpleName());
    }

    @Override
    public String column(Field field) {
        // 默认使用连字符风格的字段映射
        return StringUtils.toUnderline(field.getName());
    }

}
