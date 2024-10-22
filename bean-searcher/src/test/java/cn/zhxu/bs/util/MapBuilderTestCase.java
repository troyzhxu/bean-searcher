package cn.zhxu.bs.util;

import cn.zhxu.bs.operator.InList;
import cn.zhxu.bs.operator.StartWith;
import cn.zhxu.bs.param.FieldParam;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;

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
                .field(User::getId)
                .field(User::getName)
                .field(User::getNickName)
                .field(User::isActive)
                .field(User::isAccountLocked)
                .build()
                .keySet();
        Assertions.assertTrue(keys.contains(MapBuilder.FIELD_PARAM + "id"));
        Assertions.assertTrue(keys.contains(MapBuilder.FIELD_PARAM + "name"));
        Assertions.assertTrue(keys.contains(MapBuilder.FIELD_PARAM + "nickName"));
        Assertions.assertTrue(keys.contains(MapBuilder.FIELD_PARAM + "active"));
        Assertions.assertTrue(keys.contains(MapBuilder.FIELD_PARAM + "accountLocked"));
        System.out.println("\ttest_01 ok!");
    }

    @Test
    public void test_02() {
        Map<String, Object> params = MapUtils.builder()
                .field("id", 1)                             // $
                .field("nickName", "You")                   // $
                .or(b -> b
                    .field("name", "Jack")                // _0
                    .field("name", "Tom")                 // _1
                    .and(c -> c
                        .field("active", true)            // _2
                        .or(o -> {
                            o.field("age", 20);             // _3
                            o.field("age", 30);             // _4
                        })
                        .field("accountLocked", false)    // _2
                    )
                )
                .or(b -> b
                    .field("sex", "男")                    // _5
                    .field("sex", "女")                    // _6
                )
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
        Assertions.assertEquals("((_0|_1)|((_2)&(_3|_4)))&(_5|_6)", groupExpr);
        System.out.println("\ttest_02 ok!");
    }

    private void assertParam(Map<String, Object> params, String group, String field, Object... values) {
        Object result = params.get(group + MapBuilder.FIELD_PARAM + field);
        Assertions.assertInstanceOf(FieldParam.class, result);
        FieldParam fieldParam = (FieldParam) result;
        Assertions.assertEquals(fieldParam.getName(), field);
        Assertions.assertArrayEquals(fieldParam.getValues(), values);
    }

    @Test
    public void test_03() {
        Set<String> keys = MapUtils.builder()
                .field(User::getName, (Collection<Object>) null)
                .build()
                .keySet();
        Assertions.assertTrue(keys.contains(MapBuilder.FIELD_PARAM + "name"));
        System.out.println("\ttest_03 ok!");
    }

    @Test
    public void test_ternary_string() {
        testTernaryParam(true, "Jack", "Tom");
        testTernaryParam(false, "Jack", "Tom");
        testTernaryParam(true, "Tom", "Jack");
        testTernaryParam(false, "Tom", "Jack");
    }

    private void testTernaryParam(boolean isList, String value1, String value2) {
        String name = FieldFns.name(User::getName);
        Map<String, Object> params1 = MapUtils.builder()
                .field(name, isList ? Arrays.asList(value1, value2) : value1)
                .build();
        assertListParams(isList, value1, value2, params1, name);
        Map<String, Object> params2 = MapUtils.builder()
                .field(name, isList ? new String[] { value1, value2 } : value1)
                .build();
        assertListParams(isList, value1, value2, params2, name);
    }

    private static void assertListParams(boolean isList, String value1, String value2, Map<String, Object> params, String name) {
        FieldParam param = (FieldParam) params.get(MapBuilder.FIELD_PARAM + name);
        Assertions.assertNotNull(param);
        Assertions.assertEquals(name, param.getName());
        Object[] values = param.getValues();
        if (isList) {
            Assertions.assertEquals(2, values.length);
            Assertions.assertEquals(value1, values[0]);
            Assertions.assertEquals(value2, values[1]);
        } else {
            Assertions.assertEquals(1, values.length);
            Assertions.assertEquals(value1, values[0]);
        }
    }

    @Test
    public void test_ternary_int() {
        testTernaryParamInt(true, 1, 2);
        testTernaryParamInt(false, 1, 2);
        testTernaryParamInt(true, 200, 100);
        testTernaryParamInt(false, 200, 100);
    }

    private void testTernaryParamInt(boolean isList, int value1, int value2) {
        String name = FieldFns.name(User::getId);
        Map<String, Object> params = MapUtils.builder()
                .field(name, isList ? new int[] { value1, value2 } : value1)
                .build();
        FieldParam param = (FieldParam) params.get(MapBuilder.FIELD_PARAM + name);
        Assertions.assertNotNull(param);
        Assertions.assertEquals(name, param.getName());
        Object[] values = param.getValues();
        if (isList) {
            Assertions.assertEquals(2, values.length);
            Assertions.assertEquals(value1, values[0]);
            Assertions.assertEquals(value2, values[1]);
        } else {
            Assertions.assertEquals(1, values.length);
            Assertions.assertEquals(value1, values[0]);
        }
    }

    @Test
    public void test_rpc_page_01() {
        Map<String, Object> params = MapUtils.builder()
                .limit(300, 50)
                .buildForRpc();
        Assertions.assertEquals(2, params.size());
        Assertions.assertEquals(300L, params.get("offset"));
        Assertions.assertEquals(50, params.get("size"));
        System.out.println("\ttest_rpc_page_01 ok!");
    }

    @Test
    public void test_rpc_page_02() {
        Map<String, Object> params = MapUtils.builder()
                .page(10, 30)
                .buildForRpc();
        Assertions.assertEquals(2, params.size());
        Assertions.assertEquals(10L, params.get("page"));
        Assertions.assertEquals(30, params.get("size"));
        System.out.println("\ttest_rpc_page_02 ok!");
    }

    @Test
    public void test_rpc_orderBy_01() {
        Map<String, Object> params = MapUtils.builder()
                .orderBy(User::getId).asc()
                .orderBy(User::getName).desc()
                .buildForRpc();
        Assertions.assertEquals(1, params.size());
        Assertions.assertEquals("id:asc,name:desc", params.get("orderBy"));
        System.out.println("\ttest_rpc_orderBy_01 ok!");
    }

    @Test
    public void test_rpc_orderBy_02() {
        Map<String, Object> params = MapUtils.builder()
                .orderBy(User::getId).desc()
                .orderBy(User::getName).asc()
                .buildForRpc();
        Assertions.assertEquals(1, params.size());
        Assertions.assertEquals("id:desc,name:asc", params.get("orderBy"));
        System.out.println("\ttest_rpc_orderBy_02 ok!");
    }

    @Test
    public void test_rpc_orderBy_03() {
        Map<String, Object> params = MapUtils.builder()
                .orderBy(User::getId).desc()
                .buildForRpc();
        Assertions.assertEquals(1, params.size());
        Assertions.assertEquals("id:desc", params.get("orderBy"));
        System.out.println("\ttest_rpc_orderBy_03 ok!");
    }

    @Test
    public void test_rpc_orderBy_04() {
        Map<String, Object> params = MapUtils.builder()
                .orderBy(User::getId, "desc")
                .buildForRpc();
        Assertions.assertEquals(1, params.size());
        Assertions.assertEquals("id:desc", params.get("orderBy"));
        System.out.println("\ttest_rpc_orderBy_04 ok!");
    }

    @Test
    public void test_rpc_onlySelect_01() {
        Map<String, Object> params = MapUtils.builder()
                .onlySelect(User::getId)
                .onlySelect(User::getName)
                .buildForRpc();
        Assertions.assertEquals(1, params.size());
        Assertions.assertEquals("id,name", params.get("onlySelect"));
        System.out.println("\ttest_rpc_onlySelect_01 ok!");
    }

    @Test
    public void test_rpc_onlySelect_02() {
        Map<String, Object> params = MapUtils.builder()
                .onlySelect(User::getId, User::getName)
                .buildForRpc();
        Assertions.assertEquals(1, params.size());
        Assertions.assertEquals("id,name", params.get("onlySelect"));
        System.out.println("\ttest_rpc_onlySelect_02 ok!");
    }

    @Test
    public void test_rpc_onlySelect_03() {
        Map<String, Object> params = MapUtils.builder()
                .onlySelect("id")
                .onlySelect("name")
                .buildForRpc();
        Assertions.assertEquals(1, params.size());
        Assertions.assertEquals("id,name", params.get("onlySelect"));
        System.out.println("\ttest_rpc_onlySelect_03 ok!");
    }

    @Test
    public void test_rpc_onlySelect_04() {
        Map<String, Object> params = MapUtils.builder()
                .onlySelect("id,name")
                .buildForRpc();
        Assertions.assertEquals(1, params.size());
        Assertions.assertEquals("id,name", params.get("onlySelect"));
        System.out.println("\ttest_rpc_onlySelect_04 ok!");
    }

    @Test
    public void test_rpc_selectExclude_01() {
        Map<String, Object> params = MapUtils.builder()
                .selectExclude(User::getId)
                .selectExclude(User::getName)
                .buildForRpc();
        Assertions.assertEquals(1, params.size());
        Assertions.assertEquals("id,name", params.get("selectExclude"));
        System.out.println("\ttest_rpc_selectExclude_01 ok!");
    }

    @Test
    public void test_rpc_selectExclude_02() {
        Map<String, Object> params = MapUtils.builder()
                .selectExclude(User::getId, User::getName)
                .buildForRpc();
        Assertions.assertEquals(1, params.size());
        Assertions.assertEquals("id,name", params.get("selectExclude"));
        System.out.println("\ttest_rpc_selectExclude_02 ok!");
    }

    @Test
    public void test_rpc_selectExclude_03() {
        Map<String, Object> params = MapUtils.builder()
                .selectExclude("id")
                .selectExclude("name")
                .buildForRpc();
        Assertions.assertEquals(1, params.size());
        Assertions.assertEquals("id,name", params.get("selectExclude"));
        System.out.println("\ttest_rpc_selectExclude_03 ok!");
    }

    @Test
    public void test_rpc_selectExclude_04() {
        Map<String, Object> params = MapUtils.builder()
                .selectExclude("id,name")
                .buildForRpc();
        Assertions.assertEquals(1, params.size());
        Assertions.assertEquals("id,name", params.get("selectExclude"));
        System.out.println("\ttest_rpc_selectExclude_04 ok!");
    }

    @Test
    public void test_rpc_field_01() {
        Map<String, Object> params = MapUtils.builder()
                .field(User::getName, "Jack").op(StartWith.class).ic()
                .field(User::getId, 1, 2, 3).op(InList.class)
                .buildForRpc();
        Assertions.assertEquals(7, params.size());
        Assertions.assertEquals("Jack", params.get("name-0"));
        Assertions.assertEquals("StartWith", params.get("name-op"));
        Assertions.assertEquals(true, params.get("name-ic"));
        Assertions.assertEquals(1, params.get("id-0"));
        Assertions.assertEquals(2, params.get("id-1"));
        Assertions.assertEquals(3, params.get("id-2"));
        Assertions.assertEquals("InList", params.get("id-op"));
        System.out.println("\ttest_rpc_field_01 ok!");
    }

    @Test
    public void test_rpc_field_02() {
        Map<String, Object> params = MapUtils.builder()
                .group("A")
                .field(User::getName, "Jack").op(StartWith.class).ic()
                .group("B")
                .field(User::getId, 1, 2, 3).op(InList.class)
                .groupExpr("A|B")
                .buildForRpc();
        Assertions.assertEquals(8, params.size());
        Assertions.assertEquals("Jack", params.get("A.name-0"));
        Assertions.assertEquals("StartWith", params.get("A.name-op"));
        Assertions.assertEquals(true, params.get("A.name-ic"));
        Assertions.assertEquals(1, params.get("B.id-0"));
        Assertions.assertEquals(2, params.get("B.id-1"));
        Assertions.assertEquals(3, params.get("B.id-2"));
        Assertions.assertEquals("InList", params.get("B.id-op"));
        Assertions.assertEquals("A|B", params.get("gexpr"));
        System.out.println("\ttest_rpc_field_02 ok!");
    }

    @Test
    public void test_rpc_field_03() {
        Map<String, Object> params = MapUtils.builder()
                .or(o -> {
                    o.field(User::getName, "Jack").op(StartWith.class).ic();
                    o.field(User::getId, 1, 2, 3).op(InList.class);
                })
                .buildForRpc();
        Assertions.assertEquals(8, params.size());
        Assertions.assertEquals("Jack", params.get("_0.name-0"));
        Assertions.assertEquals("StartWith", params.get("_0.name-op"));
        Assertions.assertEquals(true, params.get("_0.name-ic"));
        Assertions.assertEquals(1, params.get("_1.id-0"));
        Assertions.assertEquals(2, params.get("_1.id-1"));
        Assertions.assertEquals(3, params.get("_1.id-2"));
        Assertions.assertEquals("InList", params.get("_1.id-op"));
        Assertions.assertEquals("_0|_1", params.get("gexpr"));
        System.out.println("\ttest_rpc_field_03 ok!");
    }

}
