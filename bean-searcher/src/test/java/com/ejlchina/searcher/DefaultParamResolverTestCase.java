package com.ejlchina.searcher;

import com.ejlchina.searcher.implement.DefaultMetaResolver;
import com.ejlchina.searcher.implement.DefaultParamResolver;
import com.ejlchina.searcher.implement.PageSizeExtractor;
import com.ejlchina.searcher.param.FetchType;
import com.ejlchina.searcher.param.Paging;
import com.ejlchina.searcher.util.MapUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class DefaultParamResolverTestCase {

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

    private SearchParam resolve(FetchType fetchType, Map<String, Object> paraMap) {
        return paramResolver.resolve(beanMeta, fetchType, paraMap);
    }

    @Test
    public void test_01() {
        assert_01(resolve(new FetchType(FetchType.DEFAULT), null));
        assert_01(resolve(new FetchType(FetchType.DEFAULT), new HashMap<>()));
        assert_01(resolve(new FetchType(FetchType.DEFAULT), MapUtils.builder().build()));
        assert_01(resolve(new FetchType(FetchType.DEFAULT), MapUtils.builder(new HashMap<>()).build()));
        assert_01(resolve(new FetchType(FetchType.DEFAULT), MapUtils.flatBuilder(new HashMap<>()).build()));
        assert_01(resolve(new FetchType(FetchType.DEFAULT), MapUtils.flat(new HashMap<>())));
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
        assert_02(resolve(new FetchType(FetchType.DEFAULT), MapUtils.builder()
                .page(2, 10)
                .build()));
        assert_02(resolve(new FetchType(FetchType.DEFAULT), MapUtils.builder()
                .limit(20, 10)
                .build()));
        Map<String, Object> params = new HashMap<>();
        params.put("page", 2);
        params.put("size", 10);
        assert_02(resolve(new FetchType(FetchType.DEFAULT), params));
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
        assert_03(resolve(new FetchType(FetchType.DEFAULT), MapUtils.builder()
                .page(2, 10)
                .build()));
        assert_03(resolve(new FetchType(FetchType.DEFAULT), MapUtils.builder()
                .limit(20, 10)
                .build()));
        Map<String, Object> params = new HashMap<>();
        params.put("page", 2);
        params.put("size", 10);
        assert_03(resolve(new FetchType(FetchType.DEFAULT), params));
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

}
