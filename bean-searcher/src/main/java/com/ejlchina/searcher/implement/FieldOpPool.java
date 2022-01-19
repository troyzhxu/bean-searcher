package com.ejlchina.searcher.implement;

import com.ejlchina.searcher.FieldOp;
import com.ejlchina.searcher.param.Operator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 字段运算符池
 * @author Troy.Zhou @ 2022-01-19
 * @since v3.3.0
 */
public class FieldOpPool {

    private List<FieldOp> fieldOps;

    private final Map<Object, FieldOp> cache = new ConcurrentHashMap<>();


    public FieldOpPool(List<FieldOp> fieldOps) {
        this.fieldOps = Objects.requireNonNull(fieldOps);
    }

    public FieldOpPool() {
        fieldOps = new ArrayList<>();
        fieldOps.add(Operator.Equal);
        fieldOps.add(Operator.NotEqual);
        fieldOps.add(Operator.GreaterThan);
        fieldOps.add(Operator.GreaterEqual);
        fieldOps.add(Operator.LessThan);
        fieldOps.add(Operator.LessEqual);
        fieldOps.add(Operator.Between);
        fieldOps.add(Operator.Contain);
        fieldOps.add(Operator.StartWith);
        fieldOps.add(Operator.EndWith);
        fieldOps.add(Operator.InList);
        fieldOps.add(Operator.NotIn);
        fieldOps.add(Operator.IsNull);
        fieldOps.add(Operator.NotNull);
        fieldOps.add(Operator.Empty);
        fieldOps.add(Operator.NotEmpty);
    }


    public FieldOp getFieldOp(Object key) {
        FieldOp fOp = cache.get(key);
        if (fOp != null) {
            return fOp;
        }
        String name = key.toString();
        for (FieldOp op: fieldOps) {
            if (op == key || op.isNamed(name)) {
                cache.put(key, op);
                return op;
            }
        }
        return null;
    }

    public List<FieldOp> getFieldOps() {
        return fieldOps;
    }

    public void setFieldOps(List<FieldOp> fieldOps) {
        this.fieldOps = fieldOps;
    }

}
