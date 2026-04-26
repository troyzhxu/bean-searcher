package cn.zhxu.bs;

import cn.zhxu.bs.bean.Cluster;
import cn.zhxu.bs.bean.DbType;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.Map;

/**
 * 字段元信息
 */
public class FieldMeta {

    /**
     * 所属的 Bean 元信息
     */
    private final BeanMeta<?> beanMeta;

    /**
     * 字段名
     * @since v4.1.0
     */
    private final String name;

    /**
     * Java 字段
     */
    private final Field field;

    /**
     * 该字段对应的 SQL 片段
     */
    private final SqlSnippet fieldSql;

    /**
     * 该字段对应的 DB 字段别名
     */
    private final String dbAlias;

    /**
     * 该字段是否可作为检索参数
     */
    private final boolean conditional;

    /**
     * 该字段可作为检索时，被允许的运算符
     */
    private final Class<? extends FieldOp>[] onlyOn;

    /**
     * 数据库中该字段的类型，用于转换用户传入的检索参数值，为 {@link DbType#UNKNOWN } 时表示不需要转换
     * @since v3.8.0
     */
    private final DbType dbType;

    /**
     * 字段的聚合标志
     * @since v4.1.0
     */
    private final Cluster cluster;

    /**
     * 经过泛型解析后的字段类型缓存（v4.8.7+）
     */
    private transient volatile Class<?> resolvedType;

    public FieldMeta(BeanMeta<?> beanMeta, String name, Field field, SqlSnippet fieldSql, String dbAlias, boolean conditional,
                     Class<? extends FieldOp>[] onlyOn, DbType dbType, Cluster cluster) {
        this.beanMeta = beanMeta;
        this.name = name;
        this.field = field;
        this.fieldSql = fieldSql;
        this.dbAlias = dbAlias;
        this.conditional = conditional;
        this.onlyOn = onlyOn;
        this.dbType = dbType;
        this.cluster = cluster;
    }

    public BeanMeta<?> getBeanMeta() {
        return beanMeta;
    }

    public Field getField() {
        return field;
    }

    public boolean selectable() {
        return field != null;
    }

    public String getName() {
        return name;
    }

    public String fullName() {
        return beanMeta.getBeanClass().getName() + "#" + name;
    }

    public Class<?> getType() {
        if (field == null) {
            return null;
        }
        Class<?> result = resolvedType;
        if (result != null) {
            return result;
        }
        result = resolveGenericType();
        resolvedType = result;
        return result;
    }

    /**
     * 当字段从泛型父类继承时（如 Book extends IdDelAD&lt;Integer, Book&gt; 中的 id 字段），
     * 将擦除后的类型（Number）解析为子类声明的实际类型（Integer）。
     * @return 解析后的类型
     * @since v4.8.7
     */
    private Class<?> resolveGenericType() {
        Class<?> declaringClass = field.getDeclaringClass();
        // 如果字段声明类没有类型参数，无需解析
        if (declaringClass.getTypeParameters().length == 0) {
            return field.getType();
        }
        Class<?> beanClass = beanMeta.getBeanClass();
        // 如果字段就在当前类声明，无需解析
        if (beanClass == declaringClass) {
            return field.getType();
        }
        // 检查字段的泛型类型是否为类型变量
        Type genericType = field.getGenericType();
        if (!(genericType instanceof TypeVariable<?> typeVar)) {
            return field.getType();
        }
        // 沿着继承链向上解析：从 beanClass 遍历到 declaringClass
        Map<String, Type> typeMap = new HashMap<>();
        Type current = beanClass;
        while (current instanceof Class<?> currentClass) {
            if (currentClass.equals(declaringClass)) {
                // 已到达字段声明类，解析 TypeVariable
                Type resolved = typeVar;
                while (resolved instanceof TypeVariable) {
                    Type mapped = typeMap.get(((TypeVariable<?>) resolved).getName());
                    if (mapped == null) {
                        break;
                    }
                    resolved = mapped;
                }
                if (resolved instanceof Class) {
                    return (Class<?>) resolved;
                }
                if (resolved instanceof ParameterizedType) {
                    return (Class<?>) ((ParameterizedType) resolved).getRawType();
                }
                // 解析失败，返回擦除类型
                return field.getType();
            }
            Type superclass = currentClass.getGenericSuperclass();
            if (superclass instanceof ParameterizedType pt) {
                Class<?> rawClass = (Class<?>) pt.getRawType();
                TypeVariable<?>[] typeParams = rawClass.getTypeParameters();
                Type[] actualTypes = pt.getActualTypeArguments();
                for (int i = 0; i < typeParams.length; i++) {
                    Type actual = actualTypes[i];
                    // 如果实际参数本身也是类型变量，递归解析
                    while (actual instanceof TypeVariable) {
                        Type mapped = typeMap.get(((TypeVariable<?>) actual).getName());
                        if (mapped == null) {
                            break;
                        }
                        actual = mapped;
                    }
                    typeMap.put(typeParams[i].getName(), actual);
                }
                current = rawClass;
            } else if (superclass instanceof Class) {
                current = superclass;
            } else {
                break;
            }
        }
        // 未能解析，返回擦除类型
        return field.getType();
    }

    public SqlSnippet getFieldSql() {
        return fieldSql;
    }

    public String getDbAlias() {
        return dbAlias;
    }

    public boolean isConditional() {
        return conditional;
    }

    public Class<? extends FieldOp>[] getOnlyOn() {
        return onlyOn;
    }

    public DbType getDbType() {
        return dbType;
    }

    public Cluster getCluster() {
        return cluster;
    }

}
