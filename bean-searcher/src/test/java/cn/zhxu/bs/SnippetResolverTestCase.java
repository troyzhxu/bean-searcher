package cn.zhxu.bs;

import cn.zhxu.bs.implement.DefaultSnippetResolver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class SnippetResolverTestCase {

    final SnippetResolver resolver = new DefaultSnippetResolver();

    @Test
    public void test_01() {
        SqlSnippet snippet = resolver.resolve("name = ?");
        Assertions.assertEquals("name = ?", snippet.getSql());
        Assertions.assertTrue(snippet.getParas().isEmpty());
        System.out.println("\ttest_01 ok!");
    }

    @Test
    public void test_02() {
        SqlSnippet snippet = resolver.resolve(":name");
        Assertions.assertEquals("?", snippet.getSql());
        assert_02(snippet.getParas());

        snippet = resolver.resolve(" :name");
        Assertions.assertEquals(" ?", snippet.getSql());
        assert_02(snippet.getParas());

        snippet = resolver.resolve(":name ");
        Assertions.assertEquals("? ", snippet.getSql());
        assert_02(snippet.getParas());

        snippet = resolver.resolve(" :name ");
        Assertions.assertEquals(" ? ", snippet.getSql());
        assert_02(snippet.getParas());

        snippet = resolver.resolve("name = :name");
        Assertions.assertEquals("name = ?", snippet.getSql());
        assert_02(snippet.getParas());

        snippet = resolver.resolve("name=:name");
        Assertions.assertEquals("name=?", snippet.getSql());
        assert_02(snippet.getParas());

        snippet = resolver.resolve(":name = name");
        Assertions.assertEquals("? = name", snippet.getSql());
        assert_02(snippet.getParas());

        snippet = resolver.resolve(":name=name");
        Assertions.assertEquals("?=name", snippet.getSql());
        assert_02(snippet.getParas());

        snippet = resolver.resolve(":name>name");
        Assertions.assertEquals("?>name", snippet.getSql());
        assert_02(snippet.getParas());

        snippet = resolver.resolve("name>:name");
        Assertions.assertEquals("name>?", snippet.getSql());
        assert_02(snippet.getParas());

        snippet = resolver.resolve(":name>=name");
        Assertions.assertEquals("?>=name", snippet.getSql());
        assert_02(snippet.getParas());

        snippet = resolver.resolve("name>=:name");
        Assertions.assertEquals("name>=?", snippet.getSql());
        assert_02(snippet.getParas());

        snippet = resolver.resolve(":name<name");
        Assertions.assertEquals("?<name", snippet.getSql());
        assert_02(snippet.getParas());

        snippet = resolver.resolve("name<:name");
        Assertions.assertEquals("name<?", snippet.getSql());
        assert_02(snippet.getParas());

        snippet = resolver.resolve(":name<=name");
        Assertions.assertEquals("?<=name", snippet.getSql());
        assert_02(snippet.getParas());

        snippet = resolver.resolve("name<=:name");
        Assertions.assertEquals("name<=?", snippet.getSql());
        assert_02(snippet.getParas());
        System.out.println("\ttest_02 ok!");
    }

    private void assert_02(List<SqlSnippet.SqlPara> paras) {
        Assertions.assertEquals(1, paras.size());
        SqlSnippet.SqlPara para = paras.get(0);
        Assertions.assertEquals("name", para.getName());
        Assertions.assertEquals(":name", para.getSqlName());
        Assertions.assertTrue(para.isJdbcPara());
    }

    @Test
    public void test_03() {
        SqlSnippet snippet = resolver.resolve(":name:");
        Assertions.assertEquals(":name:", snippet.getSql());
        assert_03(snippet.getParas());

        snippet = resolver.resolve(" :name:");
        Assertions.assertEquals(" :name:", snippet.getSql());
        assert_03(snippet.getParas());

        snippet = resolver.resolve(":name: ");
        Assertions.assertEquals(":name: ", snippet.getSql());
        assert_03(snippet.getParas());

        snippet = resolver.resolve("name = :name:");
        Assertions.assertEquals("name = :name:", snippet.getSql());
        assert_03(snippet.getParas());

        snippet = resolver.resolve("name=:name:");
        Assertions.assertEquals("name=:name:", snippet.getSql());
        assert_03(snippet.getParas());

        snippet = resolver.resolve(":name: = name");
        Assertions.assertEquals(":name: = name", snippet.getSql());
        assert_03(snippet.getParas());

        snippet = resolver.resolve(":name:=name");
        Assertions.assertEquals(":name:=name", snippet.getSql());
        assert_03(snippet.getParas());

        snippet = resolver.resolve(":name:>name");
        Assertions.assertEquals(":name:>name", snippet.getSql());
        assert_03(snippet.getParas());

        snippet = resolver.resolve(":name:<name");
        Assertions.assertEquals(":name:<name", snippet.getSql());
        assert_03(snippet.getParas());

        snippet = resolver.resolve("name>:name:");
        Assertions.assertEquals("name>:name:", snippet.getSql());
        assert_03(snippet.getParas());

        snippet = resolver.resolve("name<:name:");
        Assertions.assertEquals("name<:name:", snippet.getSql());
        assert_03(snippet.getParas());
        System.out.println("\ttest_03 ok!");
    }

    private void assert_03(List<SqlSnippet.SqlPara> paras) {
        Assertions.assertEquals(1, paras.size());
        SqlSnippet.SqlPara para = paras.get(0);
        Assertions.assertEquals("name", para.getName());
        Assertions.assertEquals(":name:", para.getSqlName());
        Assertions.assertFalse(para.isJdbcPara());
    }

    @Test
    public void test_04() {
        SqlSnippet snippet = resolver.resolve("json_param\\:\\:jsonb->>name");
        Assertions.assertEquals("json_param::jsonb->>name", snippet.getSql());
        Assertions.assertTrue(snippet.getParas().isEmpty());
        System.out.println("\ttest_04 ok!");
    }

    @Test
    public void test_05() {
        SqlSnippet snippet = resolver.resolve("\\:name");
        Assertions.assertEquals(":name", snippet.getSql());
        Assertions.assertTrue(snippet.getParas().isEmpty());
        System.out.println("\ttest_05 ok!");
    }

}
