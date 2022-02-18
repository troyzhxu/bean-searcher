package com.ejlchina.searcher;

import java.util.ArrayList;
import java.util.List;

/**
 * SQL 片段
 * @author Troy.Zhou @ 2021-10-30
 * @since v3.4.0
 * @param <T> 泛型
 */
public class SqlWrapper<T> {

    /**
     * SQL 片段
     */
    private String sql;

    /**
     * 参数
     */
    private final List<T> paras = new ArrayList<>();

    public SqlWrapper() { }

    public SqlWrapper(String sql) {
        this.sql = sql;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public List<T> getParas() {
        return paras;
    }

    public void addPara(T para) {
        this.paras.add(para);
    }

    public void addParas(List<T> paras) {
        this.paras.addAll(paras);
    }

}
