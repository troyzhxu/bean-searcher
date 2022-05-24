package com.ejlchina.searcher;

import com.ejlchina.searcher.implement.DefaultDbMapping;
import org.junit.Assert;
import org.junit.Test;

public class DefaultDbMappingTestCase {

    public static class UserEntity {

        private long id;
        private String name;
        private String nickName;

    }

    @Test
    public void test_01() throws NoSuchFieldException {
        DefaultDbMapping mapping = new DefaultDbMapping();
        Assert.assertEquals("user_entity", mapping.table(UserEntity.class).getTables());
        Assert.assertEquals("id", mapping.column(UserEntity.class, UserEntity.class.getDeclaredField("id")).getFieldSql());
        Assert.assertEquals("name", mapping.column(UserEntity.class, UserEntity.class.getDeclaredField("name")).getFieldSql());
        Assert.assertEquals("nick_name", mapping.column(UserEntity.class, UserEntity.class.getDeclaredField("nickName")).getFieldSql());
        System.out.println("DefaultDbMappingTestCase test_01 passed");
    }

    @Test
    public void test_02() throws NoSuchFieldException {
        DefaultDbMapping mapping = new DefaultDbMapping();
        mapping.setUpperCase(true);
        Assert.assertEquals("USER_ENTITY", mapping.table(UserEntity.class).getTables());
        Assert.assertEquals("ID", mapping.column(UserEntity.class, UserEntity.class.getDeclaredField("id")).getFieldSql());
        Assert.assertEquals("NAME", mapping.column(UserEntity.class, UserEntity.class.getDeclaredField("name")).getFieldSql());
        Assert.assertEquals("NICK_NAME", mapping.column(UserEntity.class, UserEntity.class.getDeclaredField("nickName")).getFieldSql());
        System.out.println("DefaultDbMappingTestCase test_02 passed");
    }

    @Test
    public void test_03() throws NoSuchFieldException {
        DefaultDbMapping mapping = new DefaultDbMapping();
        mapping.setRedundantSuffixes(new String[]{"Entity"});
        Assert.assertEquals("user", mapping.table(UserEntity.class).getTables());
        Assert.assertEquals("id", mapping.column(UserEntity.class, UserEntity.class.getDeclaredField("id")).getFieldSql());
        Assert.assertEquals("name", mapping.column(UserEntity.class, UserEntity.class.getDeclaredField("name")).getFieldSql());
        Assert.assertEquals("nick_name", mapping.column(UserEntity.class, UserEntity.class.getDeclaredField("nickName")).getFieldSql());
        System.out.println("DefaultDbMappingTestCase test_03 passed");
    }

    @Test
    public void test_04() throws NoSuchFieldException {
        DefaultDbMapping mapping = new DefaultDbMapping();
        mapping.setUnderlineCase(false);
        Assert.assertEquals("UserEntity", mapping.table(UserEntity.class).getTables());
        Assert.assertEquals("id", mapping.column(UserEntity.class, UserEntity.class.getDeclaredField("id")).getFieldSql());
        Assert.assertEquals("name", mapping.column(UserEntity.class, UserEntity.class.getDeclaredField("name")).getFieldSql());
        Assert.assertEquals("nickName", mapping.column(UserEntity.class, UserEntity.class.getDeclaredField("nickName")).getFieldSql());
        System.out.println("DefaultDbMappingTestCase test_04 passed");
    }

    @Test
    public void test_05() throws NoSuchFieldException {
        DefaultDbMapping mapping = new DefaultDbMapping();
        mapping.setUpperCase(true);
        mapping.setUnderlineCase(false);
        Assert.assertEquals("USERENTITY", mapping.table(UserEntity.class).getTables());
        Assert.assertEquals("ID", mapping.column(UserEntity.class, UserEntity.class.getDeclaredField("id")).getFieldSql());
        Assert.assertEquals("NAME", mapping.column(UserEntity.class, UserEntity.class.getDeclaredField("name")).getFieldSql());
        Assert.assertEquals("NICKNAME", mapping.column(UserEntity.class, UserEntity.class.getDeclaredField("nickName")).getFieldSql());
        System.out.println("DefaultDbMappingTestCase test_05 passed");
    }

    @Test
    public void test_06() {
        DefaultDbMapping mapping = new DefaultDbMapping();
        mapping.setTablePrefix("t_");
        Assert.assertEquals("t_user_entity", mapping.table(UserEntity.class).getTables());
        System.out.println("DefaultDbMappingTestCase test_06 passed");
    }

    @Test
    public void test_07() {
        DefaultDbMapping mapping = new DefaultDbMapping();
        mapping.setTablePrefix("t_");
        mapping.setRedundantSuffixes(new String[]{"VO", "Entity"});
        Assert.assertEquals("t_user", mapping.table(UserEntity.class).getTables());
        System.out.println("DefaultDbMappingTestCase test_07 passed");
    }

    @Test
    public void test_08() throws NoSuchFieldException {
        DefaultDbMapping mapping = new DefaultDbMapping();
        mapping.setUnderlineCase(false);
        Assert.assertEquals("UserEntity", mapping.table(UserEntity.class).getTables());
        Assert.assertEquals("id", mapping.column(UserEntity.class, UserEntity.class.getDeclaredField("id")).getFieldSql());
        Assert.assertEquals("name", mapping.column(UserEntity.class, UserEntity.class.getDeclaredField("name")).getFieldSql());
        Assert.assertEquals("nickName", mapping.column(UserEntity.class, UserEntity.class.getDeclaredField("nickName")).getFieldSql());
        System.out.println("DefaultDbMappingTestCase test_08 passed");
    }

}
