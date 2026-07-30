package com.example.mapper;

import com.example.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 用户数据访问层。
 *
 * 对比 Bean Searcher：Bean Searcher 无需手写 Mapper，检索/分页/排序/统计全部由框架自动生成 SQL；
 * 这里必须手动声明每一个查询方法，并在 XML 中手写动态 SQL。
 */
public interface UserMapper {

    /**
     * 分页检索。参数中的 offset / size 用于 LIMIT（导出时不含 offset，则查全部）。
     */
    List<User> search(Map<String, Object> params);

    /**
     * 符合条件的总条数（用于分页）。
     */
    long count(Map<String, Object> params);

    /**
     * 年龄求和（对应 Bean Searcher search(User.class, User::getAge) 的 summaries）。
     */
    int sumAge(Map<String, Object> params);

}
