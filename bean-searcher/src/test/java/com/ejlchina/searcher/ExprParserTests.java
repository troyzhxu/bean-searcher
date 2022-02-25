package com.ejlchina.searcher;

import com.ejlchina.searcher.group.DefaultParserFactory;
import com.ejlchina.searcher.group.ExprParser;
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
        Assert.assertEquals("a|b&d|f", parse("a|b&(c|d|e)&d|f"));
    }

    @Test
    public void test_10() {
        Assert.assertEquals("a|b&d|f", parse("(a|b&((c|(d|e)))&d)|(f)"));
    }

    @Test
    public void test_11() {
        Assert.assertEquals("a", parse("(a)"));
    }

    @Test
    public void test_12() {
        Assert.assertEquals("a", parse("a&a"));
    }

    @Test
    public void test_13() {
        Assert.assertEquals("a", parse("a|a&b"));
    }

    @Test
    public void test_14() {
        Assert.assertEquals("a", parse("a|(a&b)"));
    }

    @Test
    public void test_15() {
        Assert.assertEquals("a", parse("a&(a|b)"));
    }

    @Test
    public void
    test_16() {
        Assert.assertEquals("C&B|A", parse("A|((A|C)&B)"));
    }

    @Test
    public void test_17() {
        Assert.assertEquals("(C|B)&A", parse("A&((A&C)|B)"));
    }

    @Test
    public void test_18() {
        Assert.assertEquals("(C|B)&A", parse("A&(A&C|B)"));
    }

    @Test
    public void test_19() {
        Assert.assertEquals("B|C|A", parse("A | (B | C)"));
    }

    @Test
    public void test_20() {
        Assert.assertEquals("B&C&A", parse("A & (B & C)"));
    }

    @Test

    public void test_21() {
        Assert.assertEquals("D|C&B|A", parse("A | (((A | C) & B) | D)"));
    }

    @Test
    public void test_22() {
        Assert.assertEquals("D&C&B|A", parse("A | (A | C) & B & (A | D)"));
    }

    @Test
    public void test_23() {
        Assert.assertEquals("E|D&C&B|A", parse("A | ((A | C) & B & (A | D) | E)"));
    }

    @Test
    public void test_24() {
        Assert.assertEquals("(D|C|B)&A", parse("A & (A & C | B | A & D)"));
    }

}
