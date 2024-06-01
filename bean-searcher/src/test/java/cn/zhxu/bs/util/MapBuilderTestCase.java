package cn.zhxu.bs.util;

import cn.zhxu.bs.param.FieldParam;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class MapBuilderTestCase {

    public static class User {
        private int id;
        private String name;
        private String nickName;
        private boolean active;
        private boolean accountLocked;
        public int getId() {
            return id;
        }
        public String getName() {
            return name;
        }
        public String getNickName() {
            return nickName;
        }
        public boolean isActive() {
            return active;
        }
        public boolean isAccountLocked() {
            return accountLocked;
        }
    }

    @Test
    public void test_01() {
        Set<String> keys = MapUtils.builder()
                .field(FieldFnsTestCase.User::getId)
                .field(FieldFnsTestCase.User::getName)
                .field(FieldFnsTestCase.User::getNickName)
                .field(FieldFnsTestCase.User::isActive)
                .field(FieldFnsTestCase.User::isAccountLocked)
                .build()
                .keySet();
        Assert.assertTrue(keys.contains(MapBuilder.FIELD_PARAM + "id"));
        Assert.assertTrue(keys.contains(MapBuilder.FIELD_PARAM + "name"));
        Assert.assertTrue(keys.contains(MapBuilder.FIELD_PARAM + "nickName"));
        Assert.assertTrue(keys.contains(MapBuilder.FIELD_PARAM + "active"));
        Assert.assertTrue(keys.contains(MapBuilder.FIELD_PARAM + "accountLocked"));
    }

    @Test
    public void test_02() {
        Map<String, Object> params = MapUtils.builder()
                .field("id", 1)                             // $
                .field("nickName", "You")                   // $
                .or(b -> {
                    b.field("name", "Jack");                // _0
                    b.field("name", "Tom");                 // _1
                    b.and(c -> {
                        c.field("active", true);            // _2
                        c.or(o -> {
                            o.field("age", 20);             // _3
                            o.field("age", 30);             // _4
                        });
                        c.field("accountLocked", false);    // _2
                    });
                })
                .or(b -> {
                    b.field("sex", "男");                    // _5
                    b.field("sex", "女");                    // _6
                })
                .build();
        assertParam(params, "$", "id", 1);
        assertParam(params, "$", "nickName", "You");
        assertParam(params, "_0", "name", "Jack");
        assertParam(params, "_1", "name", "Tom");
        assertParam(params, "_2", "active", true);
        assertParam(params, "_2", "accountLocked", false);
        assertParam(params, "_3", "age", 20);
        assertParam(params, "_4", "age", 30);
        assertParam(params, "_5", "sex", "男");
        assertParam(params, "_6", "sex", "女");
        String groupExpr = MapUtils.builder(params).getGroupExpr();
        Assert.assertEquals("((_0|_1)|((_2)&(_3|_4)))&(_5|_6)", groupExpr);
        System.out.println(groupExpr);
    }

    private void assertParam(Map<String, Object> params, String group, String field, Object... values) {
        Object result = params.get(group + MapBuilder.FIELD_PARAM + field);
        Assert.assertTrue(result instanceof FieldParam);
        FieldParam fieldParam = (FieldParam) result;
        Assert.assertEquals(fieldParam.getName(), field);
        Assert.assertArrayEquals(fieldParam.getValues(), values);
    }

    @Test
    public void test_03() {
        Set<String> keys = MapUtils.builder()
                .field(FieldFnsTestCase.User::getName, (Collection<Object>) null)
                .build()
                .keySet();
        Assert.assertTrue(keys.contains(MapBuilder.FIELD_PARAM + "name"));
    }

}
