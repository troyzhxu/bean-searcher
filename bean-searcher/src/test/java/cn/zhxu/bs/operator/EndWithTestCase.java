package cn.zhxu.bs.operator;

import cn.zhxu.bs.FieldOp;
import cn.zhxu.bs.SqlWrapper;
import cn.zhxu.bs.dialect.MySqlDialect;
import cn.zhxu.bs.dialect.PostgreSqlDialect;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class EndWithTestCase {

    final EndWith endWith = new EndWith();

    @Test
    public void test_01() {
        Assertions.assertEquals("EndWith", endWith.name());
        Assertions.assertTrue(endWith.isNamed("ew"));
        Assertions.assertTrue(endWith.isNamed("EndWith"));
        Assertions.assertFalse(endWith.lonely());
        System.out.println("EndWithTestCase test_01 passed");
    }

    @Test
    public void test_02() {
        endWith.setDialect(new MySqlDialect());
        StringBuilder sb = new StringBuilder();
        List<Object> params = endWith.operate(sb, new FieldOp.OpPara(
                name -> new SqlWrapper<>("name"), false, new Object[]{ "abc" }
        ));
        Assertions.assertEquals("name like ?", sb.toString());
        Assertions.assertArrayEquals(new Object[] { "%abc" }, params.toArray());
        System.out.println("EndWithTestCase test_02 passed");
    }

    @Test
    public void test_03() {
        endWith.setDialect(new MySqlDialect());
        StringBuilder sb = new StringBuilder();
        List<Object> params = endWith.operate(sb, new FieldOp.OpPara(
                name -> new SqlWrapper<>("name"), true, new Object[]{ "abc" }
        ));
        Assertions.assertEquals("upper(name) like ?", sb.toString());
        Assertions.assertArrayEquals(new Object[] { "%ABC" }, params.toArray());
        System.out.println("EndWithTestCase test_03 passed");
    }

    @Test
    public void test_04() {
        endWith.setDialect(new MySqlDialect());
        StringBuilder sb = new StringBuilder();
        SqlWrapper<Object> fieldSql = new SqlWrapper<>("(select name from user where id = ?)");
        fieldSql.addPara(12);
        List<Object> params = endWith.operate(sb, new FieldOp.OpPara(
                name -> fieldSql, false, new Object[]{ "abc" }
        ));
        Assertions.assertEquals("(select name from user where id = ?) like ?", sb.toString());
        Assertions.assertArrayEquals(new Object[] { 12, "%abc" }, params.toArray());
        System.out.println("EndWithTestCase test_04 passed");
    }

    @Test
    public void test_05() {
        endWith.setDialect(new MySqlDialect());
        StringBuilder sb = new StringBuilder();
        SqlWrapper<Object> fieldSql = new SqlWrapper<>("(select name from user where id = ?)");
        fieldSql.addPara(12);
        List<Object> params = endWith.operate(sb, new FieldOp.OpPara(
                name -> fieldSql, true, new Object[]{ "abc" }
        ));
        Assertions.assertEquals("upper((select name from user where id = ?)) like ?", sb.toString());
        Assertions.assertArrayEquals(new Object[] { 12, "%ABC" }, params.toArray());
        System.out.println("EndWithTestCase test_05 passed");
    }

    @Test
    public void test_06() {
        endWith.setDialect(new PostgreSqlDialect());
        StringBuilder sb = new StringBuilder();
        List<Object> params = endWith.operate(sb, new FieldOp.OpPara(
                name -> new SqlWrapper<>("name"), false, new Object[]{ "abc" }
        ));
        Assertions.assertEquals("name like ?", sb.toString());
        Assertions.assertArrayEquals(new Object[] { "%abc" }, params.toArray());
        System.out.println("EndWithTestCase test_06 passed");
    }

    @Test
    public void test_07() {
        endWith.setDialect(new PostgreSqlDialect());
        StringBuilder sb = new StringBuilder();
        List<Object> params = endWith.operate(sb, new FieldOp.OpPara(
                name -> new SqlWrapper<>("name"), true, new Object[]{ "abc" }
        ));
        Assertions.assertEquals("name ilike ?", sb.toString());
        Assertions.assertArrayEquals(new Object[] { "%abc" }, params.toArray());
        System.out.println("EndWithTestCase test_07 passed");
    }

    @Test
    public void test_08() {
        endWith.setDialect(new PostgreSqlDialect());
        StringBuilder sb = new StringBuilder();
        SqlWrapper<Object> fieldSql = new SqlWrapper<>("(select name from user where id = ?)");
        fieldSql.addPara(12);
        List<Object> params = endWith.operate(sb, new FieldOp.OpPara(
                name -> fieldSql, false, new Object[] { "abc" }
        ));
        Assertions.assertEquals("(select name from user where id = ?) like ?", sb.toString());
        Assertions.assertArrayEquals(new Object[]{ 12, "%abc" }, params.toArray());
        System.out.println("EndWithTestCase test_08 passed");
    }

    @Test
    public void test_09() {
        endWith.setDialect(new PostgreSqlDialect());
        StringBuilder sb = new StringBuilder();
        SqlWrapper<Object> fieldSql = new SqlWrapper<>("(select name from user where id = ?)");
        fieldSql.addPara(12);
        List<Object> params = endWith.operate(sb, new FieldOp.OpPara(
                name -> fieldSql, true, new Object[] { "abc" }
        ));
        Assertions.assertEquals("(select name from user where id = ?) ilike ?", sb.toString());
        Assertions.assertArrayEquals(new Object[]{ 12, "%abc" }, params.toArray());
        System.out.println("EndWithTestCase test_09 passed");
    }

}
