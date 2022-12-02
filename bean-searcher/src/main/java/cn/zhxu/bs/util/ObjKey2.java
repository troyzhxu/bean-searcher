package cn.zhxu.bs.util;

import java.util.Objects;

/**
 * 两个对象组成一个键
 * @author Troy.Zhou @ 2022-04-20
 * @since v3.6.0
 */
public class ObjKey2 {

    private final Object o1;
    private final Object o2;

    public ObjKey2(Object o1, Object o2) {
        this.o1 = o1;
        this.o2 = o2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ObjKey2 objKey2 = (ObjKey2) o;
        return Objects.equals(o1, objKey2.o1) && Objects.equals(o2, objKey2.o2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(o1, o2);
    }

}
