package cn.zhxu.bs;

import cn.zhxu.bs.implement.DefaultDbMapping;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DefaultDbMappingTestCase {

    public static class UserEntity {

        private long id;
        private String name;
        private String nickName;

    }

    @Test
    public void test_01() throws NoSuchFieldException {
        DefaultDbMapping mapping = new DefaultDbMapping();
        Assertions.assertEquals("user_entity", mapping.table(UserEntity.class).getTables());
        Assertions.assertEquals("id", mapping.column(UserEntity.class, UserEntity.class.getDeclaredField("id")).getFieldSql());
        Assertions.assertEquals("name", mapping.column(UserEntity.class, UserEntity.class.getDeclaredField("name")).getFieldSql());
        Assertions.assertEquals("nick_name", mapping.column(UserEntity.class, UserEntity.class.getDeclaredField("nickName")).getFieldSql());
        System.out.println("\ttest_01 ok!");
    }

    @Test
    public void test_02() throws NoSuchFieldException {
        DefaultDbMapping mapping = new DefaultDbMapping();
        mapping.setUpperCase(true);
        Assertions.assertEquals("USER_ENTITY", mapping.table(UserEntity.class).getTables());
        Assertions.assertEquals("ID", mapping.column(UserEntity.class, UserEntity.class.getDeclaredField("id")).getFieldSql());
        Assertions.assertEquals("NAME", mapping.column(UserEntity.class, UserEntity.class.getDeclaredField("name")).getFieldSql());
        Assertions.assertEquals("NICK_NAME", mapping.column(UserEntity.class, UserEntity.class.getDeclaredField("nickName")).getFieldSql());
        System.out.println("\ttest_02 ok!");
    }

    @Test
    public void test_03() throws NoSuchFieldException {
        DefaultDbMapping mapping = new DefaultDbMapping();
        mapping.setRedundantSuffixes(new String[]{"Entity"});
        Assertions.assertEquals("user", mapping.table(UserEntity.class).getTables());
        Assertions.assertEquals("id", mapping.column(UserEntity.class, UserEntity.class.getDeclaredField("id")).getFieldSql());
        Assertions.assertEquals("name", mapping.column(UserEntity.class, UserEntity.class.getDeclaredField("name")).getFieldSql());
        Assertions.assertEquals("nick_name", mapping.column(UserEntity.class, UserEntity.class.getDeclaredField("nickName")).getFieldSql());
        System.out.println("\ttest_03 ok!");
    }

    @Test
    public void test_04() throws NoSuchFieldException {
        DefaultDbMapping mapping = new DefaultDbMapping();
        mapping.setUnderlineCase(false);
        Assertions.assertEquals("UserEntity", mapping.table(UserEntity.class).getTables());
        Assertions.assertEquals("id", mapping.column(UserEntity.class, UserEntity.class.getDeclaredField("id")).getFieldSql());
        Assertions.assertEquals("name", mapping.column(UserEntity.class, UserEntity.class.getDeclaredField("name")).getFieldSql());
        Assertions.assertEquals("nickName", mapping.column(UserEntity.class, UserEntity.class.getDeclaredField("nickName")).getFieldSql());
        System.out.println("\ttest_04 ok!");
    }

    @Test
    public void test_05() throws NoSuchFieldException {
        DefaultDbMapping mapping = new DefaultDbMapping();
        mapping.setUpperCase(true);
        mapping.setUnderlineCase(false);
        Assertions.assertEquals("USERENTITY", mapping.table(UserEntity.class).getTables());
        Assertions.assertEquals("ID", mapping.column(UserEntity.class, UserEntity.class.getDeclaredField("id")).getFieldSql());
        Assertions.assertEquals("NAME", mapping.column(UserEntity.class, UserEntity.class.getDeclaredField("name")).getFieldSql());
        Assertions.assertEquals("NICKNAME", mapping.column(UserEntity.class, UserEntity.class.getDeclaredField("nickName")).getFieldSql());
        System.out.println("\ttest_05 ok!");
    }

    @Test
    public void test_06() {
        DefaultDbMapping mapping = new DefaultDbMapping();
        mapping.setTablePrefix("t_");
        Assertions.assertEquals("t_user_entity", mapping.table(UserEntity.class).getTables());
        System.out.println("\ttest_06 ok!");
    }

    @Test
    public void test_07() {
        DefaultDbMapping mapping = new DefaultDbMapping();
        mapping.setTablePrefix("t_");
        mapping.setRedundantSuffixes(new String[]{"VO", "Entity"});
        Assertions.assertEquals("t_user", mapping.table(UserEntity.class).getTables());
        System.out.println("\ttest_07 ok!");
    }

    @Test
    public void test_08() throws NoSuchFieldException {
        DefaultDbMapping mapping = new DefaultDbMapping();
        mapping.setUnderlineCase(false);
        Assertions.assertEquals("UserEntity", mapping.table(UserEntity.class).getTables());
        Assertions.assertEquals("id", mapping.column(UserEntity.class, UserEntity.class.getDeclaredField("id")).getFieldSql());
        Assertions.assertEquals("name", mapping.column(UserEntity.class, UserEntity.class.getDeclaredField("name")).getFieldSql());
        Assertions.assertEquals("nickName", mapping.column(UserEntity.class, UserEntity.class.getDeclaredField("nickName")).getFieldSql());
        System.out.println("\ttest_08 ok!");
    }

}
