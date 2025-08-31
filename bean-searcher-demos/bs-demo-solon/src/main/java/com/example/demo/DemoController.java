package com.example.demo;

import cn.zhxu.bs.BeanSearcher;
import cn.zhxu.bs.SearchResult;
import cn.zhxu.bs.ex.BeanExporter;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.ModelAndView;
import org.noear.solon.data.annotation.Tran;
import org.noear.wood.DbContext;
import org.noear.wood.annotation.Db;

import java.io.IOException;
import java.sql.SQLException;

@Controller
public class DemoController {

    @Db
    private DbContext db;

    @Inject
    private BeanSearcher beanSearcher;

    @Inject
    private BeanExporter beanExporter;

    @Mapping("/")
    public ModelAndView index() {
        return new ModelAndView("index.html");
    }

    /**
     * 请求参数在哪里? 参见 {@link ReqParamFilter }<p>
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

    @Mapping("/employees.cvs")
    public void exportEmployees() throws IOException {
        // 导出数据文件，将 batchSize 调为 1，batchDelay 配置为 0.5s, 测试其是否可以边查询边下载
        beanExporter.export("员工资料", Employee.class, 1);
    }

    /**
     * 事务测试
     */
    @Tran
    @Mapping("/create")
    public Employee create(int id, String name) throws SQLException {
        // 使用其它 ORM 插入数据
        db.sql("INSERT INTO `employee` VALUES (?, ?,22,'Male',now(),1)", id, name).execute();
        // 使用 Bean Searcher 把刚插入的数据再查出来（v4.3.2 开始 默认支持事务内查询）
        return beanSearcher.searchFirst(Employee.class);
    }

}
