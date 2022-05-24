package com.ejlchina.searcher.convertor;

import com.ejlchina.searcher.BeanMeta;
import com.ejlchina.searcher.FieldConvertor;
import com.ejlchina.searcher.FieldMeta;
import com.ejlchina.searcher.implement.BoolNumFieldConvertor;
import com.ejlchina.searcher.implement.DefaultMetaResolver;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collection;

public class BoolNumFieldConvertorTestCase {

    private final FieldConvertor convertor = new BoolNumFieldConvertor();

    private final BeanMeta<Entity> beanMeta = new DefaultMetaResolver().resolve(Entity.class);

    public static class Entity {

        private Integer vI;
        private int vi;
        private Long vL;
        private long vl;
        private Short vS;
        private short vs;
        private Byte vB;
        private byte vb;

    }

    @Test
    public void test_supports() {
        Collection<FieldMeta> fieldMetas = beanMeta.getFieldMetas();
        Assert.assertEquals(8, fieldMetas.size());
        fieldMetas.forEach(meta -> Assert.assertTrue(convertor.supports(meta, Boolean.class)));
        System.out.println("BoolNumFieldConvertorTests 01 OK!");
    }

    @Test
    public void test_convert_vI() throws IllegalAccessException {
        FieldMeta meta = beanMeta.requireFieldMeta("vI");
        Entity entity = new Entity();
        meta.getField().set(entity, convertor.convert(meta, true));
        Assert.assertEquals(Integer.valueOf(1), entity.vI);
        meta.getField().set(entity, convertor.convert(meta, false));
        Assert.assertEquals(Integer.valueOf(0), entity.vI);
        System.out.println("BoolNumFieldConvertorTests 02 OK!");
    }

    @Test
    public void test_convert_vi() throws IllegalAccessException {
        FieldMeta meta = beanMeta.requireFieldMeta("vi");
        Entity entity = new Entity();
        meta.getField().set(entity, convertor.convert(meta, true));
        Assert.assertEquals(1, entity.vi);
        meta.getField().set(entity, convertor.convert(meta, false));
        Assert.assertEquals(0, entity.vi);
    }

    @Test
    public void test_convert_vL() throws IllegalAccessException {
        FieldMeta meta = beanMeta.requireFieldMeta("vL");
        Entity entity = new Entity();
        meta.getField().set(entity, convertor.convert(meta, true));
        Assert.assertEquals(Long.valueOf(1), entity.vL);
        meta.getField().set(entity, convertor.convert(meta, false));
        Assert.assertEquals(Long.valueOf(0), entity.vL);
    }

    @Test
    public void test_convert_vl() throws IllegalAccessException {
        FieldMeta meta = beanMeta.requireFieldMeta("vl");
        Entity entity = new Entity();
        meta.getField().set(entity, convertor.convert(meta, true));
        Assert.assertEquals(1L, entity.vl);
        meta.getField().set(entity, convertor.convert(meta, false));
        Assert.assertEquals(0L, entity.vl);
    }

    @Test
    public void test_convert_vS() throws IllegalAccessException {
        FieldMeta meta = beanMeta.requireFieldMeta("vS");
        Entity entity = new Entity();
        meta.getField().set(entity, convertor.convert(meta, true));
        Assert.assertEquals(Short.valueOf((short) 1), entity.vS);
        meta.getField().set(entity, convertor.convert(meta, false));
        Assert.assertEquals(Short.valueOf((short) 0), entity.vS);
    }

    @Test
    public void test_convert_vs() throws IllegalAccessException {
        FieldMeta meta = beanMeta.requireFieldMeta("vs");
        Entity entity = new Entity();
        meta.getField().set(entity, convertor.convert(meta, true));
        Assert.assertEquals((short) 1, entity.vs);
        meta.getField().set(entity, convertor.convert(meta, false));
        Assert.assertEquals((short) 0, entity.vs);
    }

    @Test
    public void test_convert_vB() throws IllegalAccessException {
        FieldMeta meta = beanMeta.requireFieldMeta("vB");
        Entity entity = new Entity();
        meta.getField().set(entity, convertor.convert(meta, true));
        Assert.assertEquals(Byte.valueOf((byte) 1), entity.vB);
        meta.getField().set(entity, convertor.convert(meta, false));
        Assert.assertEquals(Byte.valueOf((byte) 0), entity.vB);
    }

    @Test
    public void test_convert_vb() throws IllegalAccessException {
        FieldMeta meta = beanMeta.requireFieldMeta("vb");
        Entity entity = new Entity();
        meta.getField().set(entity, convertor.convert(meta, true));
        Assert.assertEquals((byte) 1, entity.vb);
        meta.getField().set(entity, convertor.convert(meta, false));
        Assert.assertEquals((byte) 0, entity.vb);
    }

}
