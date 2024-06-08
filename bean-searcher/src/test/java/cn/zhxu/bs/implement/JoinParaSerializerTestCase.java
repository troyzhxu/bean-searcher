package cn.zhxu.bs.implement;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class JoinParaSerializerTestCase {

    JoinParaSerializer serializer = new JoinParaSerializer();

    @Test
    public void test_01() {
        Assertions.assertEquals("1,2,3", serializer.serialize(Arrays.asList(1,2,3)));
        Assertions.assertEquals("1,2,3", serializer.serialize(new int[] {1,2,3}));
        Assertions.assertEquals("1,2,3", serializer.serialize(new long[] {1,2,3}));
        Assertions.assertEquals("1,2,3", serializer.serialize(new short[] {1,2,3}));
        Assertions.assertEquals("1,2,3", serializer.serialize(new byte[] {1,2,3}));
        Assertions.assertEquals("true,false", serializer.serialize(new boolean[] {true, false}));

        Assertions.assertEquals("1,2,3", serializer.serialize(new Integer[] {1,2,3}));
        Assertions.assertEquals("1,2,3", serializer.serialize(new Long[] {1L,2L,3L}));
        Assertions.assertEquals("1,2,3", serializer.serialize(new Short[] {1,2,3}));
        Assertions.assertEquals("1,2,3", serializer.serialize(new Byte[] {1,2,3}));
        Assertions.assertEquals("true,false", serializer.serialize(new Boolean[] {true, false}));

        Assertions.assertEquals("'A','B','C'", serializer.serialize(new String[] {"A","B","C"}));
        Assertions.assertEquals("'A','B','C',null", serializer.serialize(Arrays.asList("A","B","C", null)));
        Assertions.assertEquals("ABC", serializer.serialize("ABC"));
        Assertions.assertEquals("100", serializer.serialize(100));
        System.out.println("\ttest_01 ok!");
    }

}
