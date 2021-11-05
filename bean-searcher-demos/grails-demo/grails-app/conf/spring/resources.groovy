package spring

import com.ejlchina.searcher.implement.DefaultBeanSearcher
import com.ejlchina.searcher.implement.DefaultMapSearcher
import com.ejlchina.searcher.implement.DefaultSqlExecutor


// Place your Spring DSL code here
beans = {

    sqlExecutor(DefaultSqlExecutor) {
        dataSource = ref('dataSource')
    }

    // 声明 BeanSearcher 检索器，它查询的结果是 SearchBean 泛型对象
    beanSearcher(DefaultBeanSearcher) {
        sqlExecutor = ref('sqlExecutor')
    }

    // 声明 MapSearcher 检索器，它查询的结果是 Map 对象
    mapSearcher(DefaultMapSearcher) {
        sqlExecutor = ref('sqlExecutor')
    }

}
