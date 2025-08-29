package cn.zhxu.bs.ex;

import java.util.List;

/**
 * 导出字段解析器
 * @author Troy.Zhou @ 2025/8/29
 * @since v4.5.0
 */
public interface ExportFieldResolver {

    /**
     * 解析导出字段
     * @param clazz 数据实体类
     * @return 导出字段列表
     */
    List<ExportField> resolve(Class<?> clazz);

}
