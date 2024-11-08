package cn.zhxu.bs.filter;

import cn.zhxu.bs.util.MapUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class IndexArrayParamFilterTestCase {

    @Test
    public void test_01() {
        IndexArrayParamFilter filter = new IndexArrayParamFilter();
        Map<String, Object> params = MapUtils.builder()
                .put("id[0]", "1")
                .put("id[1]", "2")
                .put("id[2]", "3")
                .put("id[11]", "4")
                .build();
        params = filter.doFilter(null, params);
        Assertions.assertEquals(8, params.size());
        Assertions.assertEquals("1", params.get("id-0"));
        Assertions.assertEquals("2", params.get("id-1"));
        Assertions.assertEquals("3", params.get("id-2"));
        Assertions.assertEquals("4", params.get("id-11"));
        System.out.println("\ttest_01 ok!");
    }

    @Test
    public void test_02() {
        IndexArrayParamFilter filter = new IndexArrayParamFilter("_");
        Map<String, Object> params = MapUtils.builder()
                .put("age[0]", "21")
                .put("age[1]", "22")
                .put("age[2]", "30")
                .put("age_op", "il")
                .build();
        params = filter.doFilter(null, params);
        Assertions.assertEquals(7, params.size());
        Assertions.assertEquals("il", params.get("age_op"));
        Assertions.assertEquals("21", params.get("age_0"));
        Assertions.assertEquals("22", params.get("age_1"));
        Assertions.assertEquals("30", params.get("age_2"));
        System.out.println("\ttest_02 ok!");
    }

    @Test
    public void test_03() {
        IndexArrayParamFilter filter = new IndexArrayParamFilter();
        Map<String, Object> params = MapUtils.builder()
                .put("id[", "1")
                .put("id]", "2")
                .put("id[0", "3")
                .put("id0]", "4")
                .put("id[]", "5")
                .put("id[x]", "6")
                .put("[0]", "7")
                .put("age", "[32")
                .build();
        params = filter.doFilter(null, params);
        Assertions.assertEquals(8, params.size());
        Assertions.assertEquals("1", params.get("id["));
        Assertions.assertEquals("2", params.get("id]"));
        Assertions.assertEquals("3", params.get("id[0"));
        Assertions.assertEquals("4", params.get("id0]"));
        Assertions.assertEquals("5", params.get("id[]"));
        Assertions.assertEquals("6", params.get("id[x]"));
        Assertions.assertEquals("7", params.get("[0]"));
        Assertions.assertEquals("[32", params.get("age"));
        System.out.println(getClass() + " test_03 ok!");
    }

}
