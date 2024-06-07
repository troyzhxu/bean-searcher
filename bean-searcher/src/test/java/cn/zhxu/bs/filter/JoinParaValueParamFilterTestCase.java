package cn.zhxu.bs.filter;

import cn.zhxu.bs.BeanMeta;
import cn.zhxu.bs.MetaResolver;
import cn.zhxu.bs.bean.SearchBean;
import cn.zhxu.bs.implement.DefaultMetaResolver;
import cn.zhxu.bs.util.MapUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class JoinParaValueParamFilterTestCase {

    JoinParaValueParamFilter filter = new JoinParaValueParamFilter();
    MetaResolver metaResolver = new DefaultMetaResolver();

    @SearchBean(where = "id in (:idList:)")
    public static class User {
        private int id;
    }

    BeanMeta<User> beanMeta = metaResolver.resolve(User.class);

    @Test
    public void test_01() {
        List<Integer> otherList = Arrays.asList(4, 5, 6);
        Map<String, Object> params = MapUtils.builder()
                .put("idList", Arrays.asList(1, 2, 3))
                .put("otherList", otherList)
                .build();
        params = filter.doFilter(beanMeta, params);
        Assertions.assertEquals(2, params.size());
        Assertions.assertEquals("1,2,3", params.get("idList"));
        Assertions.assertEquals(otherList, params.get("otherList"));
        System.out.println("\ttest_01 passed!");
    }

    @Test
    public void test_02() {
        Map<String, Object> params = MapUtils.builder()
                .put("age", 30)
                .put("idList", Arrays.asList(1, 2, 3))
                .put("name", "Jack")
                .build();
        params = filter.doFilter(beanMeta, params);
        Assertions.assertEquals(3, params.size());
        Assertions.assertEquals(30, params.get("age"));
        Assertions.assertEquals("1,2,3", params.get("idList"));
        Assertions.assertEquals("Jack", params.get("name"));
        System.out.println("\ttest_02 passed!");
    }

}
