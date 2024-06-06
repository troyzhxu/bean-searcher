package cn.zhxu.bs.filter;

import cn.zhxu.bs.util.MapUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Map;

public class CollectionParamFilterTests {

    CollectionParamFilter filter = new CollectionParamFilter();

    @Test
    public void test_01() {
        Map<String, Object> params = MapUtils.builder()
                .put("idList", Arrays.asList(1, 2, 3))
                .build();
        params = filter.doFilter(null, params);
        Assert.assertEquals(1, params.size());
        Assert.assertEquals("1,2,3", params.get("idList"));
    }

    @Test
    public void test_02() {
        Map<String, Object> params = MapUtils.builder()
                .put("age", 30)
                .put("idList", Arrays.asList(1, 2, 3))
                .put("name", "Jack")
                .build();
        params = filter.doFilter(null, params);
        Assert.assertEquals(3, params.size());
        Assert.assertEquals(30, params.get("age"));
        Assert.assertEquals("1,2,3", params.get("idList"));
        Assert.assertEquals("Jack", params.get("name"));
    }

}
