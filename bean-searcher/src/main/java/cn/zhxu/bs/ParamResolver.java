package cn.zhxu.bs;

import cn.zhxu.bs.param.FetchType;
import cn.zhxu.bs.util.NameConfigs;

import java.util.Map;

/**
 * 请求参数解析器接口
 * @author Troy.Zhou @ 2017-03-20
 * */
public interface ParamResolver {

    /**
     * @param beanMeta 元数据
     * @param fetchType Fetch 类型
     * @param paraMap 原始检索参数
     * @return SearchParam
     * @throws IllegalParamException 抛出非法参数异常后将终止 SQL 查询
     * */
    SearchParam resolve(BeanMeta<?> beanMeta, FetchType fetchType, Map<String, Object> paraMap) throws IllegalParamException;

    /**
     * 配置类
     * @since v4.3
     */
    class Configuration extends NameConfigs {

        /**
         * 用于控制参数构建器中使用 `groupExpr(..)` 方法指定的组表达式是否合并或覆盖前端参数传来的组表达式
         */
        private boolean gexprMerge = true;

        public String opSuffix() {
            return separator + op;
        }

        public String icSuffix() {
            return separator + ic;
        }

        public boolean gexprMerge() {
            return gexprMerge;
        }

        public Configuration gexprMerge(boolean gexprMerge) {
            this.gexprMerge = gexprMerge;
            return this;
        }

    }

}
