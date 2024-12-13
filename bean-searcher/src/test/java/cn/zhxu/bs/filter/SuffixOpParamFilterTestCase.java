package cn.zhxu.bs.filter;

import cn.zhxu.bs.util.MapUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class SuffixOpParamFilterTestCase {

    @Test
    public void test_01() {
        SuffixOpParamFilter filter = new SuffixOpParamFilter();
        Map<String, Object> params = MapUtils.builder()
                .put("id-il", "[1,2,3]")
                .put("age-gt", "30")
                .build();
        params = filter.doFilter(null, params);
        Assertions.assertEquals("[1,2,3]", params.get("id"));
        Assertions.assertEquals("il", params.get("id-op"));
        Assertions.assertEquals("30", params.get("age"));
        Assertions.assertEquals("gt", params.get("age-op"));
        System.out.println("\ttest_01 ok!");
    }

    @Test
    public void test_02() {
        SuffixOpParamFilter filter = new SuffixOpParamFilter("_", "op", "ic");
        Map<String, Object> params = MapUtils.builder()
                .put("id_il", "[1,2,3]")
                .put("age_gt", "30")
                .build();
        params = filter.doFilter(null, params);
        Assertions.assertEquals("[1,2,3]", params.get("id"));
        Assertions.assertEquals("il", params.get("id_op"));
        Assertions.assertEquals("30", params.get("age"));
        Assertions.assertEquals("gt", params.get("age_op"));
        System.out.println("\ttest_02 ok!");
    }

    @Test
    public void test_03() {
        Map<String, Object> params = MapUtils.builder()
                .put("id-il", "[1,2,3]")
                .put("age-gt", "30")
                .build();
        params = new SuffixOpParamFilter().doFilter(null, params);
        params = new JsonArrayParamFilter().doFilter(null, params);
        Assertions.assertEquals("[1,2,3]", params.get("id"));
        Assertions.assertEquals("1", params.get("id-0"));
        Assertions.assertEquals("2", params.get("id-1"));
        Assertions.assertEquals("3", params.get("id-2"));
        Assertions.assertEquals("il", params.get("id-op"));
        Assertions.assertEquals("30", params.get("age"));
        Assertions.assertEquals("gt", params.get("age-op"));
        System.out.println("\ttest_03 ok!");
    }

    @Test
    public void test_04() {
        Map<String, String[]> map = new HashMap<>();
        map.put("id-il", new String[] { "1", "2", "3" });
        map.put("age-gt", new String[] { "30" });

        Map<String, Object> params = MapUtils.flatBuilder(map)
                .put("name-eq", "Jack")
                .build();
        params = new SuffixOpParamFilter().doFilter(null, params);
        Assertions.assertEquals("1", params.get("id-0"));
        Assertions.assertEquals("2", params.get("id-1"));
        Assertions.assertEquals("3", params.get("id-2"));
        Assertions.assertEquals("il", params.get("id-op"));
        Assertions.assertEquals("30", params.get("age"));
        Assertions.assertEquals("gt", params.get("age-op"));
        Assertions.assertEquals("Jack", params.get("name"));
        Assertions.assertEquals("eq", params.get("name-op"));
        System.out.println("\ttest_04 ok!");
    }

    @Test
    public void test_05() {
        SuffixOpParamFilter filter = new SuffixOpParamFilter();
        Map<String, Object> params = MapUtils.builder()
                .put("id-il-ic", "[1,2,3]")
                .put("age-gt-ic", "30")
                .build();
        params = filter.doFilter(null, params);
        Assertions.assertEquals("[1,2,3]", params.get("id"));
        Assertions.assertEquals("il", params.get("id-op"));
        Assertions.assertEquals(true, params.get("id-ic"));
        Assertions.assertEquals("30", params.get("age"));
        Assertions.assertEquals("gt", params.get("age-op"));
        Assertions.assertEquals(true, params.get("age-ic"));
        System.out.println("\ttest_05 ok!");
    }

    @Test
    public void test_06() {
        SuffixOpParamFilter filter = new SuffixOpParamFilter("_", "op", "IC");
        Map<String, Object> params = MapUtils.builder()
                .put("id_il_IC", "[1,2,3]")
                .put("age_gt_ic", "30")
                .build();
        params = filter.doFilter(null, params);
        Assertions.assertEquals("[1,2,3]", params.get("id"));
        Assertions.assertEquals("il", params.get("id_op"));
        Assertions.assertEquals(true, params.get("id_IC"));
        Assertions.assertEquals("30", params.get("age"));
        Assertions.assertEquals("gt", params.get("age_op"));
        Assertions.assertNull(params.get("age_IC"));
        System.out.println("\ttest_06 ok!");
    }

    @Test
    public void test_07() {
        Map<String, Object> params = MapUtils.builder()
                .put("id-il-ic", "[1,2,3]")
                .put("age-gt-ic", "30")
                .build();
        params = new SuffixOpParamFilter().doFilter(null, params);
        params = new JsonArrayParamFilter().doFilter(null, params);
        Assertions.assertEquals("[1,2,3]", params.get("id"));
        Assertions.assertEquals("1", params.get("id-0"));
        Assertions.assertEquals("2", params.get("id-1"));
        Assertions.assertEquals("3", params.get("id-2"));
        Assertions.assertEquals("il", params.get("id-op"));
        Assertions.assertEquals(true, params.get("id-ic"));
        Assertions.assertEquals("30", params.get("age"));
        Assertions.assertEquals("gt", params.get("age-op"));
        Assertions.assertEquals(true, params.get("age-ic"));
        System.out.println("\ttest_07 ok!");
    }

    @Test
    public void test_08() {
        Map<String, String[]> map = new HashMap<>();
        map.put("id-il-ic", new String[] { "1", "2", "3" });
        map.put("age-gt-ic", new String[] { "30" });

        Map<String, Object> params = MapUtils.flatBuilder(map)
                .put("name-eq-ic", "Jack")
                .build();
        params = new SuffixOpParamFilter().doFilter(null, params);
        Assertions.assertEquals("1", params.get("id-0"));
        Assertions.assertEquals("2", params.get("id-1"));
        Assertions.assertEquals("3", params.get("id-2"));
        Assertions.assertEquals("il", params.get("id-op"));
        Assertions.assertEquals(true, params.get("id-ic"));
        Assertions.assertEquals("30", params.get("age"));
        Assertions.assertEquals("gt", params.get("age-op"));
        Assertions.assertEquals(true, params.get("age-ic"));
        Assertions.assertEquals("Jack", params.get("name"));
        Assertions.assertEquals("eq", params.get("name-op"));
        Assertions.assertEquals(true, params.get("name-ic"));
        System.out.println("\ttest_08 ok!");
    }
}
