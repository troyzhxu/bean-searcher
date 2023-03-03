package com.example.demo;

import cn.zhxu.bs.BeanSearcher;
import cn.zhxu.bs.SearchResult;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.ModelAndView;

@Controller
public class DemoController {

    @Inject
    private BeanSearcher beanSearcher;

    @Mapping("/")
    public ModelAndView index() {
        return new ModelAndView("index.html");
    }

    @Mapping("/employees")
    public SearchResult<Employee> employees() {
        return beanSearcher.search(Employee.class);
    }

}
