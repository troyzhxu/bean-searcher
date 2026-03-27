package cn.zhxu.bs.implement;

import cn.zhxu.bs.FieldMeta;
import cn.zhxu.bs.MetaResolver;
import cn.zhxu.bs.SearchParam;
import cn.zhxu.bs.group.Group;
import cn.zhxu.bs.operator.Between;
import cn.zhxu.bs.operator.InList;
import cn.zhxu.bs.param.FetchType;
import cn.zhxu.bs.param.FieldParam;
import cn.zhxu.bs.util.MapUtils;
import org.junit.jupiter.api.Test;

import java.util.*;

public class DefaultParamResolverTestCase {

    static final MetaResolver metaResolver = new DefaultMetaResolver();
    static final DefaultParamResolver resolver = new DefaultParamResolver();

    public static class User {
        private int age;

        public int getAge() {
            return age;
        }
    }

    @Test
    public void test_01() {
        Map<String, Object> params = MapUtils.builder()
                .put("age-0", 18)
                .put("age-1", 28)
                .field(User::getAge).op(Between.class)
                .build();
        SearchParam searchParam = resolver.resolve(metaResolver.resolve(User.class), new FetchType(FetchType.DEFAULT), params);

        System.out.println(searchParam);

        System.out.println("\ttest_01 ok!");
    }


    @Test
    public void test_02() {
        Map<String, Object> params = MapUtils.builder()
                .put("age-10", 31)
                .put("age-11", 20)
                .put("age-12", 30)
                .field(User::getAge, 30, 18, 28, 0, 6, 10, 8).op(InList.class)
                .field(User::getAge, 30, 18, 28, 0, 6, 10, 8).op(InList.class)
                .build();
        // 多线程只读共享模式
        Collection<FieldMeta> fieldMetas = metaResolver.resolve(User.class).getFieldMetas();
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            int index = i;
            threads.add(new Thread(() -> {
                Group<List<FieldParam>> group = resolver.resolveParamsGroup(fieldMetas, params);
                Object[] values = group.getValue().get(0).getValues();
                System.out.println("\t" + index + " - " + Arrays.toString(values));
            }));
        }
        threads.forEach(Thread::start);
        threads.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        System.out.println("\ttest_02 ok!");
    }


}
