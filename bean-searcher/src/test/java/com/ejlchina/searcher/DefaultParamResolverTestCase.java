package com.ejlchina.searcher;

import com.ejlchina.searcher.implement.DefaultMetaResolver;
import com.ejlchina.searcher.implement.DefaultParamResolver;
import com.ejlchina.searcher.param.FetchType;
import com.ejlchina.searcher.param.Paging;
import com.ejlchina.searcher.util.MapUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class DefaultParamResolverTestCase {

    final ParamResolver paramResolver = new DefaultParamResolver();

    final BeanMeta<User> beanMeta = new DefaultMetaResolver().resolve(User.class);

    static class User {
        String name;
        int age;
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


}
