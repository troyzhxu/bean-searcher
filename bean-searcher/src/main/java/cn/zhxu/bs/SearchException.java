package cn.zhxu.bs;

/**
 * 检索器异常
 * 
 * @author Troy.Zhou @ 2017-03-20
 * */
public class SearchException extends RuntimeException {

    public SearchException(String message) {
        super(message);
    }

    public SearchException(String message, Throwable cause) {
        super(message, cause);
    }

}
