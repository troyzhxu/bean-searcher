package cn.zhxu.bs;

import cn.zhxu.bs.implement.DefaultSnippetResolver;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class SnippetResolverTestCase {

    final SnippetResolver resolver = new DefaultSnippetResolver();

    @Test
    public void test_01() {
        SqlSnippet snippet = resolver.resolve("name = ?");
        Assert.assertEquals("name = ?", snippet.getSql());
        Assert.assertTrue(snippet.getParas().isEmpty());
    }

    @Test
    public void test_02() {
        SqlSnippet snippet = resolver.resolve(":name");
        Assert.assertEquals("?", snippet.getSql());
        assert_02(snippet.getParas());

        snippet = resolver.resolve(" :name");
        Assert.assertEquals(" ?", snippet.getSql());
        assert_02(snippet.getParas());

        snippet = resolver.resolve(":name ");
        Assert.assertEquals("? ", snippet.getSql());
        assert_02(snippet.getParas());

        snippet = resolver.resolve(" :name ");
        Assert.assertEquals(" ? ", snippet.getSql());
        assert_02(snippet.getParas());

        snippet = resolver.resolve("name = :name");
        Assert.assertEquals("name = ?", snippet.getSql());
        assert_02(snippet.getParas());

        snippet = resolver.resolve("name=:name");
        Assert.assertEquals("name=?", snippet.getSql());
        assert_02(snippet.getParas());

        snippet = resolver.resolve(":name = name");
        Assert.assertEquals("? = name", snippet.getSql());
        assert_02(snippet.getParas());

        snippet = resolver.resolve(":name=name");
        Assert.assertEquals("?=name", snippet.getSql());
        assert_02(snippet.getParas());

        snippet = resolver.resolve(":name>name");
        Assert.assertEquals("?>name", snippet.getSql());
        assert_02(snippet.getParas());

        snippet = resolver.resolve("name>:name");
        Assert.assertEquals("name>?", snippet.getSql());
        assert_02(snippet.getParas());

        snippet = resolver.resolve(":name>=name");
        Assert.assertEquals("?>=name", snippet.getSql());
        assert_02(snippet.getParas());

        snippet = resolver.resolve("name>=:name");
        Assert.assertEquals("name>=?", snippet.getSql());
        assert_02(snippet.getParas());

        snippet = resolver.resolve(":name<name");
        Assert.assertEquals("?<name", snippet.getSql());
        assert_02(snippet.getParas());

        snippet = resolver.resolve("name<:name");
        Assert.assertEquals("name<?", snippet.getSql());
        assert_02(snippet.getParas());

        snippet = resolver.resolve(":name<=name");
        Assert.assertEquals("?<=name", snippet.getSql());
        assert_02(snippet.getParas());

        snippet = resolver.resolve("name<=:name");
        Assert.assertEquals("name<=?", snippet.getSql());
        assert_02(snippet.getParas());
    }

    private void assert_02(List<SqlSnippet.SqlPara> paras) {
        Assert.assertEquals(1, paras.size());
        SqlSnippet.SqlPara para = paras.get(0);
        Assert.assertEquals("name", para.getName());
        Assert.assertEquals(":name", para.getSqlName());
        Assert.assertTrue(para.isJdbcPara());
    }

    @Test
    public void test_03() {
        SqlSnippet snippet = resolver.resolve(":name:");
        Assert.assertEquals(":name:", snippet.getSql());
        assert_03(snippet.getParas());

        snippet = resolver.resolve(" :name:");
        Assert.assertEquals(" :name:", snippet.getSql());
        assert_03(snippet.getParas());

        snippet = resolver.resolve(":name: ");
        Assert.assertEquals(":name: ", snippet.getSql());
        assert_03(snippet.getParas());

        snippet = resolver.resolve("name = :name:");
        Assert.assertEquals("name = :name:", snippet.getSql());
        assert_03(snippet.getParas());

        snippet = resolver.resolve("name=:name:");
        Assert.assertEquals("name=:name:", snippet.getSql());
        assert_03(snippet.getParas());

        snippet = resolver.resolve(":name: = name");
        Assert.assertEquals(":name: = name", snippet.getSql());
        assert_03(snippet.getParas());

        snippet = resolver.resolve(":name:=name");
        Assert.assertEquals(":name:=name", snippet.getSql());
        assert_03(snippet.getParas());

        snippet = resolver.resolve(":name:>name");
        Assert.assertEquals(":name:>name", snippet.getSql());
        assert_03(snippet.getParas());

        snippet = resolver.resolve(":name:<name");
        Assert.assertEquals(":name:<name", snippet.getSql());
        assert_03(snippet.getParas());

        snippet = resolver.resolve("name>:name:");
        Assert.assertEquals("name>:name:", snippet.getSql());
        assert_03(snippet.getParas());

        snippet = resolver.resolve("name<:name:");
        Assert.assertEquals("name<:name:", snippet.getSql());
        assert_03(snippet.getParas());
    }

    private void assert_03(List<SqlSnippet.SqlPara> paras) {
        Assert.assertEquals(1, paras.size());
        SqlSnippet.SqlPara para = paras.get(0);
        Assert.assertEquals("name", para.getName());
        Assert.assertEquals(":name:", para.getSqlName());
        Assert.assertFalse(para.isJdbcPara());
    }

    @Test
    public void test_04() {
        SqlSnippet snippet = resolver.resolve("json_param\\:\\:jsonb->>name");
        Assert.assertEquals("json_param::jsonb->>name", snippet.getSql());
        Assert.assertTrue(snippet.getParas().isEmpty());
    }

    @Test
    public void test_05() {
        SqlSnippet snippet = resolver.resolve("\\:name");
        Assert.assertEquals(":name", snippet.getSql());
        Assert.assertTrue(snippet.getParas().isEmpty());
    }

}
