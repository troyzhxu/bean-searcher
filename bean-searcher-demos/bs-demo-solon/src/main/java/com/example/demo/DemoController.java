package com.example.demo;

import cn.zhxu.bs.BeanSearcher;
import cn.zhxu.bs.SearchResult;
import cn.zhxu.bs.util.MapUtils;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.ModelAndView;
import org.noear.solon.data.annotation.Tran;
import org.noear.wood.DbContext;
import org.noear.wood.annotation.Db;

import java.sql.SQLException;

@Controller
public class DemoController {

    @Db
    @Inject
    private DbContext db;

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

    @Tran
    @Mapping("/create")
    public Employee create(int id, String name) throws SQLException {
        // 事务测试
        db.sql("INSERT INTO `employee` VALUES (?, ?,22,'Male',now(),1)", id, name).execute();
        var params = MapUtils.builder()
                .field(Employee::getId, id)
                .build();
        return beanSearcher.searchFirst(Employee.class, params);
    }

}
