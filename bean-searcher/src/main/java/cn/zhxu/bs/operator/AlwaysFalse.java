package cn.zhxu.bs.operator;

import cn.zhxu.bs.FieldOp;
import cn.zhxu.bs.dialect.DialectWrapper;

import java.util.Collections;
import java.util.List;

/**
 * 恒假运算符,用于忽略字段参数值,生成0条件
 *
 * @author Corey @ 2024-06-13
 * @since v4.2.10
 */
public class AlwaysFalse extends DialectWrapper implements FieldOp {


    @Override
    public String name () {

        return "AlwaysFalse";
    }

    @Override
    public boolean isNamed ( String name ) {

        return "af".equals( name ) || "AlwaysFalse".equals( name );
    }

    @Override
    public boolean lonely () {

        return true;
    }

    @Override
    public List< Object > operate ( StringBuilder sqlBuilder, OpPara opPara ) {

        sqlBuilder.append( "0" );
        return Collections.emptyList();
    }
}
