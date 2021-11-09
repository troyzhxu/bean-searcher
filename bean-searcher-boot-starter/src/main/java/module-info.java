module bean.searcher.boot {

    requires java.sql;
    requires bean.searcher;
    requires spring.beans;
    requires spring.context;
    requires spring.boot;
    requires spring.boot.autoconfigure;

    exports com.ejlchina.searcher.boot;

}