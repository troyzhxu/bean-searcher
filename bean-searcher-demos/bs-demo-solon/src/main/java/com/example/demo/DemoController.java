package com.example.demo;

import cn.zhxu.bs.BeanSearcher;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Param;

@Controller
public class DemoController {

    @Inject
    private BeanSearcher beanSearcher;

    @Mapping("/hello")
    public String hello(@Param(defaultValue = "world") String name) {
        System.out.println("beanSearcher = " + beanSearcher);
        System.out.println("Solon.context().getBean(BeanSearcher.class) = " + Solon.context().getBean(BeanSearcher.class));
        return String.format("Hello %s!", name);
    }

}
