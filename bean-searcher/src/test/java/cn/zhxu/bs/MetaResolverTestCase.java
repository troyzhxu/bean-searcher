package cn.zhxu.bs;

import cn.zhxu.bs.bean.*;
import cn.zhxu.bs.implement.DefaultDbMapping;
import cn.zhxu.bs.implement.DefaultMetaResolver;
import cn.zhxu.bs.operator.Equal;
import cn.zhxu.bs.operator.GreaterEqual;
import cn.zhxu.bs.operator.StartWith;
import cn.zhxu.bs.util.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

public class MetaResolverTestCase {

    static final MetaResolver metaResolver = new DefaultMetaResolver();

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
        Assertions.assertEquals("user01", beanMeta.getTableSnippet().getSql());
        Assertions.assertTrue(beanMeta.getTableSnippet().getParas().isEmpty());

        Assertions.assertTrue(StringUtils.isBlank(beanMeta.getWhere()));
        Assertions.assertTrue(beanMeta.getWhereSqlParas().isEmpty());

        Assertions.assertTrue(StringUtils.isBlank(beanMeta.getGroupBy()));
        Assertions.assertTrue(beanMeta.getGroupBySqlParas().isEmpty());

        Assertions.assertTrue(StringUtils.isBlank(beanMeta.getOrderBySnippet().getSql()));
        Assertions.assertTrue(beanMeta.getOrderBySnippet().getParas().isEmpty());

        Assertions.assertSame(User01.class, beanMeta.getBeanClass());
        Assertions.assertTrue(StringUtils.isBlank(beanMeta.getDataSource()));

        Assertions.assertFalse(beanMeta.isDistinct());
        Assertions.assertTrue(beanMeta.isSortable());
        Assertions.assertFalse(beanMeta.isDistinctOrGroupBy());

        Assertions.assertEquals(4, beanMeta.getFieldCount());
        Assertions.assertEquals(4, beanMeta.getFieldSet().size());
        Assertions.assertEquals(4, beanMeta.getFieldMetas().size());

        FieldMeta idMeta = beanMeta.requireFieldMeta("id");
        Assertions.assertSame(beanMeta, idMeta.getBeanMeta());
        Assertions.assertEquals("id", idMeta.getName());
        Assertions.assertSame(long.class, idMeta.getType());
        Assertions.assertEquals("id", idMeta.getFieldSql().getSql());
        Assertions.assertTrue(idMeta.getFieldSql().getParas().isEmpty());
        Assertions.assertEquals(0, idMeta.getOnlyOn().length);
        Assertions.assertTrue(idMeta.isConditional());

        FieldMeta nameMeta = beanMeta.requireFieldMeta("name");
        Assertions.assertSame(beanMeta, nameMeta.getBeanMeta());
        Assertions.assertEquals("name", nameMeta.getName());
        Assertions.assertSame(String.class, nameMeta.getType());
        Assertions.assertEquals("name", nameMeta.getFieldSql().getSql());
        Assertions.assertTrue(nameMeta.getFieldSql().getParas().isEmpty());
        Assertions.assertEquals(0, nameMeta.getOnlyOn().length);
        Assertions.assertTrue(nameMeta.isConditional());

        FieldMeta enableMeta = beanMeta.requireFieldMeta("enable");
        Assertions.assertSame(beanMeta, enableMeta.getBeanMeta());
        Assertions.assertEquals("enable", enableMeta.getName());
        Assertions.assertSame(boolean.class, enableMeta.getType());
        Assertions.assertEquals("enable", enableMeta.getFieldSql().getSql());
        Assertions.assertTrue(enableMeta.getFieldSql().getParas().isEmpty());
        Assertions.assertEquals(0, enableMeta.getOnlyOn().length);
        Assertions.assertTrue(enableMeta.isConditional());

