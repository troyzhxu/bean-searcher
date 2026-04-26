package cn.zhxu.bs;

import cn.zhxu.bs.convertor.NumberFieldConvertor;
import cn.zhxu.bs.implement.DefaultBeanReflector;
import cn.zhxu.bs.implement.DefaultMetaResolver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * 测试 FieldMeta.getType() 对泛型父类继承字段的类型解析
 * @since v4.8.7
 */
public class FieldMetaGenericTypeTestCase {

    static final MetaResolver metaResolver = new DefaultMetaResolver();

    // ==================== 测试用实体 ====================

    /**
     * Case 1: 单层泛型继承
     * Book extends BaseEntity&lt;Integer&gt; → id 类型应为 Integer
     */
    public static class BaseEntity<T extends Number> {
        private T id;
    }

    public static class Book extends BaseEntity<Integer> {
        private String name;
    }

    /**
     * Case 2: 多层泛型继承
     * SubBook → MiddleEntity&lt;Integer&gt; → BaseEntity2&lt;Integer&gt;
     */
    public static class BaseEntity2<T extends Number> {
        private T id;
    }

    public static class MiddleEntity<V extends Number> extends BaseEntity2<V> {
    }

    public static class SubBook extends MiddleEntity<Integer> {
    }

    /**
     * Case 3: 非泛型父类（回归测试——不应受影响）
     */
    public static class PlainBase {
        private long id;
        private String name;
    }

    public static class User extends PlainBase {
        private int age;
    }

    /**
     * Case 4: 多个类型参数 + 自引用（模拟 IdDelAD 场景）
     */
    public static class IdEntity<ID extends Number, D extends IdEntity<ID, D>> {
        private ID id;
        private int version;
    }

    public static class XmlBook extends IdEntity<Integer, XmlBook> {
    }

    /**
     * Case 5: 字段在自身类声明（无继承）
     */
    public static class SelfBean {
        private Integer id;
        private String name;
    }

    /**
     * Case 6: 多层 + 类型变量传递
     * Concrete extends Middle&lt;String&gt; → Middle extends Base&lt;V&gt;
     */
    public static class StringBase<T> {
        private T id;
    }

    public static class StringMiddle<V> extends StringBase<V> {
    }

    public static class Concrete extends StringMiddle<String> {
    }

    /**
     * Case 7: 相同类名不同包测试——保证 id 作为 Long 的场景
     */
    public static class LongBase<T extends Number> {
        private T id;
    }

    public static class LongBook extends LongBase<Long> {
    }

    // ==================== 测试用例 ====================

    @Test
    public void test_direct_generic_inheritance() {
        BeanMeta<Book> beanMeta = metaResolver.resolve(Book.class);
        FieldMeta idMeta = beanMeta.requireFieldMeta("id");
        // 未修复时返回 Number.class，修复后应返回 Integer.class
        Assertions.assertSame(Integer.class, idMeta.getType(),
                "id field from BaseEntity<Integer> should resolve to Integer, but got " + idMeta.getType());
        // name 字段非泛型继承，不受影响
        FieldMeta nameMeta = beanMeta.requireFieldMeta("name");
        Assertions.assertSame(String.class, nameMeta.getType());
        System.out.println("\ttest_direct_generic_inheritance ok!");
    }

    @Test
    public void test_multi_level_generic_inheritance() {
        BeanMeta<SubBook> beanMeta = metaResolver.resolve(SubBook.class);
        FieldMeta idMeta = beanMeta.requireFieldMeta("id");
        // SubBook → MiddleEntity<Integer> → BaseEntity2<Integer>
        Assertions.assertSame(Integer.class, idMeta.getType(),
                "id from multi-level generic should resolve to Integer, but got " + idMeta.getType());
        System.out.println("\ttest_multi_level_generic_inheritance ok!");
    }

    @Test
    public void test_non_generic_parent() {
        BeanMeta<User> beanMeta = metaResolver.resolve(User.class);
        // 非泛型父类的字段不应受影响
        FieldMeta idMeta = beanMeta.requireFieldMeta("id");
        Assertions.assertSame(long.class, idMeta.getType(),
                "id from non-generic parent should remain long.class");
        FieldMeta nameMeta = beanMeta.requireFieldMeta("name");
        Assertions.assertSame(String.class, nameMeta.getType());
        FieldMeta ageMeta = beanMeta.requireFieldMeta("age");
        Assertions.assertSame(int.class, ageMeta.getType());
        System.out.println("\ttest_non_generic_parent ok!");
    }

    @Test
    public void test_multiple_type_params() {
        BeanMeta<XmlBook> beanMeta = metaResolver.resolve(XmlBook.class);
        // id 为泛型 ID → Integer
        FieldMeta idMeta = beanMeta.requireFieldMeta("id");
        Assertions.assertSame(Integer.class, idMeta.getType(),
                "id from IdEntity<Integer, XmlBook> should resolve to Integer, but got " + idMeta.getType());
        // version 为非泛型 int，不应受影响
        FieldMeta versionMeta = beanMeta.requireFieldMeta("version");
        Assertions.assertSame(int.class, versionMeta.getType());
        System.out.println("\ttest_multiple_type_params ok!");
    }

