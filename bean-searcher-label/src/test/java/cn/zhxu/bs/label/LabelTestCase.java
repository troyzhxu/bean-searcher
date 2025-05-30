package cn.zhxu.bs.label;

import cn.zhxu.bs.BeanMeta;
import cn.zhxu.bs.IllegalParamException;
import cn.zhxu.bs.bean.DbIgnore;
import cn.zhxu.bs.implement.DefaultMetaResolver;
import cn.zhxu.bs.util.AnnoUtils;
import cn.zhxu.bs.util.MapUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class LabelTestCase {

    public static class User {
        private int id;
        private String code;
        public User(int id, String code) {
            this.id = id;
            this.code = code;
        }
        @LabelFor("id")
        private String name;
        @LabelFor(value = "id", key = "name")
        private String nickname;
        @LabelFor(value = "code", key = "status")
        private String codeName;
        @LabelFor(value = "id")
        private String status;
    }

    @Test
    public void test_01() throws IllegalParamException, NoSuchFieldException {
        Field field = User.class.getDeclaredField("name");
        Assertions.assertTrue(AnnoUtils.isAnnotated(field, DbIgnore.class));
        System.out.println("\ttest_01 ok!");
    }

    @Test
    public void test_02() throws IllegalParamException {
        BeanMeta<User> beanMeta = new DefaultMetaResolver().resolve(User.class);
        Set<String> fieldSet = beanMeta.getFieldSet();
        Assertions.assertEquals(2, fieldSet.size());
        Assertions.assertTrue(fieldSet.contains("id"));
        Assertions.assertTrue(fieldSet.contains("code"));
        System.out.println("\ttest_02 ok!");
    }

    LabelLoader<Integer> nameLoader = new LabelLoader<Integer>() {
        @Override
        public boolean supports(String key) {
            return "name".equals(key);
        }

        final List<Label<Integer>> list = Arrays.asList(
                new Label<>(1, "张三"),
                new Label<>(2, "李四"),
                new Label<>(3, "王五"),
                new Label<>(4, "孙六")
        );

        @Override
        public List<Label<Integer>> load(String key, List<Integer> ids) {
            Assertions.assertEquals("name", key);
            return list.stream().filter(label -> ids.contains(label.getId())).collect(Collectors.toList());
        }
    };

    LabelLoader<String> statusLoader = new LabelLoader<String>() {
        @Override
        public boolean supports(String key) {
            return "status".equals(key);
        }
        final List<Label<String>> list = Arrays.asList(
                new Label<>("A", "待审核"),
                new Label<>("B", "审核中"),
                new Label<>("C", "审核通过"),
                new Label<>("D", "未通过")
        );
        @Override
        public List<Label<String>> load(String key, List<String> ids) {
            Assertions.assertEquals("status", key);
            return list.stream().filter(label -> ids.contains(label.getId())).collect(Collectors.toList());
        }
    };

    LabelLoader<Integer> intStatusLoader = new LabelLoader<Integer>() {
        @Override
        public boolean supports(String key) {
            return "status".equals(key);
        }
        final List<Label<Integer>> list = Arrays.asList(
                new Label<>(1, "张"),
                new Label<>(2, "李"),
                new Label<>(3, "王"),
                new Label<>(4, "孙")
        );
        @Override
        public List<Label<Integer>> load(String key, List<Integer> ids) {
            Assertions.assertEquals("status", key);
            return list.stream().filter(label -> ids.contains(label.getId())).collect(Collectors.toList());
        }
    };

    @Test
    public void test_03() throws IllegalParamException {
        LabelResultFilter resultFilter = new LabelResultFilter();
        resultFilter.addLabelLoader(nameLoader);
        resultFilter.addLabelLoader(statusLoader);
        resultFilter.addLabelLoader(intStatusLoader);
        List<User> dataList = Arrays.asList(
                new User(1, "A"),
                new User(2, "B"),
                new User(3, "C"),
                new User(4, "D"),
                new User(1, "B")
        );
        resultFilter.processDataList(User.class, dataList);
        Assertions.assertEquals("张三", dataList.get(0).name);
        Assertions.assertEquals("李四", dataList.get(1).name);
        Assertions.assertEquals("王五", dataList.get(2).name);
        Assertions.assertEquals("孙六", dataList.get(3).name);
        Assertions.assertEquals("张三", dataList.get(4).name);

        Assertions.assertEquals("张三", dataList.get(0).nickname);
        Assertions.assertEquals("李四", dataList.get(1).nickname);
        Assertions.assertEquals("王五", dataList.get(2).nickname);
        Assertions.assertEquals("孙六", dataList.get(3).nickname);
        Assertions.assertEquals("张三", dataList.get(4).nickname);

        Assertions.assertEquals("待审核", dataList.get(0).codeName);
        Assertions.assertEquals("审核中", dataList.get(1).codeName);
        Assertions.assertEquals("审核通过", dataList.get(2).codeName);
        Assertions.assertEquals("未通过", dataList.get(3).codeName);
        Assertions.assertEquals("审核中", dataList.get(4).codeName);

        Assertions.assertEquals("张", dataList.get(0).status);
        Assertions.assertEquals("李", dataList.get(1).status);
        Assertions.assertEquals("王", dataList.get(2).status);
        Assertions.assertEquals("孙", dataList.get(3).status);
        Assertions.assertEquals("张", dataList.get(4).status);

        System.out.println("\ttest_03 ok!");
    }


    @Test
    public void test_04() throws IllegalParamException {
        LabelResultFilter resultFilter = new LabelResultFilter();
        resultFilter.addLabelLoader(nameLoader);
        resultFilter.addLabelLoader(statusLoader);
        resultFilter.addLabelLoader(intStatusLoader);
        List<Map<String, Object>> dataList = Arrays.asList(
                MapUtils.of("id", 1, "code", "A"),
                MapUtils.of("id", 2, "code", "B"),
                MapUtils.of("id", 3, "code", "C"),
                MapUtils.of("id", 4, "code", "D"),
                MapUtils.of("id", 1, "code", "B")
        );
        resultFilter.processDataList(User.class, dataList);
        Assertions.assertEquals("张三", dataList.get(0).get("name"));
        Assertions.assertEquals("李四", dataList.get(1).get("name"));
        Assertions.assertEquals("王五", dataList.get(2).get("name"));
        Assertions.assertEquals("孙六", dataList.get(3).get("name"));
        Assertions.assertEquals("张三", dataList.get(4).get("name"));

        Assertions.assertEquals("张三", dataList.get(0).get("nickname"));
        Assertions.assertEquals("李四", dataList.get(1).get("nickname"));
        Assertions.assertEquals("王五", dataList.get(2).get("nickname"));
        Assertions.assertEquals("孙六", dataList.get(3).get("nickname"));
        Assertions.assertEquals("张三", dataList.get(4).get("nickname"));

        Assertions.assertEquals("待审核", dataList.get(0).get("codeName"));
        Assertions.assertEquals("审核中", dataList.get(1).get("codeName"));
        Assertions.assertEquals("审核通过", dataList.get(2).get("codeName"));
        Assertions.assertEquals("未通过", dataList.get(3).get("codeName"));
        Assertions.assertEquals("审核中", dataList.get(4).get("codeName"));

        Assertions.assertEquals("张", dataList.get(0).get("status"));
        Assertions.assertEquals("李", dataList.get(1).get("status"));
        Assertions.assertEquals("王", dataList.get(2).get("status"));
        Assertions.assertEquals("孙", dataList.get(3).get("status"));
        Assertions.assertEquals("张", dataList.get(4).get("status"));
        System.out.println("\ttest_04 ok!");
    }

}
