package cn.zhxu.bs.implement;

import cn.zhxu.bs.*;
import cn.zhxu.bs.bean.DbField;
import cn.zhxu.bs.bean.DbIgnore;
import cn.zhxu.bs.bean.SearchBean;
import cn.zhxu.bs.convertor.EnumFieldConvertor;
import cn.zhxu.bs.convertor.NumberFieldConvertor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.*;

/**
 * {@link DefaultBeanReflector} 单元测试。
 * <p>
 * 覆盖两条路径：
 * <ol>
 *   <li>普通 class：无参构造 + setter（字段直接赋值）</li>
 *   <li>Java record：canonical constructor 实例化</li>
 * </ol>
 * 并测试类型转换（convertor）、异常分支等。
 *
 * @since v4.9.0
 */
public class DefaultBeanReflectorTestCase {

    static final MetaResolver metaResolver = new DefaultMetaResolver();

    // -----------------------------------------------------------------
    // 辅助方法
    // -----------------------------------------------------------------

    /** 构造一个不带任何 convertor 的 reflector */
    private DefaultBeanReflector plainReflector() {
        return new DefaultBeanReflector();
    }

    /** 构造一个带 NumberFieldConvertor 的 reflector */
    private DefaultBeanReflector numberReflector() {
        DefaultBeanReflector r = new DefaultBeanReflector();
        r.addConvertor(new NumberFieldConvertor());
        return r;
    }

    /** 构造一个带 NumberFieldConvertor + EnumFieldConvertor 的 reflector */
    private DefaultBeanReflector fullReflector() {
        DefaultBeanReflector r = new DefaultBeanReflector();
        r.addConvertor(new NumberFieldConvertor());
        r.addConvertor(new EnumFieldConvertor());
        return r;
    }

    /** 用给定 reflector 通过 alias→value 映射反射出实例 */
    private <T> T reflect(DefaultBeanReflector reflector, BeanMeta<T> meta, Map<String, Object> values) {
        return reflector.reflect(meta, meta.getFieldMetas(), values::get);
    }

    // =================================================================
    //  ① 普通 class 路径
    // =================================================================

    // -----------------------------------------------------------------
    // 1-1. 基础赋值
    // -----------------------------------------------------------------

    public static class BasicBean {
        public Long id;
        public String name;
        public Integer age;
    }

    @Test
    public void test_plain_basic() {
        BeanMeta<BasicBean> meta = metaResolver.resolve(BasicBean.class);
        Assertions.assertFalse(meta.isRecord());

        Map<String, Object> values = aliasMap(meta,
                "id", 1L,
                "name", "Alice",
                "age", 25);

        BasicBean bean = reflect(plainReflector(), meta, values);
        Assertions.assertEquals(1L,       bean.id);
        Assertions.assertEquals("Alice",  bean.name);
        Assertions.assertEquals(25,       bean.age);
        System.out.println("\ttest_plain_basic ok!");
    }

    // -----------------------------------------------------------------
    // 1-2. null 值：只提供部分字段，其余保持默认（null / 0）
    // -----------------------------------------------------------------

    @Test
    public void test_plain_partial_null() {
        BeanMeta<BasicBean> meta = metaResolver.resolve(BasicBean.class);

        Map<String, Object> values = aliasMap(meta, "id", 9L);

        BasicBean bean = reflect(plainReflector(), meta, values);
        Assertions.assertEquals(9L,   bean.id);
        Assertions.assertNull(bean.name);   // 未提供 → null
        Assertions.assertNull(bean.age);    // 未提供 → null
        System.out.println("\ttest_plain_partial_null ok!");
    }

    // -----------------------------------------------------------------
    // 1-3. @DbIgnore 字段不出现在 fieldMetas，赋值后字段保持默认值
    // -----------------------------------------------------------------

    public static class IgnoreBean {
        public Long id;
        @DbIgnore
        public String secret;
        public String name;
    }

    @Test
    public void test_plain_db_ignore() {
        BeanMeta<IgnoreBean> meta = metaResolver.resolve(IgnoreBean.class);
        Assertions.assertEquals(2, meta.getFieldCount());
        Assertions.assertNull(meta.getFieldMeta("secret"));

        Map<String, Object> values = aliasMap(meta,
                "id",   3L,
                "name", "Bob");

        IgnoreBean bean = reflect(plainReflector(), meta, values);
        Assertions.assertEquals(3L,    bean.id);
        Assertions.assertEquals("Bob", bean.name);
        Assertions.assertNull(bean.secret);   // 未被赋值
        System.out.println("\ttest_plain_db_ignore ok!");
    }

