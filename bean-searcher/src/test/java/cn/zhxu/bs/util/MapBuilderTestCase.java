package cn.zhxu.bs.util;

import org.junit.Assert;
import org.junit.Test;

import java.util.Map;
import java.util.Set;

public class MapBuilderTestCase {

    public static class User {
        private int id;
        private String name;
        private String nickName;
        private boolean active;
        private boolean accountLocked;
        public int getId() {
            return id;
        }
        public String getName() {
            return name;
        }
        public String getNickName() {
            return nickName;
        }
        public boolean isActive() {
            return active;
        }
        public boolean isAccountLocked() {
            return accountLocked;
        }
    }

    @Test
    public void test_01() {
        Set<String> keys = MapUtils.builder()
                .field(FieldFnsTestCase.User::getId)
                .field(FieldFnsTestCase.User::getName)
                .field(FieldFnsTestCase.User::getNickName)
                .field(FieldFnsTestCase.User::isActive)
                .field(FieldFnsTestCase.User::isAccountLocked)
                .build()
                .keySet();
        Assert.assertTrue(keys.contains(MapBuilder.FIELD_PARAM + "id"));
        Assert.assertTrue(keys.contains(MapBuilder.FIELD_PARAM + "name"));
        Assert.assertTrue(keys.contains(MapBuilder.FIELD_PARAM + "nickName"));
        Assert.assertTrue(keys.contains(MapBuilder.FIELD_PARAM + "active"));
        Assert.assertTrue(keys.contains(MapBuilder.FIELD_PARAM + "accountLocked"));
    }

    @Test
    public void test_02() {
        Map<String, Object> params = MapUtils.builder()
                .field("id", 1)                             // $
                .field("nickName", "You")                   // $
                .or(b -> {
                    b.field("name", "Jack");                // A
                    b.field("name", "Tom");                 // B
                    b.and(c -> {
                        c.field("active", true);            // C
                        c.field("accountLocked", false);    // C
                    });
                })
                .build();
        // $&(A|B|C)
    }

}
