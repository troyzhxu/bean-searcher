package com.ejlchina.searcher;

import com.ejlchina.searcher.group.Group;
import com.ejlchina.searcher.group.GroupResolver;
import org.junit.Assert;
import org.junit.Test;

public class GroupResolverTests {

    GroupResolver groupResolver = new GroupResolver();

    @Test
    public void test_01() {
        Group<String> group = groupResolver.createParser("a+b").parse();
        Assert.assertEquals("a+b", group.toString());
    }

    @Test
    public void test_02() {
        Group<String> group = groupResolver.createParser("a*b").parse();
        Assert.assertEquals("a*b", group.toString());
    }

    @Test
    public void test_03() {
        Group<String> group = groupResolver.createParser("a+a+a").parse();
        Assert.assertEquals("a", group.toString());
    }

    @Test
    public void test_04() {
        Group<String> group = groupResolver.createParser("b*b*b").parse();
        Assert.assertEquals("b", group.toString());
    }

    @Test
    public void test_05() {
        Group<String> group = groupResolver.createParser("a+b+a+b").parse();
        Assert.assertEquals("a+b", group.toString());
    }

    @Test
    public void test_06() {
        Group<String> group = groupResolver.createParser("a*b*a*b").parse();
        Assert.assertEquals("a*b", group.toString());
    }

    @Test
    public void test_07() {
        Group<String> group = groupResolver.createParser("(a+b)*(a+b)").parse();
        Assert.assertEquals("a+b", group.toString());
    }

    @Test
    public void test_08() {
        Group<String> group = groupResolver.createParser("(a*b)+(a*b)").parse();
        Assert.assertEquals("a*b", group.toString());
    }

    @Test
    public void test_09() {
        Group<String> group = groupResolver.createParser("a+b*(c+d+e)*d+f").parse();
        Assert.assertEquals("a+b*(c+d+e)*d+f", group.toString());
    }

    @Test
    public void test_19() {
        Group<String> group = groupResolver.createParser("(a+b*((c+(d+e)))*d)+(f)").parse();
        Assert.assertEquals("a+b*(d+e+c)*d+f", group.toString());
    }

}
