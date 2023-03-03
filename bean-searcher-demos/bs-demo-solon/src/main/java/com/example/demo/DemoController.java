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

    /**
     * 请求参数在哪里? 参见 {@link Config#currentRequestParamFilter() }<p>
     * 如果没有配置那个参数过滤器，这里只需这么写即可：
     * <pre>{@code
     * Map<String, Object> params = new HashMap<>(Context.current().paramMap());
     * return beanSearcher.search(Employee.class, params, Employee::getAge);
     * }</pre>
     * @return SearchResult<Employee>
     */
    @Mapping("/employees")
    public SearchResult<Employee> employees() {
        // 分页查询员工信息，并对年龄进行统计
        return beanSearcher.search(Employee.class, Employee::getAge);
    }

}
