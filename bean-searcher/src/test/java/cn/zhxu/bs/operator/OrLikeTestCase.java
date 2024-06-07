package cn.zhxu.bs.operator;

import cn.zhxu.bs.FieldOp;
import cn.zhxu.bs.SqlWrapper;
import cn.zhxu.bs.dialect.MySqlDialect;
import cn.zhxu.bs.dialect.PostgreSqlDialect;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class OrLikeTestCase {

    final OrLike orLike = new OrLike();

    @Test
    public void test_01() {
        Assertions.assertEquals("OrLike", orLike.name());
        Assertions.assertTrue(orLike.isNamed("ol"));
        Assertions.assertTrue(orLike.isNamed("OrLike"));
        Assertions.assertFalse(orLike.lonely());
        System.out.println("OrLikeTestCase test_01 passed");
    }

    @Test
    public void test_02() {
        orLike.setDialect(new MySqlDialect());
        StringBuilder sb = new StringBuilder();
        List<Object> params = orLike.operate(sb, new FieldOp.OpPara(
                name -> new SqlWrapper<>("name"), false, new Object[]{ "a", "B", "c" }
        ));
        Assertions.assertEquals("name like ? or name like ? or name like ?", sb.toString());
        Assertions.assertArrayEquals(new Object[]{ "a", "B", "c" }, params.toArray());
        System.out.println("OrLikeTestCase test_02 passed");
    }

    @Test
    public void test_03() {
        orLike.setDialect(new MySqlDialect());
        StringBuilder sb = new StringBuilder();
        List<Object> params = orLike.operate(sb, new FieldOp.OpPara(
                name -> new SqlWrapper<>("name"), true, new Object[]{ "a", "B", "c" }
        ));
        Assertions.assertEquals("upper(name) like ? or upper(name) like ? or upper(name) like ?", sb.toString());
        Assertions.assertArrayEquals(new Object[]{ "A", "B", "C" }, params.toArray());
        System.out.println("OrLikeTestCase test_03 passed");
    }

    @Test
    public void test_04() {
        orLike.setDialect(new MySqlDialect());
        StringBuilder sb = new StringBuilder();
        SqlWrapper<Object> fieldSql = new SqlWrapper<>("(select name from user where id = ?)");
        fieldSql.addPara(12);
        List<Object> params = orLike.operate(sb, new FieldOp.OpPara(
                name -> fieldSql, false, new Object[]{ "a", "B", "c" }
        ));
        Assertions.assertEquals("(select name from user where id = ?) like ? or " +
                "(select name from user where id = ?) like ? or " +
                "(select name from user where id = ?) like ?", sb.toString());
        Assertions.assertArrayEquals(new Object[]{ 12, "a", 12, "B", 12, "c" }, params.toArray());
        System.out.println("OrLikeTestCase test_04 passed");
    }

    @Test
    public void test_05() {
        orLike.setDialect(new MySqlDialect());
        StringBuilder sb = new StringBuilder();
        SqlWrapper<Object> fieldSql = new SqlWrapper<>("(select name from user where id = ?)");
        fieldSql.addPara(12);
        List<Object> params = orLike.operate(sb, new FieldOp.OpPara(
                name -> fieldSql, true, new Object[]{ "a", "B", "c" }
        ));
        Assertions.assertEquals("upper((select name from user where id = ?)) like ? or " +
                "upper((select name from user where id = ?)) like ? or " +
                "upper((select name from user where id = ?)) like ?", sb.toString());
        Assertions.assertArrayEquals(new Object[]{ 12, "A", 12, "B", 12, "C" }, params.toArray());
        System.out.println("OrLikeTestCase test_05 passed");
    }

    @Test
    public void test_06() {
        orLike.setDialect(new PostgreSqlDialect());
        StringBuilder sb = new StringBuilder();
        List<Object> params = orLike.operate(sb, new FieldOp.OpPara(
                name -> new SqlWrapper<>("name"), false, new Object[]{ "a", "B", "c" }
        ));
        Assertions.assertEquals("name like ? or name like ? or name like ?", sb.toString());
        Assertions.assertArrayEquals(new Object[]{ "a", "B", "c" }, params.toArray());
        System.out.println("OrLikeTestCase test_06 passed");
    }

    @Test
    public void test_07() {
        orLike.setDialect(new PostgreSqlDialect());
        StringBuilder sb = new StringBuilder();
        List<Object> params = orLike.operate(sb, new FieldOp.OpPara(
                name -> new SqlWrapper<>("name"), true, new Object[]{ "a", "B", "c" }
        ));
        Assertions.assertEquals("name ilike ? or name ilike ? or name ilike ?", sb.toString());
        Assertions.assertArrayEquals(new Object[]{ "a", "B", "c" }, params.toArray());
        System.out.println("OrLikeTestCase test_07 passed");
    }

    @Test
    public void test_08() {
        orLike.setDialect(new PostgreSqlDialect());
        StringBuilder sb = new StringBuilder();
        SqlWrapper<Object> fieldSql = new SqlWrapper<>("(select name from user where id = ?)");
        fieldSql.addPara(12);
        List<Object> params = orLike.operate(sb, new FieldOp.OpPara(
                name -> fieldSql, false, new Object[]{ "a", "B", "c" }
        ));
        Assertions.assertEquals("(select name from user where id = ?) like ? or " +
                "(select name from user where id = ?) like ? or " +
                "(select name from user where id = ?) like ?", sb.toString());
        Assertions.assertArrayEquals(new Object[]{ 12, "a", 12, "B", 12, "c" }, params.toArray());
        System.out.println("OrLikeTestCase test_08 passed");
    }

    @Test
    public void test_09() {
        orLike.setDialect(new PostgreSqlDialect());
        StringBuilder sb = new StringBuilder();
        SqlWrapper<Object> fieldSql = new SqlWrapper<>("(select name from user where id = ?)");
        fieldSql.addPara(12);
        List<Object> params = orLike.operate(sb, new FieldOp.OpPara(
                name -> fieldSql, true, new Object[]{ "a", "B", "c" }
        ));
        Assertions.assertEquals("(select name from user where id = ?) ilike ? or " +
                "(select name from user where id = ?) ilike ? or " +
                "(select name from user where id = ?) ilike ?", sb.toString());
        Assertions.assertArrayEquals(new Object[]{ 12, "a", 12, "B", 12, "c" }, params.toArray());
        System.out.println("OrLikeTestCase test_09 passed");
    }

}
