package cn.zhxu.bs;

import cn.zhxu.bs.group.DefaultParserFactory;
import cn.zhxu.bs.group.ExprParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ExprParserTestCase {

    ExprParser.Factory parserFactory = new DefaultParserFactory();

    private String parse(String expr) {
        return parserFactory.create(expr).parse().toString();
    }

    @Test
    public void test_00() {
        Assertions.assertEquals("a", parse("a"));
    }

    @Test
    public void test_01() {
        Assertions.assertEquals("a|b", parse("a|b"));
    }

    @Test
    public void test_02() {
        Assertions.assertEquals("a&b", parse("a&b"));
    }

    @Test
    public void test_03() {
        Assertions.assertEquals("a", parse("a|a|a"));
    }

    @Test
    public void test_04() {
        Assertions.assertEquals("b", parse("b&b&b"));
    }

    @Test
    public void test_05() {
        Assertions.assertEquals("a|b", parse("a|b|a|b"));
    }

    @Test
    public void test_06() {
        Assertions.assertEquals("a&b", parse("a&b&a&b"));
    }

    @Test
    public void test_07() {
        Assertions.assertEquals("a|b", parse("(a|b)&(a|b)"));
    }

    @Test
    public void test_08() {
        Assertions.assertEquals("a&b", parse("(a&b)|(a&b)"));
    }

    @Test
    public void test_09() {
        Assertions.assertEquals("a|b&d|f", parse("a|b&(c|d|e)&d|f"));
    }

    @Test
    public void test_10() {
        Assertions.assertEquals("a|b&d|f", parse("(a|b&((c|(d|e)))&d)|(f)"));
    }

    @Test
    public void test_11() {
        Assertions.assertEquals("a", parse("(a)"));
    }

    @Test
    public void test_12() {
        Assertions.assertEquals("a", parse("a&a"));
    }

    @Test
    public void test_13() {
        Assertions.assertEquals("a", parse("a|a&b"));
    }

    @Test
    public void test_14() {
        Assertions.assertEquals("a", parse("a|(a&b)"));
    }

    @Test
    public void test_15() {
        Assertions.assertEquals("a", parse("a&(a|b)"));
    }

    @Test
    public void
    test_16() {
        Assertions.assertEquals("C&B|A", parse("A|((A|C)&B)"));
    }

    @Test
    public void test_17() {
        Assertions.assertEquals("(C|B)&A", parse("A&((A&C)|B)"));
    }

    @Test
    public void test_18() {
        Assertions.assertEquals("(C|B)&A", parse("A&(A&C|B)"));
    }

    @Test
    public void test_19() {
        Assertions.assertEquals("B|C|A", parse("A | (B | C)"));
    }

    @Test
    public void test_20() {
        Assertions.assertEquals("B&C&A", parse("A & (B & C)"));
    }

    @Test

    public void test_21() {
        Assertions.assertEquals("D|C&B|A", parse("A | (((A | C) & B) | D)"));
    }

    @Test
    public void test_22() {
        Assertions.assertEquals("D&C&B|A", parse("A | (A | C) & B & (A | D)"));
    }

    @Test
    public void test_23() {
        Assertions.assertEquals("E|D&C&B|A", parse("A | ((A | C) & B & (A | D) | E)"));
    }

    @Test
    public void test_24() {
        Assertions.assertEquals("(D|C|B)&A", parse("A & (A & C | B | A & D)"));
    }

    @Test
    public void test_25() {
        Assertions.assertEquals("B&(A|C)", parse("(A & B) | (C & B)"));
    }

    @Test
    public void test_26() {
        Assertions.assertEquals("B|A&C", parse("(A | B) & (C | B)"));
    }

    @Test
    public void test_27() {
        Assertions.assertEquals("A&B", parse("(A & B) | (A & B & D)"));
    }

    @Test
    public void test_28() {
        Assertions.assertEquals("A|B", parse("(A | B) & (A | B | D)"));
    }

    @Test
    public void test_29() {
        Assertions.assertEquals("A&B&(C&D|E&F)", parse("(A&B&C&D)|(A&B&E&F)"));
    }

    @Test
    public void test_30() {
        Assertions.assertEquals("A|B|(C|D)&(E|F)", parse("(A|B|C|D)&(A|B|E|F)"));
    }

    @Test
    public void test_31() {
        Assertions.assertEquals("A&B&(C|E&F)", parse("(A&B&C)|(A&B&E&F)"));
    }

    @Test
    public void test_32() {
        Assertions.assertEquals("A|B|C&(E|F)", parse("(A|B|C)&(A|B|E|F)"));
    }

}