        FieldMeta birthdayMeta = beanMeta.requireFieldMeta("birthday");
        Assertions.assertSame(beanMeta, birthdayMeta.getBeanMeta());
        Assertions.assertEquals("birthday", birthdayMeta.getName());
        Assertions.assertSame(Date.class, birthdayMeta.getType());
        Assertions.assertEquals("birthday", birthdayMeta.getFieldSql().getSql());
        Assertions.assertTrue(birthdayMeta.getFieldSql().getParas().isEmpty());
        Assertions.assertEquals(0, birthdayMeta.getOnlyOn().length);
        Assertions.assertTrue(birthdayMeta.isConditional());

        assertAlias(beanMeta.getFieldMetas());
    }

    private void assertAlias(Collection<FieldMeta> metas) {
        HashSet<String> set = new HashSet<>(metas.size());
        for (FieldMeta meta: metas) {
            String alias = meta.getDbAlias();
            System.out.println(meta.getName() + ": " + alias);
            char fc = alias.charAt(0);
            // Oracle 数据库的别名不能以下划线开头
            Assertions.assertTrue(fc >= 'A' && fc <= 'z' && (fc <= 'Z' || fc >= 'a'), "别名必须以字母开头");
            for (char c : alias.toCharArray()) {
                Assertions.assertTrue(c >= 'A' && c <= 'z' && (c <= 'Z' || c >= 'a') || (c >= '0' && c <= '9') || c == '_', "别名必须由字母数字下划线组成");
            }
            Assertions.assertFalse(set.contains(alias), "别名不能重复");
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
        Assertions.assertEquals("c_2", beanMeta.requireFieldMeta("id").getDbAlias());
        Assertions.assertEquals("c_1", beanMeta.requireFieldMeta("enable").getDbAlias());
        Assertions.assertEquals("xxx", beanMeta.requireFieldMeta("birthday").getDbAlias());
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
            Assertions.assertTrue(message.startsWith("The alias [bb] of [" + User03.class.getName()));
            Assertions.assertTrue(message.endsWith("] is already exists on other fields."));
            Assertions.assertTrue(message.contains("User03.id]") || message.contains("User03.enable]"));
            exception = true;
        }
        Assertions.assertTrue(exception);
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
        Assertions.assertArrayEquals(new Object[] {GreaterEqual.class}, beanMeta.requireFieldMeta("id").getOnlyOn());
        Assertions.assertArrayEquals(new Object[] {StartWith.class, Equal.class}, beanMeta.requireFieldMeta("name").getOnlyOn());
        Assertions.assertArrayEquals(new Object[] {}, beanMeta.requireFieldMeta("enable").getOnlyOn());
        Assertions.assertArrayEquals(new Object[] {}, beanMeta.requireFieldMeta("birthday").getOnlyOn());
        Assertions.assertTrue(beanMeta.requireFieldMeta("id").isConditional());
        Assertions.assertTrue(beanMeta.requireFieldMeta("name").isConditional());
        Assertions.assertFalse(beanMeta.requireFieldMeta("enable").isConditional());
        Assertions.assertTrue(beanMeta.requireFieldMeta("birthday").isConditional());
        assertAlias(beanMeta.getFieldMetas());
    }

    @SearchBean(tables = "user u, role r", where = "u.role_id = r.id", autoMapTo = "u")
    public static class User05 {
        private static final int serializeId = 1111;
        private final transient int modCount = 0;
        private long id;
        private String name;
        private boolean enable;
        @DbField("r.name")
        private String role;
    }

    @Test
    public void test05() {
        BeanMeta<User05> beanMeta = metaResolver.resolve(User05.class);
        Assertions.assertEquals("user u, role r", beanMeta.getTableSnippet().getSql());
        Assertions.assertTrue(beanMeta.getTableSnippet().getParas().isEmpty());

        Assertions.assertEquals("u.role_id = r.id", beanMeta.getWhere());
        Assertions.assertTrue(beanMeta.getWhereSqlParas().isEmpty());

        Assertions.assertTrue(StringUtils.isBlank(beanMeta.getGroupBy()));
        Assertions.assertTrue(beanMeta.getGroupBySqlParas().isEmpty());

        Assertions.assertTrue(StringUtils.isBlank(beanMeta.getOrderBySnippet().getSql()));
        Assertions.assertTrue(beanMeta.getOrderBySnippet().getParas().isEmpty());

        Assertions.assertSame(User05.class, beanMeta.getBeanClass());
        Assertions.assertTrue(StringUtils.isBlank(beanMeta.getDataSource()));

        Assertions.assertFalse(beanMeta.isDistinct());
        Assertions.assertTrue(beanMeta.isSortable());
        Assertions.assertFalse(beanMeta.isDistinctOrGroupBy());

        Assertions.assertEquals(4, beanMeta.getFieldCount());
        Assertions.assertEquals(4, beanMeta.getFieldSet().size());
        Assertions.assertEquals(4, beanMeta.getFieldMetas().size());

        FieldMeta idMeta = beanMeta.requireFieldMeta("id");
        Assertions.assertEquals("u.id", idMeta.getFieldSql().getSql());

        FieldMeta nameMeta = beanMeta.requireFieldMeta("name");
        Assertions.assertEquals("u.name", nameMeta.getFieldSql().getSql());

        FieldMeta enableMeta = beanMeta.requireFieldMeta("enable");
        Assertions.assertEquals("u.enable", enableMeta.getFieldSql().getSql());

        FieldMeta birthdayMeta = beanMeta.requireFieldMeta("role");
        Assertions.assertEquals("r.name", birthdayMeta.getFieldSql().getSql());

        assertAlias(beanMeta.getFieldMetas());
    }

    @SearchBean(tables = ":t_name: t", autoMapTo = "t")
    public static class User06 {
        private long id;
        private String name;
    }

    @Test
    public void test06() {
        BeanMeta<User06> beanMeta = metaResolver.resolve(User06.class);
        SqlSnippet tableSnippet = beanMeta.getTableSnippet();
        Assertions.assertEquals(":t_name: t", tableSnippet.getSql());
        List<SqlSnippet.SqlPara> tableParas = tableSnippet.getParas();
        Assertions.assertEquals(1, tableParas.size());
        SqlSnippet.SqlPara tablePara = tableParas.get(0);

        Assertions.assertEquals("t_name", tablePara.getName());
        Assertions.assertEquals(":t_name:", tablePara.getSqlName());
        Assertions.assertFalse(tablePara.isJdbcPara());

        Assertions.assertTrue(StringUtils.isBlank(beanMeta.getWhere()));
        Assertions.assertTrue(beanMeta.getWhereSqlParas().isEmpty());

        Assertions.assertTrue(StringUtils.isBlank(beanMeta.getGroupBy()));
        Assertions.assertTrue(beanMeta.getGroupBySqlParas().isEmpty());

        Assertions.assertTrue(StringUtils.isBlank(beanMeta.getOrderBySnippet().getSql()));
        Assertions.assertTrue(beanMeta.getOrderBySnippet().getParas().isEmpty());

        Assertions.assertSame(User06.class, beanMeta.getBeanClass());
        Assertions.assertTrue(StringUtils.isBlank(beanMeta.getDataSource()));

        Assertions.assertFalse(beanMeta.isDistinct());
        Assertions.assertTrue(beanMeta.isSortable());
        Assertions.assertFalse(beanMeta.isDistinctOrGroupBy());

        Assertions.assertEquals(2, beanMeta.getFieldCount());
        Assertions.assertEquals(2, beanMeta.getFieldSet().size());
        Assertions.assertEquals(2, beanMeta.getFieldMetas().size());

        FieldMeta idMeta = beanMeta.requireFieldMeta("id");
        Assertions.assertEquals("t.id", idMeta.getFieldSql().getSql());

        FieldMeta nameMeta = beanMeta.requireFieldMeta("name");
        Assertions.assertEquals("t.name", nameMeta.getFieldSql().getSql());

        assertAlias(beanMeta.getFieldMetas());
    }


    @SearchBean(tables = ":t_name: t left join (select * from user where type = :type) u", autoMapTo = "t")
    public static class User07 {
        private long id;
    }

    @Test
    public void test07() {
        BeanMeta<User07> beanMeta = metaResolver.resolve(User07.class);
        SqlSnippet tableSnippet = beanMeta.getTableSnippet();
        Assertions.assertEquals(":t_name: t left join (select * from user where type = ?) u", tableSnippet.getSql());
        List<SqlSnippet.SqlPara> tableParas = tableSnippet.getParas();
        Assertions.assertEquals(2, tableParas.size());

        SqlSnippet.SqlPara para1 = tableParas.get(0);
        Assertions.assertEquals("t_name", para1.getName());
        Assertions.assertEquals(":t_name:", para1.getSqlName());
        Assertions.assertFalse(para1.isJdbcPara());

        SqlSnippet.SqlPara para2 = tableParas.get(1);
        Assertions.assertEquals("type", para2.getName());
        Assertions.assertEquals(":type", para2.getSqlName());
        Assertions.assertTrue(para2.isJdbcPara());
    }

    @SearchBean(where = "age = :age and :ext_cond:")
    public static class User08 {
        private long id;
    }

    @Test
    public void test08() {
        BeanMeta<User08> beanMeta = metaResolver.resolve(User08.class);
        Assertions.assertEquals("age = ? and :ext_cond:", beanMeta.getWhere());
        List<SqlSnippet.SqlPara> jsonCondParas = beanMeta.getWhereSqlParas();
        Assertions.assertEquals(2, jsonCondParas.size());

        SqlSnippet.SqlPara para1 = jsonCondParas.get(0);
        Assertions.assertEquals("age", para1.getName());
        Assertions.assertEquals(":age", para1.getSqlName());
        Assertions.assertTrue(para1.isJdbcPara());

        SqlSnippet.SqlPara para2 = jsonCondParas.get(1);
        Assertions.assertEquals("ext_cond", para2.getName());
        Assertions.assertEquals(":ext_cond:", para2.getSqlName());
        Assertions.assertFalse(para2.isJdbcPara());
    }

    @SearchBean(groupBy = "sex, :g:")
    public static class User09 {
        private long id;
    }

    @Test
    public void test09() {
        BeanMeta<User09> beanMeta = metaResolver.resolve(User09.class);
        Assertions.assertEquals("sex, :g:", beanMeta.getGroupBy());
        List<SqlSnippet.SqlPara> paras = beanMeta.getGroupBySqlParas();
        Assertions.assertEquals(1, paras.size());
        SqlSnippet.SqlPara para1 = paras.get(0);
        Assertions.assertEquals("g", para1.getName());
        Assertions.assertEquals(":g:", para1.getSqlName());
        Assertions.assertFalse(para1.isJdbcPara());

        Assertions.assertTrue(beanMeta.isDistinctOrGroupBy());
    }

    @SearchBean(orderBy = "age :order:")
    public static class User10 {
        private long id;
    }

    @Test
    public void test10() {
        BeanMeta<User10> beanMeta = metaResolver.resolve(User10.class);
        SqlSnippet orderBySnippet = beanMeta.getOrderBySnippet();
        Assertions.assertEquals("age :order:", orderBySnippet.getSql());
        List<SqlSnippet.SqlPara> paras = orderBySnippet.getParas();
        Assertions.assertEquals(1, paras.size());
        SqlSnippet.SqlPara para1 = paras.get(0);
        Assertions.assertEquals("order", para1.getName());
        Assertions.assertEquals(":order:", para1.getSqlName());
        Assertions.assertFalse(para1.isJdbcPara());
        Assertions.assertTrue(beanMeta.isSortable());
    }

    @SearchBean(distinct = true)
    public static class User11 {
        private long id;
    }

    static final MetaResolver ignoreNameAgeHeightAndOnlyEntityMetaResolver;

    static {
        DefaultDbMapping dbMapping = new DefaultDbMapping();
        dbMapping.setDefaultSortType(SortType.ONLY_ENTITY);
        dbMapping.setIgnoreFields(new String[]{ "name", "age", "height" });
        ignoreNameAgeHeightAndOnlyEntityMetaResolver = new DefaultMetaResolver(dbMapping);
    }

    @Test
    public void test11() {
        BeanMeta<User11> beanMeta = metaResolver.resolve(User11.class);
        Assertions.assertTrue(beanMeta.isDistinct());
        Assertions.assertTrue(beanMeta.isDistinctOrGroupBy());
        Assertions.assertTrue(beanMeta.isSortable());

        BeanMeta<User11> beanMeta2 = ignoreNameAgeHeightAndOnlyEntityMetaResolver.resolve(User11.class);
        Assertions.assertTrue(beanMeta2.isDistinct());
        Assertions.assertTrue(beanMeta2.isDistinctOrGroupBy());
        Assertions.assertFalse(beanMeta2.isSortable());
    }

    @SearchBean(sortType = SortType.ONLY_ENTITY)
    public static class User12 {
        private long id;
    }

    @Test
    public void test12() {
        BeanMeta<User12> beanMeta = metaResolver.resolve(User12.class);
        Assertions.assertFalse(beanMeta.isDistinct());
        Assertions.assertFalse(beanMeta.isDistinctOrGroupBy());
        Assertions.assertFalse(beanMeta.isSortable());

        BeanMeta<User12> beanMeta2 = ignoreNameAgeHeightAndOnlyEntityMetaResolver.resolve(User12.class);
        Assertions.assertFalse(beanMeta2.isDistinct());
        Assertions.assertFalse(beanMeta2.isDistinctOrGroupBy());
        Assertions.assertFalse(beanMeta2.isSortable());
    }

    @SearchBean(sortType = SortType.DEFAULT)
    public static class User13 {
        private long id;
    }

    @Test
    public void test13() {
        BeanMeta<User13> beanMeta = metaResolver.resolve(User13.class);
        Assertions.assertFalse(beanMeta.isDistinct());
        Assertions.assertFalse(beanMeta.isDistinctOrGroupBy());
        Assertions.assertTrue(beanMeta.isSortable());

        BeanMeta<User13> beanMeta2 = ignoreNameAgeHeightAndOnlyEntityMetaResolver.resolve(User13.class);
        Assertions.assertFalse(beanMeta2.isDistinct());
        Assertions.assertFalse(beanMeta2.isDistinctOrGroupBy());
        Assertions.assertFalse(beanMeta2.isSortable());
    }

    @SearchBean(sortType = SortType.ALLOW_PARAM)
    public static class User14 {
        private long id;
    }

    @Test
    public void test14() {
        BeanMeta<User14> beanMeta = metaResolver.resolve(User14.class);
        Assertions.assertFalse(beanMeta.isDistinct());
        Assertions.assertFalse(beanMeta.isDistinctOrGroupBy());
        Assertions.assertTrue(beanMeta.isSortable());

        BeanMeta<User14> beanMeta2 = ignoreNameAgeHeightAndOnlyEntityMetaResolver.resolve(User14.class);
        Assertions.assertFalse(beanMeta2.isDistinct());
        Assertions.assertFalse(beanMeta2.isDistinctOrGroupBy());
        Assertions.assertTrue(beanMeta2.isSortable());
    }

    public static class User15 {
        private long id;
        @DbIgnore
        private String name;
    }

    @Test
    public void test15() {
        BeanMeta<User15> beanMeta = metaResolver.resolve(User15.class);
        Assertions.assertEquals(1, beanMeta.getFieldCount());
        Assertions.assertNotNull(beanMeta.getFieldMeta("id"));
        Assertions.assertNull(beanMeta.getFieldMeta("name"));
    }

    @SearchBean(ignoreFields = {"age", "height"})
    public static class User16 {
        private long id;
        @DbIgnore
        private String name;
        private int age;
        private int height;
    }

    @Test
    public void test16() {
        BeanMeta<User16> beanMeta = metaResolver.resolve(User16.class);
        Assertions.assertEquals(1, beanMeta.getFieldCount());
        Assertions.assertNotNull(beanMeta.getFieldMeta("id"));
        Assertions.assertNull(beanMeta.getFieldMeta("name"));
        Assertions.assertNull(beanMeta.getFieldMeta("age"));
        Assertions.assertNull(beanMeta.getFieldMeta("height"));
    }

    public static class User17 {
        private long id;
        private String name;
        private int age;
        private int height;
    }

    @Test
    public void test17() {
        BeanMeta<User17> beanMeta = ignoreNameAgeHeightAndOnlyEntityMetaResolver.resolve(User17.class);
        Assertions.assertEquals(1, beanMeta.getFieldCount());
        Assertions.assertNotNull(beanMeta.getFieldMeta("id"));
        Assertions.assertNull(beanMeta.getFieldMeta("name"));
        Assertions.assertNull(beanMeta.getFieldMeta("age"));
        Assertions.assertNull(beanMeta.getFieldMeta("height"));
    }

    public static class User18 {
        private long id;
        @DbField
        private String name;
        @DbField
        private int age;
        private int height;
    }

    @Test
    public void test18() {
        BeanMeta<User18> beanMeta = ignoreNameAgeHeightAndOnlyEntityMetaResolver.resolve(User18.class);
        Assertions.assertEquals(3, beanMeta.getFieldCount());
        Assertions.assertNotNull(beanMeta.getFieldMeta("id"));
        Assertions.assertNotNull(beanMeta.getFieldMeta("name"));
        Assertions.assertNotNull(beanMeta.getFieldMeta("age"));
        Assertions.assertNull(beanMeta.getFieldMeta("height"));
    }

    public static class Base19 {
        private long id;
        private String name;
        private int sex;
        @DbField
        private String hobby;
    }

    @SearchBean(ignoreFields = {"birthday", "sex", "hobby"})
    public static class User19 extends Base19 {
        @DbField
        private int age;
        private int height;
        private Date birthday;
    }

    @Test
    public void test19() {
        BeanMeta<User19> beanMeta = metaResolver.resolve(User19.class);
        Assertions.assertEquals(4, beanMeta.getFieldCount());
        Assertions.assertNotNull(beanMeta.getFieldMeta("id"));
        Assertions.assertNotNull(beanMeta.getFieldMeta("name"));
        Assertions.assertNotNull(beanMeta.getFieldMeta("age"));
        Assertions.assertNotNull(beanMeta.getFieldMeta("height"));
        Assertions.assertNull(beanMeta.getFieldMeta("birthday"));
        Assertions.assertNull(beanMeta.getFieldMeta("sex"));
        Assertions.assertNull(beanMeta.getFieldMeta("hobby"));

        BeanMeta<User19> beanMeta2 = ignoreNameAgeHeightAndOnlyEntityMetaResolver.resolve(User19.class);
        Assertions.assertEquals(2, beanMeta2.getFieldCount());
        Assertions.assertNotNull(beanMeta2.getFieldMeta("id"));
        Assertions.assertNull(beanMeta2.getFieldMeta("name"));
        Assertions.assertNotNull(beanMeta2.getFieldMeta("age"));
        Assertions.assertNull(beanMeta2.getFieldMeta("height"));
        Assertions.assertNull(beanMeta2.getFieldMeta("birthday"));
        Assertions.assertNull(beanMeta2.getFieldMeta("sex"));
        Assertions.assertNull(beanMeta2.getFieldMeta("hobby"));
    }

    @SearchBean(ignoreFields = {"name", "sex", "age", "height"})
    public static class Base20 {
        private long id;
        private String name;
        private int sex;
    }

    public static class User20 extends Base20 {
        @DbField
        private int age;
        private int height;
        private Date birthday;
        @DbField
        private int sex;
    }

    @Test
    public void test20() {
        BeanMeta<User20> beanMeta = metaResolver.resolve(User20.class);
        Assertions.assertEquals(4, beanMeta.getFieldCount());
        Assertions.assertNotNull(beanMeta.getFieldMeta("id"));
        Assertions.assertNull(beanMeta.getFieldMeta("name"));
        Assertions.assertNotNull(beanMeta.getFieldMeta("age"));
        Assertions.assertNull(beanMeta.getFieldMeta("height"));
        Assertions.assertNotNull(beanMeta.getFieldMeta("birthday"));
        Assertions.assertNotNull(beanMeta.getFieldMeta("sex"));
    }

    @SearchBean(ignoreFields = {"name", "age"})
    public static class Base21 {
        private long id;
        @DbField
        private String name;
        private int age;
    }

    public static class User21 extends Base21 {
        private int height;
        private Date birthday;
    }

    @Test
    public void test21() {
        boolean exception = false;
        try {
            metaResolver.resolve(User21.class);
        } catch (SearchException e) {
            System.out.println(e.getMessage());
            exception = true;
        }
        Assertions.assertTrue(exception);
        exception = false;
        try {
            metaResolver.resolve(Base21.class);
        } catch (SearchException e) {
            System.out.println(e.getMessage());
            exception = true;
        }
        Assertions.assertTrue(exception);
    }

    public static class User22 extends Base21 {
        private String name;
        private int sex;
        @DbField
        private int age;
    }

    @Test
    public void test22() {
        BeanMeta<User22> beanMeta = metaResolver.resolve(User22.class);
        Assertions.assertEquals(3, beanMeta.getFieldCount());
        Assertions.assertNotNull(beanMeta.getFieldMeta("id"));
        Assertions.assertNull(beanMeta.getFieldMeta("name"));
        Assertions.assertNotNull(beanMeta.getFieldMeta("sex"));
        Assertions.assertNotNull(beanMeta.getFieldMeta("age"));
    }

    public static class Base23 {
        private long id;
        static int base = 1;
    }

    public static class Base23_ extends Base23 {
        private String name;
        transient int base2 = 1;
    }

    public static class User23 extends Base23_ {
        private int age;
    }

    @SearchBean(inheritType = InheritType.NONE)
    public static class User23_1 extends Base23_ {
        private int age;
    }

    @SearchBean(inheritType = InheritType.TABLE)
    public static class User23_2 extends Base23_ {
        private int age;
    }

    @Test
    public void test23() {
        BeanMeta<User23> beanMeta = metaResolver.resolve(User23.class);
        Assertions.assertEquals(3, beanMeta.getFieldCount());
        Assertions.assertNotNull(beanMeta.getFieldMeta("id"));
        Assertions.assertNotNull(beanMeta.getFieldMeta("name"));
        Assertions.assertNotNull(beanMeta.getFieldMeta("age"));

        Assertions.assertEquals(1, metaResolver.resolve(User23_1.class).getFieldCount());
        Assertions.assertEquals(1, metaResolver.resolve(User23_2.class).getFieldCount());
    }

    @SearchBean(tables = "user")
    public static class User24_1 {
        private int age;
    }

    @SearchBean(tables = "user u")
    public static class User24_2 {
        private int age;
    }

    @SearchBean(tables = "user, role")
    public static class User24_3 {
        private int age;
    }

    @Test
    public void test24() {
        Assertions.assertEquals(1, metaResolver.resolve(User24_1.class).getFieldCount());
        Assertions.assertEquals(1, metaResolver.resolve(User24_2.class).getFieldCount());
        SearchException ex = null;
        try {
            metaResolver.resolve(User24_3.class);
        } catch (SearchException e) {
            ex = e;
        }
        Assertions.assertNotNull(ex);
        Assertions.assertTrue(ex.getMessage().contains("is not a valid SearchBean, because there is no field mapping to database"));
    }

}
