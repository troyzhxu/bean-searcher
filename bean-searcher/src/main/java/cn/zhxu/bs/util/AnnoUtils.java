package cn.zhxu.bs.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.List;

/**
 * 注解工具类，可注解组合注解
 * @since v4.4.0
 */
public class AnnoUtils {

    /**
     * 判断是否被指定注解标注，包括组合注解
     * @param element 元素
     * @param annoClass 注解类
     * @return 是否被指定注解标注
     */
    public static boolean isAnnotated(AnnotatedElement element, Class<? extends Annotation> annoClass) {
        return getAnnotation(element, annoClass) != null;
    }

    /**
     * 获取指定注解标注，包括组合注解
     * @param element 元素
     * @param annoClass 注解类
     * @return 指定注解
     */
    public static <T extends Annotation> T getAnnotation(AnnotatedElement element, Class<T> annoClass) {
        if (element == null) {
            return null;
        }
        T annotation = element.getAnnotation(annoClass);
        if (annotation != null) {
            return annotation;
        }
        return getAnnotation(element, annoClass, new ArrayList<>());
    }

    @SuppressWarnings("unchecked")
    private static <T extends Annotation> T getAnnotation(AnnotatedElement element, Class<T> annoClass, List<Annotation> checked) {
        for (Annotation anno : element.getAnnotations()) {
            if (checked.contains(anno)) {
                continue;
            }
            if (annoClass.isInstance(anno)) {
                return (T) anno;
            }
            checked.add(anno);
            T ann = getAnnotation(anno.annotationType(), annoClass, checked);
            if (ann != null) {
                return ann;
            }
        }
        return null;
    }

}
