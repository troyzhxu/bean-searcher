package cn.zhxu.bs.ex;

import java.io.IOException;
import java.util.List;

/**
 * 文件输出接口
 * @author Troy.Zhou @ 2025-08-28
 * @since v4.5.0
 */
public interface FileWriter {

    /**
     * 文件输出开始时调用
     * @param fields 文件字段列表
     * @throws IOException 抛出 IOException
     */
    void writeStart(List<ExportField> fields) throws IOException;

    /**
     * 文件输出时调用
     * @param fields 文件字段列表
     * @param dataList 文件数据列表
     * @throws IOException 抛出 IOException
     */
    void writeAndFlush(List<ExportField> fields, List<?> dataList) throws IOException ;

    /**
     * 文件输出结束时调用
     * @param fields 文件字段列表
     * @throws IOException 抛出 IOException
     */
    void writeStop(List<ExportField> fields) throws IOException;

    /**
     * 很多人同时导出时调用，告诉调用者请稍后再试
     * @throws IOException 抛出 IOException
     */
    void writeTooManyRequests() throws IOException;

    /**
     * 文件输出工厂接口
     */
    interface Factory {

        /**
         * 创建文件输出对象
         * @param filename 文件名
         * @return 文件输出对象
         * @throws IOException 抛出 IOException
         */
        FileWriter create(String filename) throws IOException;

    }

}
