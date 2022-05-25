package com.ejlchina.searcher;

import com.ejlchina.searcher.implement.BasePageExtractor;
import com.ejlchina.searcher.implement.DefaultParamResolver;
import com.ejlchina.searcher.implement.PageOffsetExtractor;
import com.ejlchina.searcher.implement.PageSizeExtractor;
import com.ejlchina.searcher.param.FetchType;
import com.ejlchina.searcher.param.Paging;
import com.ejlchina.searcher.util.MapUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;

public class PageResolverTestCase {

    @Test
    public void test_page_01() {
        BasePageExtractor pageExtractor = new PageSizeExtractor();
        DefaultParamResolver paramResolver = new DefaultParamResolver();
        paramResolver.setPageExtractor(pageExtractor);
        Paging paging = paramResolver.resolvePaging(new FetchType(FetchType.DEFAULT), new HashMap<>());
        Assert.assertEquals(pageExtractor.getStart(), paging.getOffset());
        Assert.assertEquals(pageExtractor.getDefaultSize(), paging.getSize());
    }

    @Test
    public void test_page_02() {
        PageOffsetExtractor pageExtractor = new PageOffsetExtractor();
        DefaultParamResolver paramResolver = new DefaultParamResolver();
        paramResolver.setPageExtractor(pageExtractor);
        Paging paging = paramResolver.resolvePaging(new FetchType(FetchType.DEFAULT), new HashMap<>());
        Assert.assertEquals(pageExtractor.getStart(), paging.getOffset());
        Assert.assertEquals(pageExtractor.getDefaultSize(), paging.getSize());
    }

    @Test
    public void test_page_03() {
        BasePageExtractor pageExtractor = new PageSizeExtractor();
        DefaultParamResolver paramResolver = new DefaultParamResolver();
        paramResolver.setPageExtractor(pageExtractor);
        Paging paging = paramResolver.resolvePaging(
                new FetchType(FetchType.DEFAULT),
                MapUtils.builder()
                        .page(0, pageExtractor.getMaxAllowedSize() + 10000)
                        .build()
        );
        Assert.assertEquals(pageExtractor.getStart(), paging.getOffset());
        Assert.assertEquals(pageExtractor.getMaxAllowedSize(), paging.getSize());
    }

    @Test
    public void test_page_04() {
        PageOffsetExtractor pageExtractor = new PageOffsetExtractor();
        DefaultParamResolver paramResolver = new DefaultParamResolver();
        paramResolver.setPageExtractor(pageExtractor);
        Paging paging = paramResolver.resolvePaging(
                new FetchType(FetchType.DEFAULT),
                MapUtils.builder()
                        .page(0, pageExtractor.getMaxAllowedSize() + 10000)
                        .build()
        );
        Assert.assertEquals(pageExtractor.getStart(), paging.getOffset());
        Assert.assertEquals(pageExtractor.getMaxAllowedSize(), paging.getSize());
    }

    @Test
    public void test_page_05() {
        PageSizeExtractor pageExtractor = new PageSizeExtractor();
        DefaultParamResolver paramResolver = new DefaultParamResolver();
        paramResolver.setPageExtractor(pageExtractor);
        Paging paging = paramResolver.resolvePaging(
                new FetchType(FetchType.DEFAULT),
                MapUtils.builder()
                        .put("page", 0)
                        .put("size", pageExtractor.getMaxAllowedSize() + 10000)
                        .build()
        );
        Assert.assertEquals(pageExtractor.getStart(), paging.getOffset());
        Assert.assertEquals(pageExtractor.getMaxAllowedSize(), paging.getSize());
    }

    @Test
    public void test_page_06() {
        PageOffsetExtractor pageExtractor = new PageOffsetExtractor();
        DefaultParamResolver paramResolver = new DefaultParamResolver();
        paramResolver.setPageExtractor(pageExtractor);
        Paging paging = paramResolver.resolvePaging(
                new FetchType(FetchType.DEFAULT),
                MapUtils.builder()
                        .put("page", 0)
                        .put("size", pageExtractor.getMaxAllowedSize() + 10000)
                        .build()
        );
        Assert.assertEquals(pageExtractor.getStart(), paging.getOffset());
        Assert.assertEquals(pageExtractor.getMaxAllowedSize(), paging.getSize());
    }

}
