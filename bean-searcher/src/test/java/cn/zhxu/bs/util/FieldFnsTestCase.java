package cn.zhxu.bs.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FieldFnsTestCase {

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
        Assertions.assertEquals("name", FieldFns.name(User::getName));
        Assertions.assertEquals("nickName", FieldFns.name(User::getNickName));
        Assertions.assertEquals("active", FieldFns.name(User::isActive));
        Assertions.assertEquals("accountLocked", FieldFns.name(User::isAccountLocked));
        long t0 = System.currentTimeMillis();
        for (int i = 0; i < 10000000; i++) {
            FieldFns.name(User::getId);
            FieldFns.name(User::getName);
            FieldFns.name(User::getNickName);
            FieldFns.name(User::isActive);
            FieldFns.name(User::isAccountLocked);
        }
        long t = System.currentTimeMillis() - t0;
        System.out.println("耗时：" + t);
        System.out.println("\ttest_01 ok!");
    }


}
