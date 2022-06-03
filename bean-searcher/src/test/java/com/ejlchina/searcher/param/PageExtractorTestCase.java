package com.ejlchina.searcher.param;

import com.ejlchina.searcher.BeanMeta;
import com.ejlchina.searcher.PageExtractor;
import com.ejlchina.searcher.SearchParam;
import com.ejlchina.searcher.implement.DefaultMetaResolver;
import com.ejlchina.searcher.implement.DefaultParamResolver;
import com.ejlchina.searcher.implement.PageOffsetExtractor;
import com.ejlchina.searcher.implement.PageSizeExtractor;
import com.ejlchina.searcher.util.MapUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class PageExtractorTestCase {

    final DefaultParamResolver paramResolver = new DefaultParamResolver();

    final BeanMeta<User> beanMeta = new DefaultMetaResolver().resolve(User.class);

    static class User {
        private String name;
        private int age;
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public int getAge() {
            return age;
        }
        public void setAge(int age) {
            this.age = age;
        }
    }

    private SearchParam resolve(Map<String, Object> paraMap) {
        return paramResolver.resolve(beanMeta, new FetchType(FetchType.DEFAULT), paraMap);
    }

    @Test
    public void test_01() {
        test_01_do();
        PageExtractor oldPageExtractor = paramResolver.getPageExtractor();
        paramResolver.setPageExtractor(new PageOffsetExtractor());
        test_01_do();
        paramResolver.setPageExtractor(oldPageExtractor);
    }

    private void test_01_do() {
        assert_01(resolve(null));
        assert_01(resolve(new HashMap<>()));
        assert_01(resolve(MapUtils.builder().build()));
        assert_01(resolve(MapUtils.builder(new HashMap<>()).build()));
        assert_01(resolve(MapUtils.flatBuilder(new HashMap<>()).build()));
        assert_01(resolve(MapUtils.flat(new HashMap<>())));
    }

    private void assert_01(SearchParam param) {
        Assert.assertTrue(param.getParaMap().isEmpty());
        Assert.assertEquals(FetchType.DEFAULT, param.getFetchType().getType());
        Assert.assertArrayEquals(new Object[]{ "name", "age"}, param.getFetchFields().toArray());
        Paging paging = param.getPaging();
        Assert.assertEquals(15, paging.getSize());
        Assert.assertEquals(0, paging.getOffset());
        Assert.assertEquals("[]", param.getParamsGroup().toString());
    }

    @Test
    public void test_02() {
        assert_02(resolve(MapUtils.builder()
                .page(2, 10)
                .build()));
        assert_02(resolve(MapUtils.builder()
                .limit(20, 10)
                .build()));
        Map<String, Object> params = new HashMap<>();
        params.put("page", 2);
        params.put("size", 10);
        assert_02(resolve(params));
    }

    private void assert_02(SearchParam param) {
        Assert.assertEquals(FetchType.DEFAULT, param.getFetchType().getType());
        Assert.assertArrayEquals(new Object[]{ "name", "age"}, param.getFetchFields().toArray());
        Paging paging = param.getPaging();
        Assert.assertEquals(10, paging.getSize());
        Assert.assertEquals(20, paging.getOffset());
        Assert.assertEquals("[]", param.getParamsGroup().toString());
    }

    @Test
    public void test_03() {
        PageSizeExtractor pageExtractor = (PageSizeExtractor) paramResolver.getPageExtractor();
        pageExtractor.setStart(1);
        assert_03(resolve(MapUtils.builder()
                .page(2, 10)
                .build()));
        Map<String, Object> params = new HashMap<>();
        params.put("page", 2);
        params.put("size", 10);
        assert_03(resolve(params));
        pageExtractor.setStart(0);
    }

    private void assert_03(SearchParam param) {
        Assert.assertEquals(FetchType.DEFAULT, param.getFetchType().getType());
        Assert.assertArrayEquals(new Object[]{ "name", "age"}, param.getFetchFields().toArray());
        Paging paging = param.getPaging();
        Assert.assertEquals(10, paging.getSize());
        Assert.assertEquals(10, paging.getOffset());
        Assert.assertEquals("[]", param.getParamsGroup().toString());
    }

    @Test
    public void test_04() {
        PageExtractor oldPageExtractor = paramResolver.getPageExtractor();
        PageOffsetExtractor offsetExtractor = new PageOffsetExtractor();
        offsetExtractor.setStart(1);
        paramResolver.setPageExtractor(offsetExtractor);
        assert_04(resolve(MapUtils.builder()
                .limit(20, 10)
                .build()));
        Map<String, Object> params = new HashMap<>();
        params.put("offset", 20);
        params.put("size", 10);
        assert_04(resolve(params));
        paramResolver.setPageExtractor(oldPageExtractor);
    }

    private void assert_04(SearchParam param) {
        Assert.assertEquals(FetchType.DEFAULT, param.getFetchType().getType());
        Assert.assertArrayEquals(new Object[]{ "name", "age"}, param.getFetchFields().toArray());
        Paging paging = param.getPaging();
        Assert.assertEquals(10, paging.getSize());
        Assert.assertEquals(19, paging.getOffset());
        Assert.assertEquals("[]", param.getParamsGroup().toString());
    }

}
