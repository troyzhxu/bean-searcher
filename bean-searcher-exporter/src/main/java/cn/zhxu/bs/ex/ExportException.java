package cn.zhxu.bs.ex;

/**
 * 导出异常
 * @author Troy.Zhou @ 2025-08-28
 * @since v4.5.0
 * */
public class ExportException extends RuntimeException {

    public ExportException(String message) {
        super(message);
    }

    public ExportException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 太多人同时导出异常
     */
    public static class TooManyRequests extends ExportException {

        public TooManyRequests(String message) {
            super(message);
        }

    }

}
