package cn.zhxu.bs;

/**
 * 非法参数异常
 * @author Troy.Zhou @ 2022-07-07
 * @since v3.8.1
 */
public class IllegalParamException extends Exception {

    public IllegalParamException(String message) {
        super(message);
    }

    public IllegalParamException(String message, Throwable cause) {
        super(message, cause);
    }

}
