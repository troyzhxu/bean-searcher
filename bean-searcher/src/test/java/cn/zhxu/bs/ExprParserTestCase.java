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
        System.out.println("\ttest_00 ok!");
    }

    @Test
    public void test_01() {
        Assertions.assertEquals("a|b", parse("a|b"));
        System.out.println("\ttest_01 ok!");
    }

    @Test
    public void test_02() {
        Assertions.assertEquals("a&b", parse("a&b"));
        System.out.println("\ttest_02 ok!");
    }

    @Test
    public void test_03() {
        Assertions.assertEquals("a", parse("a|a|a"));
        System.out.println("\ttest_03 ok!");
    }

    @Test
    public void test_04() {
        Assertions.assertEquals("b", parse("b&b&b"));
        System.out.println("\ttest_04 ok!");
    }

    @Test
    public void test_05() {
        Assertions.assertEquals("a|b", parse("a|b|a|b"));
        System.out.println("\ttest_05 ok!");
    }

    @Test
    public void test_06() {
        Assertions.assertEquals("a&b", parse("a&b&a&b"));
        System.out.println("\ttest_06 ok!");
    }

    @Test
    public void test_07() {
        Assertions.assertEquals("a|b", parse("(a|b)&(a|b)"));
        System.out.println("\ttest_07 ok!");
    }

    @Test
    public void test_08() {
        Assertions.assertEquals("a&b", parse("(a&b)|(a&b)"));
        System.out.println("\ttest_08 ok!");
    }

    @Test
    public void test_09() {
        Assertions.assertEquals("a|b&d|f", parse("a|b&(c|d|e)&d|f"));
        System.out.println("\ttest_09 ok!");
    }

    @Test
    public void test_10() {
        Assertions.assertEquals("a|b&d|f", parse("(a|b&((c|(d|e)))&d)|(f)"));
        System.out.println("\ttest_10 ok!");
    }

    @Test
    public void test_11() {
        Assertions.assertEquals("a", parse("(a)"));
        System.out.println("\ttest_11 ok!");
    }

    @Test
    public void test_12() {
        Assertions.assertEquals("a", parse("a&a"));
        System.out.println("\ttest_12 ok!");
    }

    @Test
    public void test_13() {
        Assertions.assertEquals("a", parse("a|a&b"));
        System.out.println("\ttest_13 ok!");
    }

    @Test
    public void test_14() {
        Assertions.assertEquals("a", parse("a|(a&b)"));
        System.out.println("\ttest_14 ok!");
    }

    @Test
    public void test_15() {
        Assertions.assertEquals("a", parse("a&(a|b)"));
        System.out.println("\ttest_15 ok!");
    }

    @Test
    public void test_16() {
        Assertions.assertEquals("C&B|A", parse("A|((A|C)&B)"));
        System.out.println("\ttest_16 ok!");
    }

    @Test
    public void test_17() {
        Assertions.assertEquals("(C|B)&A", parse("A&((A&C)|B)"));
        System.out.println("\ttest_17 ok!");
    }

    @Test
    public void test_18() {
        Assertions.assertEquals("(C|B)&A", parse("A&(A&C|B)"));
        System.out.println("\ttest_18 ok!");
    }

    @Test
    public void test_19() {
        Assertions.assertEquals("B|C|A", parse("A | (B | C)"));
        System.out.println("\ttest_19 ok!");
    }

    @Test
    public void test_20() {
        Assertions.assertEquals("B&C&A", parse("A & (B & C)"));
        System.out.println("\ttest_20 ok!");
    }

    @Test
    public void test_21() {
        Assertions.assertEquals("D|C&B|A", parse("A | (((A | C) & B) | D)"));
        System.out.println("\ttest_21 ok!");
    }

    @Test
    public void test_22() {
        Assertions.assertEquals("D&C&B|A", parse("A | (A | C) & B & (A | D)"));
        System.out.println("\ttest_22 ok!");
    }

    @Test
    public void test_23() {
        Assertions.assertEquals("E|D&C&B|A", parse("A | ((A | C) & B & (A | D) | E)"));
        System.out.println("\ttest_23 ok!");
    }

    @Test
    public void test_24() {
        Assertions.assertEquals("(D|C|B)&A", parse("A & (A & C | B | A & D)"));
        System.out.println("\ttest_24 ok!");
    }

    @Test
    public void test_25() {
        Assertions.assertEquals("B&(A|C)", parse("(A & B) | (C & B)"));
        System.out.println("\ttest_25 ok!");
    }

    @Test
    public void test_26() {
        Assertions.assertEquals("B|A&C", parse("(A | B) & (C | B)"));
        System.out.println("\ttest_26 ok!");
    }

    @Test
    public void test_27() {
        Assertions.assertEquals("A&B", parse("(A & B) | (A & B & D)"));
        System.out.println("\ttest_27 ok!");
    }

    @Test
    public void test_28() {
        Assertions.assertEquals("A|B", parse("(A | B) & (A | B | D)"));
        System.out.println("\ttest_28 ok!");
    }

    @Test
    public void test_29() {
        Assertions.assertEquals("A&B&(C&D|E&F)", parse("(A&B&C&D)|(A&B&E&F)"));
        System.out.println("\ttest_29 ok!");
    }

    @Test
    public void test_30() {
        Assertions.assertEquals("A|B|(C|D)&(E|F)", parse("(A|B|C|D)&(A|B|E|F)"));
        System.out.println("\ttest_30 ok!");
    }

    @Test
    public void test_31() {
        Assertions.assertEquals("A&B&(C|E&F)", parse("(A&B&C)|(A&B&E&F)"));
        System.out.println("\ttest_31 ok!");
    }

    @Test
    public void test_32() {
        Assertions.assertEquals("A|B|C&(E|F)", parse("(A|B|C)&(A|B|E|F)"));
        System.out.println("\ttest_32 ok!");
    }

}
