module bean.searcher {

    requires java.sql;
    requires org.slf4j;

    exports com.ejlchina.searcher;
    exports com.ejlchina.searcher.bean;
    exports com.ejlchina.searcher.dialect;
    exports com.ejlchina.searcher.operator;
    exports com.ejlchina.searcher.implement;
    exports com.ejlchina.searcher.param;
    exports com.ejlchina.searcher.util;
    exports com.ejlchina.searcher.group;

}