    // -----------------------------------------------------------------
    // 1-4. @DbField 指定列名
    // -----------------------------------------------------------------

    public static class AliasBean {
        @DbField("user_id") public Long id;
        @DbField("user_name") public String name;
    }

    @Test
    public void test_plain_db_field() {
        BeanMeta<AliasBean> meta = metaResolver.resolve(AliasBean.class);

        Map<String, Object> values = aliasMap(meta,
                "id",   7L,
                "name", "Carol");

        AliasBean bean = reflect(plainReflector(), meta, values);
        Assertions.assertEquals(7L,      bean.id);
        Assertions.assertEquals("Carol", bean.name);
        System.out.println("\ttest_plain_db_field ok!");
    }

    // -----------------------------------------------------------------
    // 1-5. 带 NumberFieldConvertor：Long → int / BigDecimal
    // -----------------------------------------------------------------

    public static class NumberBean {
        public int count;
        public BigDecimal price;
    }

    @Test
    public void test_plain_number_convertor() {
        BeanMeta<NumberBean> meta = metaResolver.resolve(NumberBean.class);

        Map<String, Object> values = aliasMap(meta,
                "count", 42L,           // Long → int
                "price", 99L);          // Long → BigDecimal

        NumberBean bean = reflect(numberReflector(), meta, values);
        Assertions.assertEquals(42,                    bean.count);
        Assertions.assertEquals(new BigDecimal("99"),  bean.price);
        System.out.println("\ttest_plain_number_convertor ok!");
    }

    // -----------------------------------------------------------------
    // 1-6. 带 EnumFieldConvertor：String / ordinal → enum
    // -----------------------------------------------------------------

    public enum Status { ACTIVE, DISABLED }

    public static class EnumBean {
        public Status statusByName;
        @DbField("status_by_ord") public Status statusByOrd;
    }

    @Test
    public void test_plain_enum_convertor() {
        BeanMeta<EnumBean> meta = metaResolver.resolve(EnumBean.class);

        Map<String, Object> values = aliasMap(meta,
                "statusByName", "ACTIVE",   // String → Status
                "statusByOrd",  1);          // int(ordinal=1) → DISABLED

        EnumBean bean = reflect(fullReflector(), meta, values);
        Assertions.assertEquals(Status.ACTIVE,   bean.statusByName);
        Assertions.assertEquals(Status.DISABLED, bean.statusByOrd);
        System.out.println("\ttest_plain_enum_convertor ok!");
    }

    // -----------------------------------------------------------------
    // 1-7. 无 convertor 时类型不兼容抛出 SearchException
    // -----------------------------------------------------------------

    public static class StrictBean {
        public int value;
    }

    @Test
    public void test_plain_no_convertor_throws() {
        BeanMeta<StrictBean> meta = metaResolver.resolve(StrictBean.class);

        Map<String, Object> values = aliasMap(meta, "value", 123L); // Long 无法直接赋给 int

        Assertions.assertThrows(SearchException.class, () ->
                reflect(plainReflector(), meta, values));
        System.out.println("\ttest_plain_no_convertor_throws ok!");
    }

    // -----------------------------------------------------------------
    // 1-8. 没有无参构造器时抛出 SearchException
    // -----------------------------------------------------------------

    public static class NoDefaultConstructor {
        public final Long id;
        public NoDefaultConstructor(Long id) { this.id = id; }
    }

    @Test
    public void test_plain_no_default_constructor_throws() {
        BeanMeta<NoDefaultConstructor> meta = metaResolver.resolve(NoDefaultConstructor.class);

        Assertions.assertThrows(SearchException.class, () ->
                reflect(plainReflector(), meta, Collections.emptyMap()));
        System.out.println("\ttest_plain_no_default_constructor_throws ok!");
    }

    // -----------------------------------------------------------------
    // 1-9. 继承链：子类 + 父类字段均能正确赋值
    // -----------------------------------------------------------------

    public static class ParentBean {
        public Long id;
    }

    public static class ChildBean extends ParentBean {
        public String name;
    }

