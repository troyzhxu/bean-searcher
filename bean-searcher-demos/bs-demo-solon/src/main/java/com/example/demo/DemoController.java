package com.example.demo;

import cn.zhxu.bs.BeanSearcher;
import cn.zhxu.bs.SearchResult;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;

@org.noear.solon.annotation.Controller
public class DemoController {

    @Inject
    private BeanSearcher beanSearcher;

    @Mapping("/")
    public String index() {
        System.out.println("beanSearcher = " + beanSearcher);
        return "You can request";
    }

    @Mapping("/employee")
    public SearchResult<Employee> employee() {
        return beanSearcher.search(Employee.class);
    }

}
