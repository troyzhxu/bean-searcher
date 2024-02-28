package cn.zhxu.bs.convertor;

import cn.zhxu.bs.BeanMeta;
import cn.zhxu.bs.FieldMeta;
import cn.zhxu.bs.implement.DefaultMetaResolver;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ListFieldConvertorTestCase {

    static final ListFieldConvertor convertor = new ListFieldConvertor();
    final BeanMeta<User> beanMeta = new DefaultMetaResolver().resolve(User.class);
    final FieldMeta rolesMeta = beanMeta.requireFieldMeta("roles");
    final FieldMeta roleIdsMeta = beanMeta.requireFieldMeta("roleIds");
    final FieldMeta roleNamesMeta = beanMeta.requireFieldMeta("roleNames");

    static class User {
        private List<Role> roles;
        private List<Integer> roleIds;
        private List<String> roleNames;
    }

    static class Role {
        private final int id;
        private final String name;
        public Role(int id, String name) {
            this.id = id;
            this.name = name;
        }
        public int getId() {
            return id;
        }
        public String getName() {
            return name;
        }
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Role role = (Role) o;
            return id == role.id && Objects.equals(name, role.name);
        }
    }

    // 干扰项测试
    static class UserConvertor implements ListFieldConvertor.Convertor<User> {
        @Override
        public User convert(String value) {
            return null;
        }
    }

    static class RoleConvertor implements ListFieldConvertor.Convertor<Role> {
        @Override
        public Role convert(String value) {
            String[] vs = value.split(":"); // 根据冒号拆分
            int roleId = Integer.parseInt(vs[0]);
            String roleName = vs[1];
            return new Role(roleId, roleName);
        }
    }

    static {
        convertor.setConvertors(Arrays.asList(new UserConvertor(), new RoleConvertor()));
    }

    @Test
    public void test_supports() {
        test_supports(rolesMeta);
        test_supports(roleIdsMeta);
        test_supports(roleNamesMeta);
    }

    public void test_supports(FieldMeta meta) {
        Assert.assertTrue(convertor.supports(meta, String.class));
    }

    @Test
    public void test_convert_role_ids() {
        String value = "1,2,3";
        Object result = convertor.convert(roleIdsMeta, value);
        Assert.assertNotNull(result);
        Assert.assertTrue(result instanceof List);
        List<?> list = (List<?>) result;
        Assert.assertEquals(3, list.size());
        Assert.assertEquals(1, list.get(0));
        Assert.assertEquals(2, list.get(1));
        Assert.assertEquals(3, list.get(2));
    }

    @Test
    public void test_convert_role_names() {
        String value = "管理员,财务,采购";
        Object result = convertor.convert(roleNamesMeta, value);
        Assert.assertNotNull(result);
        Assert.assertTrue(result instanceof List);
        List<?> list = (List<?>) result;
        Assert.assertEquals(3, list.size());
        Assert.assertEquals("管理员", list.get(0));
        Assert.assertEquals("财务", list.get(1));
        Assert.assertEquals("采购", list.get(2));
    }

    @Test
    public void test_convert_roles() {
        String value = "1:管理员,2:财务,3:采购";
        Object result = convertor.convert(rolesMeta, value);
        Assert.assertNotNull(result);
        Assert.assertTrue(result instanceof List);
        List<?> list = (List<?>) result;
        Assert.assertEquals(3, list.size());
        Assert.assertEquals(new Role(1,"管理员"), list.get(0));
        Assert.assertEquals(new Role(2,"财务"), list.get(1));
        Assert.assertEquals(new Role(3,"采购"), list.get(2));
    }

}
