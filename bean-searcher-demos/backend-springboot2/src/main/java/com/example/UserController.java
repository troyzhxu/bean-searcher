package com.example;

import cn.zhxu.bs.BeanSearcher;
import cn.zhxu.bs.SearchResult;
import cn.zhxu.bs.ex.BeanExporter;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/user")
@AllArgsConstructor
public class UserController {

	private final BeanSearcher beanSearcher;
    private final BeanExporter beanExporter;

    /**
     * 进阶版，Map 参数可以省略了，因为参数都在 AutoLoadParamFilter 里统一加载了
     */
    @GetMapping("/index")
    public SearchResult<User> index() {
        // 组合检索、排序、分页 和 统计 都在这一句代码中实现了
        return beanSearcher.search(User.class, User::getAge);
    }

    /**
     * 导出数据文件
     */
    @GetMapping("/export")
    public void export() throws IOException {
        // 故意将 batchSize 调为 5，batchDelay 在配置文件中被设置为 0.5s, 大概 5 秒才能导出所有数据，模拟大数据量导出后端需要很久的情景，
        // 看前端是否不需要等待，点击导出按钮后就立即开始下载
        // BeanSearcher 导出功能的优势在于：无论数据量多大，导出时滞留在内存里的数据都只有 batchSize 条，服务器不会内存溢出
        // 并且 后端边查询，前端边下载。前端立即响应无需等待。
        beanExporter.export("员工资料", User.class, 5);
    }

}