    @Test
    public void test_plain_inheritance() {
        BeanMeta<ChildBean> meta = metaResolver.resolve(ChildBean.class);
        Assertions.assertEquals(2, meta.getFieldCount());

        Map<String, Object> values = aliasMap(meta,
                "id",   10L,
                "name", "Dave");

        ChildBean bean = reflect(plainReflector(), meta, values);
        Assertions.assertEquals(10L,    bean.id);
        Assertions.assertEquals("Dave", bean.name);
        System.out.println("\ttest_plain_inheritance ok!");
    }

    // =================================================================
    //  ② record 路径
    // =================================================================

    // -----------------------------------------------------------------
    // 2-1. 基础 record 实例化
    // -----------------------------------------------------------------

    public record BasicRecord(Long id, String name, Integer age) {}

    @Test
    public void test_record_basic() {
        BeanMeta<BasicRecord> meta = metaResolver.resolve(BasicRecord.class);
        Assertions.assertTrue(meta.isRecord());

        Map<String, Object> values = aliasMap(meta,
                "id",   1L,
                "name", "Alice",
                "age",  25);

        BasicRecord r = reflect(plainReflector(), meta, values);
        Assertions.assertEquals(1L,       r.id());
        Assertions.assertEquals("Alice",  r.name());
        Assertions.assertEquals(25,       r.age());
        System.out.println("\ttest_record_basic ok!");
    }

    // -----------------------------------------------------------------
    // 2-2. 全部字段为 null
    // -----------------------------------------------------------------

    public record AllNullRecord(Long id, String name) {}

    @Test
    public void test_record_all_null() {
        BeanMeta<AllNullRecord> meta = metaResolver.resolve(AllNullRecord.class);

        AllNullRecord r = reflect(plainReflector(), meta, Collections.emptyMap());
        Assertions.assertNotNull(r);
        Assertions.assertNull(r.id());
        Assertions.assertNull(r.name());
        System.out.println("\ttest_record_all_null ok!");
    }

    // -----------------------------------------------------------------
    // 2-3. @DbIgnore：canonical constructor 中对应位置传 null
    // -----------------------------------------------------------------

    public record IgnoreRecord(Long id, @DbIgnore String memo, String name) {}

    @Test
    public void test_record_db_ignore() {
        BeanMeta<IgnoreRecord> meta = metaResolver.resolve(IgnoreRecord.class);
        Assertions.assertEquals(2, meta.getFieldCount());

        Map<String, Object> values = aliasMap(meta,
                "id",   99L,
                "name", "Eve");

        IgnoreRecord r = reflect(plainReflector(), meta, values);
        Assertions.assertEquals(99L,   r.id());
        Assertions.assertNull(r.memo());   // @DbIgnore → constructor 中传 null
        Assertions.assertEquals("Eve", r.name());
        System.out.println("\ttest_record_db_ignore ok!");
    }

    // -----------------------------------------------------------------
    // 2-4. @DbField 指定列名
    // -----------------------------------------------------------------

    public record AliasRecord(
            @DbField("user_id") Long id,
            @DbField("user_name") String name) {}

    @Test
    public void test_record_db_field() {
        BeanMeta<AliasRecord> meta = metaResolver.resolve(AliasRecord.class);
        // 验证列名映射是否正确解析
        Assertions.assertEquals("user_id",   meta.requireFieldMeta("id").getFieldSql().getSql());
        Assertions.assertEquals("user_name", meta.requireFieldMeta("name").getFieldSql().getSql());

        Map<String, Object> values = aliasMap(meta,
                "id",   5L,
                "name", "Frank");

        AliasRecord r = reflect(plainReflector(), meta, values);
        Assertions.assertEquals(5L,       r.id());
        Assertions.assertEquals("Frank",  r.name());
        System.out.println("\ttest_record_db_field ok!");
    }

    // -----------------------------------------------------------------
    // 2-5. 带 NumberFieldConvertor：BigDecimal → Long / int
    // -----------------------------------------------------------------

    public record NumberRecord(Long amount, int count) {}

    @Test
    public void test_record_number_convertor() {
        BeanMeta<NumberRecord> meta = metaResolver.resolve(NumberRecord.class);

        Map<String, Object> values = aliasMap(meta,
                "amount", new BigDecimal("1000"),   // BigDecimal → Long
                "count",  new BigDecimal("7"));      // BigDecimal → int

        NumberRecord r = reflect(numberReflector(), meta, values);
        Assertions.assertEquals(1000L, r.amount());
        Assertions.assertEquals(7,     r.count());
        System.out.println("\ttest_record_number_convertor ok!");
    }

