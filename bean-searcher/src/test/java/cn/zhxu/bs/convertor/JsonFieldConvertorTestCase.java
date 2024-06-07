package cn.zhxu.bs.convertor;

import cn.zhxu.bs.FieldMeta;
import cn.zhxu.bs.bean.DbField;
import cn.zhxu.bs.bean.DbType;
import cn.zhxu.bs.implement.DefaultMetaResolver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class JsonFieldConvertorTestCase {

    final JsonFieldConvertor convertor = new JsonFieldConvertor();

    final FieldMeta roleMeta = new DefaultMetaResolver().resolve(User.class).requireFieldMeta("role");
    final FieldMeta rolesMeta = new DefaultMetaResolver().resolve(User.class).requireFieldMeta("roles");

    static class User {

        @DbField(type = DbType.JSON)
        private List<Role> roles;

        @DbField(type = DbType.JSON)
        private Role role;

    }

    static class Role {
        private int id;
        private String name;

        public void setId(int id) {
            this.id = id;
        }
        public int getId() {
            return id;
        }
        public void setName(String name) {
            this.name = name;
        }
        public String getName() {
            return name;
        }
    }

    @Test
    public void test_supports() {
        test_supports(roleMeta);
        test_supports(rolesMeta);
    }

    public void test_supports(FieldMeta meta) {
        Assertions.assertTrue(convertor.supports(meta, String.class));
        Assertions.assertEquals(meta.getDbType(), DbType.JSON);
    }

    @Test
    public void test_convert_role() {
        String value = "{\"id\":1,\"name\":\"Jack\"}]";
        Object result = convertor.convert(roleMeta, value);
        Assertions.assertNotNull(result);
        assertRole(result, 1, "Jack");
    }

    @Test
    public void test_convert_roles() {
        String value = "[{\"id\":1,\"name\":\"Jack\"},{\"id\":2,\"name\":\"Tom\"}]";
        Object results = convertor.convert(rolesMeta, value);
        Assertions.assertNotNull(results);
        Assertions.assertTrue(List.class.isAssignableFrom(results.getClass()));
        List<?> list = (List<?>) results;
        Assertions.assertEquals(2, list.size());
        assertRole(list.get(0), 1, "Jack");
        assertRole(list.get(1), 2, "Tom");
    }

    private void assertRole(Object data, int id, String name) {
        Assertions.assertNotNull(data);
        Assertions.assertTrue(Role.class.isAssignableFrom(data.getClass()));
        Role role = (Role) data;
        Assertions.assertEquals(id, role.getId());
        Assertions.assertEquals(name, role.getName());
    }

}
