package cn.zhxu.bs.ex;

/**
 * 文件命名接口
 * @author Troy.Zhou @ 2025-08-28
 * @since v4.5.0
 */
public interface FileNamer {

    /**
     * 默认实现
     */
    FileNamer SELF = name -> name;

    /**
     * 获取文件名
     * @param name 文件名
     * @return 文件名
     */
    String filename(String name);

}