    // -----------------------------------------------------------------
    // 2-6. 带 EnumFieldConvertor：String → enum
    // -----------------------------------------------------------------

    public record StatusRecord(Long id, Status status) {}

    @Test
    public void test_record_enum_convertor() {
        BeanMeta<StatusRecord> meta = metaResolver.resolve(StatusRecord.class);

        Map<String, Object> values = aliasMap(meta,
                "id",     2L,
                "status", "DISABLED");

        StatusRecord r = reflect(fullReflector(), meta, values);
        Assertions.assertEquals(2L,              r.id());
        Assertions.assertEquals(Status.DISABLED, r.status());
        System.out.println("\ttest_record_enum_convertor ok!");
    }

    // -----------------------------------------------------------------
    // 2-7. 多表 record
    // -----------------------------------------------------------------

    @SearchBean(tables = "user u, role r", where = "u.role_id = r.id", autoMapTo = "u")
    public record UserRoleRecord(Long id, String name, @DbField("r.name") String roleName) {}

    @Test
    public void test_record_multi_table() {
        BeanMeta<UserRoleRecord> meta = metaResolver.resolve(UserRoleRecord.class);
        Assertions.assertEquals(3, meta.getFieldCount());

        Map<String, Object> values = aliasMap(meta,
                "id",       8L,
                "name",     "Grace",
                "roleName", "Manager");

        UserRoleRecord r = reflect(plainReflector(), meta, values);
        Assertions.assertEquals(8L,          r.id());
        Assertions.assertEquals("Grace",     r.name());
        Assertions.assertEquals("Manager",   r.roleName());
        System.out.println("\ttest_record_multi_table ok!");
    }

    // -----------------------------------------------------------------
    // 2-8. 无 convertor 时类型不兼容抛出 SearchException
    // -----------------------------------------------------------------

    public record StrictRecord(int value) {}

    @Test
    public void test_record_no_convertor_throws() {
        BeanMeta<StrictRecord> meta = metaResolver.resolve(StrictRecord.class);

        Map<String, Object> values = aliasMap(meta, "value", 123L); // Long 无法直接赋给 int

        Assertions.assertThrows(SearchException.class, () ->
                reflect(plainReflector(), meta, values));
        System.out.println("\ttest_record_no_convertor_throws ok!");
    }

    // -----------------------------------------------------------------
    // 2-9. addConvertor / setConvertors / getConvertors API
    // -----------------------------------------------------------------

    @Test
    public void test_convertor_management() {
        DefaultBeanReflector r = new DefaultBeanReflector();
        Assertions.assertTrue(r.getConvertors().isEmpty());

        NumberFieldConvertor nc = new NumberFieldConvertor();
        EnumFieldConvertor ec = new EnumFieldConvertor();

        r.addConvertor(nc);
        Assertions.assertEquals(1, r.getConvertors().size());

        r.addConvertor(ec);
        Assertions.assertEquals(2, r.getConvertors().size());

        // setConvertors(null) 应抛 NullPointerException（Objects.requireNonNull）
        Assertions.assertThrows(NullPointerException.class, () -> r.setConvertors(null));

        r.setConvertors(Collections.emptyList());
        Assertions.assertTrue(r.getConvertors().isEmpty());
        System.out.println("\ttest_convertor_management ok!");
    }

    // =================================================================
    //  工具方法
    // =================================================================

    /**
     * 将 fieldName→value 的键值对转成 dbAlias→value 的 Map（供 valueGetter 使用）。
     * 参数格式：fieldName1, value1, fieldName2, value2, ...
     */
    private <T> Map<String, Object> aliasMap(BeanMeta<T> meta, Object... kvPairs) {
        Map<String, Object> result = new HashMap<>();
        for (int i = 0; i + 1 < kvPairs.length; i += 2) {
            String fieldName = (String) kvPairs[i];
            Object value     = kvPairs[i + 1];
            FieldMeta fm = meta.getFieldMeta(fieldName);
            if (fm != null) {
                result.put(fm.getDbAlias(), value);
            }
        }
        return result;
    }

}
