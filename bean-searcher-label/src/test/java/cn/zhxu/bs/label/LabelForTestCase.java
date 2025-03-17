package cn.zhxu.bs.label;

import cn.zhxu.bs.IllegalParamException;
import cn.zhxu.bs.bean.DbIgnore;
import cn.zhxu.bs.util.AnnoUtils;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

public class LabelForTestCase {

    public static class User {
        private int id;
        private int roleId;

        @LabelFor("roleId")
        private String roleName;

        public int getRoleId() {
            return roleId;
        }
    }


    @Test
    public void test_01() throws IllegalParamException, NoSuchFieldException {
        Field field = User.class.getDeclaredField("roleName");

        boolean anno = AnnoUtils.isAnnotated(field, DbIgnore.class);


        System.out.println("dbIgnore = " + anno);


        System.out.println("\ttest_page_01 ok!");
    }

}
