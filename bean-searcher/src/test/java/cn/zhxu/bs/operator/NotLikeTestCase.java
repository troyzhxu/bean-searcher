package cn.zhxu.bs.operator;

import cn.zhxu.bs.FieldOp;
import cn.zhxu.bs.SqlWrapper;
import cn.zhxu.bs.dialect.MySqlDialect;
import cn.zhxu.bs.dialect.PostgreSqlDialect;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class NotLikeTestCase {

    final NotLike notLike = new NotLike();

    @Test
    public void test_01() {
        Assertions.assertEquals("NotLike", notLike.name());
        Assertions.assertTrue(notLike.isNamed("nk"));
        Assertions.assertTrue(notLike.isNamed("NotLike"));
        Assertions.assertFalse(notLike.lonely());
        System.out.println("\ttest_01 passed");
    }

    @Test
    public void test_02() {
        notLike.setDialect(new MySqlDialect());
        StringBuilder sb = new StringBuilder();
        List<Object> params = notLike.operate(sb, new FieldOp.OpPara(
                name -> new SqlWrapper<>("name"), false, new Object[]{ "abc%" }
        ));
        Assertions.assertEquals("name not like ?", sb.toString());
        Assertions.assertArrayEquals(new Object[] { "abc%" }, params.toArray());
        System.out.println("\ttest_02 passed");
    }

    @Test
    public void test_03() {
        notLike.setDialect(new MySqlDialect());
        StringBuilder sb = new StringBuilder();
        List<Object> params = notLike.operate(sb, new FieldOp.OpPara(
                name -> new SqlWrapper<>("name"), true, new Object[]{ "abc%" }
        ));
        Assertions.assertEquals("upper(name) not like ?", sb.toString());
        Assertions.assertArrayEquals(new Object[] { "ABC%" }, params.toArray());
        System.out.println("\ttest_03 passed");
    }

    @Test
    public void test_04() {
        notLike.setDialect(new MySqlDialect());
        StringBuilder sb = new StringBuilder();
        SqlWrapper<Object> fieldSql = new SqlWrapper<>("(select name from user where id = ?)");
        fieldSql.addPara(12);
        List<Object> params = notLike.operate(sb, new FieldOp.OpPara(
                name -> fieldSql, false, new Object[]{ "abc%" }
        ));
        Assertions.assertEquals("(select name from user where id = ?) not like ?", sb.toString());
        Assertions.assertArrayEquals(new Object[] { 12, "abc%" }, params.toArray());
        System.out.println("\ttest_04 passed");
    }

    @Test
    public void test_05() {
        notLike.setDialect(new MySqlDialect());
        StringBuilder sb = new StringBuilder();
        SqlWrapper<Object> fieldSql = new SqlWrapper<>("(select name from user where id = ?)");
        fieldSql.addPara(12);
        List<Object> params = notLike.operate(sb, new FieldOp.OpPara(
                name -> fieldSql, true, new Object[]{ "abc%" }
        ));
        Assertions.assertEquals("upper((select name from user where id = ?)) not like ?", sb.toString());
        Assertions.assertArrayEquals(new Object[] { 12, "ABC%" }, params.toArray());
        System.out.println("\ttest_05 passed");
    }

    @Test
    public void test_06() {
        notLike.setDialect(new PostgreSqlDialect());
        StringBuilder sb = new StringBuilder();
        List<Object> params = notLike.operate(sb, new FieldOp.OpPara(
                name -> new SqlWrapper<>("name"), false, new Object[]{ "abc%" }
        ));
        Assertions.assertEquals("name not like ?", sb.toString());
        Assertions.assertArrayEquals(new Object[] { "abc%" }, params.toArray());
        System.out.println("\ttest_06 passed");
    }

    @Test
    public void test_07() {
        notLike.setDialect(new PostgreSqlDialect());
        StringBuilder sb = new StringBuilder();
        List<Object> params = notLike.operate(sb, new FieldOp.OpPara(
                name -> new SqlWrapper<>("name"), true, new Object[]{ "abc%" }
        ));
        Assertions.assertEquals("name not ilike ?", sb.toString());
        Assertions.assertArrayEquals(new Object[] { "abc%" }, params.toArray());
        System.out.println("\ttest_07 passed");
    }

    @Test
    public void test_08() {
        notLike.setDialect(new PostgreSqlDialect());
        StringBuilder sb = new StringBuilder();
        SqlWrapper<Object> fieldSql = new SqlWrapper<>("(select name from user where id = ?)");
        fieldSql.addPara(12);
        List<Object> params = notLike.operate(sb, new FieldOp.OpPara(
                name -> fieldSql, false, new Object[] { "abc%" }
        ));
        Assertions.assertEquals("(select name from user where id = ?) not like ?", sb.toString());
        Assertions.assertArrayEquals(new Object[]{ 12, "abc%" }, params.toArray());
        System.out.println("\ttest_08 passed");
    }

    @Test
    public void test_09() {
        notLike.setDialect(new PostgreSqlDialect());
        StringBuilder sb = new StringBuilder();
        SqlWrapper<Object> fieldSql = new SqlWrapper<>("(select name from user where id = ?)");
        fieldSql.addPara(12);
        List<Object> params = notLike.operate(sb, new FieldOp.OpPara(
                name -> fieldSql, true, new Object[] { "abc%" }
        ));
        Assertions.assertEquals("(select name from user where id = ?) not ilike ?", sb.toString());
        Assertions.assertArrayEquals(new Object[]{ 12, "abc%" }, params.toArray());
        System.out.println("\ttest_09 passed");
    }

}
