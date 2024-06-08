package cn.zhxu.bs.filter;

import cn.zhxu.bs.util.MapUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class ArrayValueParamFilterTestCase {

    ArrayValueParamFilter filter = new ArrayValueParamFilter();

    @Test
    public void test_01() {
        Map<String, String[]> map = new HashMap<>();
        map.put("id", new String[] { "1", "2", "3" });
        map.put("id-op", new String[] { "il" });
        map.put("name", new String[] { "Jack" });
        map.put("age-bt", new String[] { "20", "30" });
        map.put("sex", new String[] { "M", "F" });
        Map<String, Object> params = filter.doFilter(null, MapUtils.flat(map));
        Assertions.assertEquals(11, params.size());
        Assertions.assertEquals("1", params.get("id-0"));
        Assertions.assertEquals("2", params.get("id-1"));
        Assertions.assertEquals("3", params.get("id-2"));
        Assertions.assertEquals("il", params.get("id-op"));
        Assertions.assertEquals("Jack", params.get("name"));
        Assertions.assertArrayEquals(new Object[] { "20", "30" }, (Object[]) params.get("age-bt"));
        Assertions.assertEquals("M", params.get("sex-0"));
        Assertions.assertEquals("F", params.get("sex-1"));
        System.out.println("\ttest_01 ok!");
    }

    @Test
    public void test_02() {
        System.out.println(new String[]{""} instanceof Object[]);
        System.out.println(new Integer[]{1} instanceof Object[]);
        Object value = new int[]{2};
        System.out.println(value instanceof Object[]);
    }

}
