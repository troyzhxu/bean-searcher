package com.ejlchina.searcher;

import com.ejlchina.searcher.group.DefaultParserFactory;
import com.ejlchina.searcher.group.ExprParser;
import com.ejlchina.searcher.group.Group;
import com.ejlchina.searcher.group.DefaultGroupResolver;
import org.junit.Assert;
import org.junit.Test;

public class ExprParserTests {

    ExprParser.Factory parserFactory = new DefaultParserFactory();

    private String parse(String expr) {
        return parserFactory.create(expr).parse().toString();
    }

    @Test
    public void test_00() {
        Assert.assertEquals("a", parse("a"));
    }

    @Test
    public void test_01() {
        Assert.assertEquals("a|b", parse("a|b"));
    }

    @Test
    public void test_02() {
        Assert.assertEquals("a&b", parse("a&b"));
    }

    @Test
    public void test_03() {
        Assert.assertEquals("a", parse("a|a|a"));
    }

    @Test
    public void test_04() {
        Assert.assertEquals("b", parse("b&b&b"));
    }

    @Test
    public void test_05() {
        Assert.assertEquals("a|b", parse("a|b|a|b"));
    }

    @Test
    public void test_06() {
        Assert.assertEquals("a&b", parse("a&b&a&b"));
    }

    @Test
    public void test_07() {
        Assert.assertEquals("a|b", parse("(a|b)&(a|b)"));
    }

    @Test
    public void test_08() {
        Assert.assertEquals("a&b", parse("(a&b)|(a&b)"));
    }

    @Test
    public void test_09() {
        Assert.assertEquals("a|b&(c|d|e)&d|f", parse("a|b&(c|d|e)&d|f"));
    }

    @Test
    public void test_10() {
        Assert.assertEquals("a|b&(d|e|c)&d|f", parse("(a|b&((c|(d|e)))&d)|(f)"));
    }

    @Test
    public void test_11() {
        Assert.assertEquals("a", parse("(a)"));
    }

    @Test
    public void test_12() {
        Assert.assertEquals("a", parse("((a))"));
    }

}
