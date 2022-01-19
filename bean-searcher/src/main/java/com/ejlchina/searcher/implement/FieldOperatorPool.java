package com.ejlchina.searcher.implement;

import com.ejlchina.searcher.FieldOp;
import com.ejlchina.searcher.dialect.Dialect;
import com.ejlchina.searcher.dialect.DialectSensor;
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
public class FieldOperatorPool extends DialectWrapper {

    private List<FieldOp> fieldOps;

    private final Map<Object, FieldOp> cache = new ConcurrentHashMap<>();


    public FieldOperatorPool(List<FieldOp> fieldOps) {
        this.fieldOps = Objects.requireNonNull(fieldOps);
    }

    public FieldOperatorPool() {
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
        if (key == null) {
            return null;
        }
        FieldOp fOp = cache.get(key);
        if (fOp != null) {
            return fOp;
        }
        for (FieldOp op: fieldOps) {
            if (isMatch(op, key)) {
                cache.put(key, op);
                return op;
            }
        }
        return null;
    }

    private boolean isMatch(FieldOp op, Object key) {
        if (key instanceof FieldOp) {
            return op.sameTo((FieldOp) key);
        }
        if (key instanceof String) {
            return op.isNamed((String) key);
        }
        if (key instanceof Class) {
            return ((Class<?>) key).isAssignableFrom(op.getClass());
        }
        return false;
    }

    public List<FieldOp> getFieldOps() {
        return fieldOps;
    }

    public synchronized void setFieldOps(List<FieldOp> fieldOps) {
        this.fieldOps = fieldOps;
        updateAllOpDialect();
    }

    public synchronized void addFieldOp(FieldOp fieldOp) {
        this.fieldOps.add(fieldOp);
        updateOpDialect(fieldOp);
    }

    @Override
    public synchronized void setDialect(Dialect dialect) {
        super.setDialect(dialect);
        updateAllOpDialect();
    }

    private void updateAllOpDialect() {
        for (FieldOp op : fieldOps) {
            updateOpDialect(op);
        }
    }

    private void updateOpDialect(FieldOp op) {
        if (op instanceof DialectSensor) {
            ((DialectSensor) op).setDialect(getDialect());
        }
    }

}

