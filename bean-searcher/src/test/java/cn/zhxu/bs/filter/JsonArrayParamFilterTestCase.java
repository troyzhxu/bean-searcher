package cn.zhxu.bs.filter;

import cn.zhxu.bs.util.MapUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class JsonArrayParamFilterTestCase {

    @Test
    public void test_01() {
        JsonArrayParamFilter filter = new JsonArrayParamFilter();
        Map<String, Object> params = MapUtils.builder()
                .put("id", "[1,2,3]")
                .build();
        params = filter.doFilter(null, params);
        Assertions.assertEquals(4, params.size());
        Assertions.assertEquals("1", params.get("id-0"));
        Assertions.assertEquals("2", params.get("id-1"));
        Assertions.assertEquals("3", params.get("id-2"));
        System.out.println("\ttest_01 ok!");
    }

    @Test
    public void test_02() {
        JsonArrayParamFilter filter = new JsonArrayParamFilter("_");
        Map<String, Object> params = MapUtils.builder()
                .put("age", "[21,22,30]")
                .put("age_op", "il")
                .build();
        params = filter.doFilter(null, params);
        Assertions.assertEquals(5, params.size());
        Assertions.assertEquals("il", params.get("age_op"));
        Assertions.assertEquals("21", params.get("age_0"));
        Assertions.assertEquals("22", params.get("age_1"));
        Assertions.assertEquals("30", params.get("age_2"));
        System.out.println("\ttest_02 ok!");
    }

    @Test
    public void test_03() {
        JsonArrayParamFilter filter = new JsonArrayParamFilter();
        Map<String, Object> params = MapUtils.builder()
                .put("id", "1,2,3]")
                .put("age", "[32")
                .build();
        params = filter.doFilter(null, params);
        Assertions.assertEquals(2, params.size());
        Assertions.assertEquals("1,2,3]", params.get("id"));
        Assertions.assertEquals("[32", params.get("age"));
        System.out.println(getClass() + " test_03 ok!");
    }

}
