package cn.zhxu.bs.boot;

import cn.zhxu.bs.convertor.OracleTimestampFieldConvertor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(oracle.sql.TIMESTAMP.class)
public class BeanSearcherConfigOnOracle {

    @Bean
    @ConditionalOnProperty(name = "bean-searcher.field-convertor.use-oracle-timestamp", havingValue = "true", matchIfMissing = true)
    @ConditionalOnMissingBean(OracleTimestampFieldConvertor.class)
    public OracleTimestampFieldConvertor oracleTimestampFieldConvertor() {
        return new OracleTimestampFieldConvertor();
    }

}
