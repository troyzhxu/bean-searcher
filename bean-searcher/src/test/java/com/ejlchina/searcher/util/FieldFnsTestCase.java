package com.ejlchina.searcher.util;

import org.junit.Assert;
import org.junit.Test;

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
    public void test() {
        Assert.assertEquals("name", FieldFns.name(User::getName));
        Assert.assertEquals("nickName", FieldFns.name(User::getNickName));
        Assert.assertEquals("active", FieldFns.name(User::isActive));
        Assert.assertEquals("accountLocked", FieldFns.name(User::isAccountLocked));

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
    }


}
