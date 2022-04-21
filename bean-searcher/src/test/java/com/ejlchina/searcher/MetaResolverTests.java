package com.ejlchina.searcher;

import com.ejlchina.searcher.bean.DbField;
import com.ejlchina.searcher.implement.DefaultMetaResolver;
import com.ejlchina.searcher.operator.Equal;
import com.ejlchina.searcher.operator.GreaterEqual;
import com.ejlchina.searcher.operator.StartWith;
import com.ejlchina.searcher.util.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

public class MetaResolverTests {

    final MetaResolver metaResolver = new DefaultMetaResolver();

    public static class User01 {
        private static final int serializeId = 1111;
        private final transient int modCount = 0;
        private long id;
        private String name;
        private boolean enable;
        private Date birthday;
    }

    @Test
    public void test01() {
        BeanMeta<User01> beanMeta = metaResolver.resolve(User01.class);
        Assert.assertFalse(beanMeta.isDistinctOrGroupBy());
        Assert.assertSame(User01.class, beanMeta.getBeanClass());
        Assert.assertTrue(StringUtils.isBlank(beanMeta.getDataSource()));
        Assert.assertFalse(beanMeta.isDistinct());
        Assert.assertTrue(beanMeta.isSortable());
        Assert.assertEquals(4, beanMeta.getFieldCount());

        FieldMeta idMeta = beanMeta.requireFieldMeta("id");
        Assert.assertSame(beanMeta, idMeta.getBeanMeta());
        Assert.assertEquals("id", idMeta.getName());
        Assert.assertSame(long.class, idMeta.getType());
        Assert.assertEquals("id", idMeta.getFieldSql().getSql());
        Assert.assertTrue(idMeta.getFieldSql().getParas().isEmpty());
        Assert.assertEquals(0, idMeta.getOnlyOn().length);
        Assert.assertTrue(idMeta.isConditional());

        FieldMeta nameMeta = beanMeta.requireFieldMeta("name");
        Assert.assertSame(beanMeta, nameMeta.getBeanMeta());
        Assert.assertEquals("name", nameMeta.getName());
        Assert.assertSame(String.class, nameMeta.getType());
        Assert.assertEquals("name", nameMeta.getFieldSql().getSql());
        Assert.assertTrue(nameMeta.getFieldSql().getParas().isEmpty());
        Assert.assertEquals(0, nameMeta.getOnlyOn().length);
        Assert.assertTrue(nameMeta.isConditional());

        FieldMeta enableMeta = beanMeta.requireFieldMeta("enable");
        Assert.assertSame(beanMeta, enableMeta.getBeanMeta());
        Assert.assertEquals("enable", enableMeta.getName());
        Assert.assertSame(boolean.class, enableMeta.getType());
        Assert.assertEquals("enable", enableMeta.getFieldSql().getSql());
        Assert.assertTrue(enableMeta.getFieldSql().getParas().isEmpty());
        Assert.assertEquals(0, enableMeta.getOnlyOn().length);
        Assert.assertTrue(enableMeta.isConditional());

        FieldMeta birthdayMeta = beanMeta.requireFieldMeta("birthday");
        Assert.assertSame(beanMeta, birthdayMeta.getBeanMeta());
        Assert.assertEquals("birthday", birthdayMeta.getName());
        Assert.assertSame(Date.class, birthdayMeta.getType());
        Assert.assertEquals("birthday", birthdayMeta.getFieldSql().getSql());
        Assert.assertTrue(birthdayMeta.getFieldSql().getParas().isEmpty());
        Assert.assertEquals(0, birthdayMeta.getOnlyOn().length);
        Assert.assertTrue(birthdayMeta.isConditional());

        assertAlias(beanMeta.getFieldMetas());
    }

    private void assertAlias(Collection<FieldMeta> metas) {
        HashSet<String> set = new HashSet<>(metas.size());
        for (FieldMeta meta: metas) {
            String alias = meta.getDbAlias();
            System.out.println(meta.getName() + ": " + alias);
            char fc = alias.charAt(0);
            // Oracle 数据库的别名不能以下划线开头
            Assert.assertTrue("别名必须以字母开头", fc >= 'A' && fc <= 'z' && (fc <= 'Z' || fc >= 'a'));
            for (char c : alias.toCharArray()) {
                Assert.assertTrue("别名必须由字母数字下划线组成",c >= 'A' && c <= 'z' && (c <= 'Z' || c >= 'a') || (c >= '0' && c <= '9') || c == '_');
            }
            Assert.assertFalse("别名不能重复", set.contains(alias));
            set.add(alias);
        }
    }

    public static class User02 {
        @DbField(alias = "c_2")
        private long id;
        private String name;
        @DbField(alias = "c_1")
        private boolean enable;
        @DbField(alias = "xxx")
        private Date birthday;
    }


    @Test
    public void test02() {
        BeanMeta<User02> beanMeta = metaResolver.resolve(User02.class);
        Assert.assertEquals("c_2", beanMeta.requireFieldMeta("id").getDbAlias());
        Assert.assertEquals("c_1", beanMeta.requireFieldMeta("enable").getDbAlias());
        Assert.assertEquals("xxx", beanMeta.requireFieldMeta("birthday").getDbAlias());
        assertAlias(beanMeta.getFieldMetas());
    }

    public static class User03 {
        @DbField(alias = "bb")
        private long id;
        private String name;
        @DbField(alias = "bb")
        private boolean enable;
        private Date birthday;
    }

    @Test
    public void test03() {
        boolean exception = false;
        try {
            metaResolver.resolve(User03.class);
        } catch (SearchException e) {
            String message = e.getMessage();
            System.out.println(message);
            Assert.assertTrue(message.startsWith("The alias [bb] of [" + User03.class.getName()));
            Assert.assertTrue(message.endsWith("] is already exists on other fields."));
            Assert.assertTrue(message.contains("User03.id]") || message.contains("User03.enable]"));
            exception = true;
        }
        Assert.assertTrue(exception);
    }

    public static class User04 {
        @DbField(onlyOn = GreaterEqual.class)
        private long id;
        @DbField(onlyOn = {StartWith.class, Equal.class})
        private String name;
        @DbField(conditional = false)
        private boolean enable;
        @DbField(value = "date_birth", conditional = true)
        private Date birthday;
    }

    @Test
    public void test04() {
        BeanMeta<User04> beanMeta = metaResolver.resolve(User04.class);
        Assert.assertArrayEquals(new Object[] {GreaterEqual.class}, beanMeta.requireFieldMeta("id").getOnlyOn());
        Assert.assertArrayEquals(new Object[] {StartWith.class, Equal.class}, beanMeta.requireFieldMeta("name").getOnlyOn());
        Assert.assertArrayEquals(new Object[] {}, beanMeta.requireFieldMeta("enable").getOnlyOn());
        Assert.assertArrayEquals(new Object[] {}, beanMeta.requireFieldMeta("birthday").getOnlyOn());
        Assert.assertTrue(beanMeta.requireFieldMeta("id").isConditional());
        Assert.assertTrue(beanMeta.requireFieldMeta("name").isConditional());
        Assert.assertFalse(beanMeta.requireFieldMeta("enable").isConditional());
        Assert.assertTrue(beanMeta.requireFieldMeta("birthday").isConditional());
        assertAlias(beanMeta.getFieldMetas());
    }

}