    @Test
    public void test_same_class_no_generic() {
        BeanMeta<SelfBean> beanMeta = metaResolver.resolve(SelfBean.class);
        FieldMeta idMeta = beanMeta.requireFieldMeta("id");
        Assertions.assertSame(Integer.class, idMeta.getType(),
                "id declared in SelfBean itself should remain Integer.class");
        System.out.println("\ttest_same_class_no_generic ok!");
    }

    @Test
    public void test_string_type_variable() {
        BeanMeta<Concrete> beanMeta = metaResolver.resolve(Concrete.class);
        FieldMeta idMeta = beanMeta.requireFieldMeta("id");
        // Concrete → StringMiddle<String> → StringBase<String>
        Assertions.assertSame(String.class, idMeta.getType(),
                "id from StringBase<String> should resolve to String, but got " + idMeta.getType());
        System.out.println("\ttest_string_type_variable ok!");
    }

    @Test
    public void test_long_generic_resolution() {
        BeanMeta<LongBook> beanMeta = metaResolver.resolve(LongBook.class);
        FieldMeta idMeta = beanMeta.requireFieldMeta("id");
        Assertions.assertSame(Long.class, idMeta.getType(),
                "id from LongBase<Long> should resolve to Long, but got " + idMeta.getType());
        System.out.println("\ttest_long_generic_resolution ok!");
    }

    /**
     * 端到端测试：模拟 MariaDB 对 INT UNSIGNED 返回 Long，
     * 验证反射时能把 Long 正确转换为 Integer 并设置到实体字段中
     */
    @Test
    public void test_generic_field_reflect_long_to_integer() {
        BeanMeta<XmlBook> beanMeta = metaResolver.resolve(XmlBook.class);
        FieldMeta idMeta = beanMeta.requireFieldMeta("id");
        Assertions.assertSame(Integer.class, idMeta.getType(),
                "Pre-condition: id type should be resolved to Integer");

        DefaultBeanReflector reflector = new DefaultBeanReflector(List.of(
                new NumberFieldConvertor()
        ));

        // 模拟 MariaDB 对 INT UNSIGNED 返回 Long 值
        XmlBook book = reflector.reflect(beanMeta, beanMeta.getFieldMetas(),
                fieldAlias -> {
                    if (fieldAlias.equals(idMeta.getDbAlias())) {
                        return 42L;  // Long from DB
                    }
                    return null;
                });

        // 验证值已正确转换为 Integer
        try {
            java.lang.reflect.Field idField = XmlBook.class.getSuperclass()
                    .getDeclaredField("id");
            idField.setAccessible(true);
            Object actualValue = idField.get(book);
            Assertions.assertNotNull(actualValue, "id should not be null");
            Assertions.assertInstanceOf(Integer.class, actualValue, "id should be Integer after conversion, but got " + actualValue.getClass().getName() + ": " + actualValue);
            Assertions.assertEquals(42, ((Integer) actualValue).intValue());
        } catch (Exception e) {
            Assertions.fail("Failed to verify reflected value: " + e.getMessage());
        }
        System.out.println("\ttest_generic_field_reflect_long_to_integer ok!");
    }

    /**
     * 端到端测试：验证反射时不会将 Long 直接设置为字段值（回归测试）
     */
    @Test
    public void test_generic_field_reflect_prevents_long_direct_set() {
        BeanMeta<LongBook> beanMeta = metaResolver.resolve(LongBook.class);
        FieldMeta idMeta = beanMeta.requireFieldMeta("id");
        Assertions.assertSame(Long.class, idMeta.getType(),
                "Pre-condition: id type should be resolved to Long");

        DefaultBeanReflector reflector = new DefaultBeanReflector(List.of(
                new NumberFieldConvertor()
        ));

        // 同样传 Long 值，但这次字段类型也是 Long，应保持 Long
        LongBook book = reflector.reflect(beanMeta, beanMeta.getFieldMetas(),
                fieldAlias -> {
                    if (fieldAlias.equals(idMeta.getDbAlias())) {
                        return 100L;
                    }
                    return null;
                });

        try {
            java.lang.reflect.Field idField = LongBook.class.getSuperclass()
                    .getDeclaredField("id");
            idField.setAccessible(true);
            Object actualValue = idField.get(book);
            Assertions.assertNotNull(actualValue, "id should not be null");
            Assertions.assertInstanceOf(Long.class, actualValue, "id should be Long, but got " + actualValue.getClass().getName());
            Assertions.assertEquals(100L, actualValue);
        } catch (Exception e) {
            Assertions.fail("Failed to verify reflected value: " + e.getMessage());
        }
        System.out.println("\ttest_generic_field_reflect_prevents_long_direct_set ok!");
    }

}
