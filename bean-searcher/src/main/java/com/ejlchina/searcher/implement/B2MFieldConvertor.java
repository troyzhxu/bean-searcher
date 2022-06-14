package com.ejlchina.searcher.implement;

import java.util.List;

/**
 * BFieldConvertor to MFieldConvertor
 *
 * @author Troy.Zhou @ 2022-04-19
 * @since v3.6.0
 *
 * this class will be removed in v4.0, please use {@link com.ejlchina.searcher.convertor.B2MFieldConvertor} instead.
 */
@Deprecated
public class B2MFieldConvertor extends com.ejlchina.searcher.convertor.B2MFieldConvertor {

    public B2MFieldConvertor(List<BFieldConvertor> convertors) {
        super(convertors);
    }

}
