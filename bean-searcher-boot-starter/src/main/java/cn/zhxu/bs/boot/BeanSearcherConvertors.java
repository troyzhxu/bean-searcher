package cn.zhxu.bs.boot;

import cn.zhxu.bs.FieldConvertor;
import cn.zhxu.bs.boot.prop.BeanSearcherFieldConvertor;
import cn.zhxu.bs.boot.prop.BeanSearcherParams;
import cn.zhxu.bs.convertor.*;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Configuration
public class BeanSearcherConvertors {

    // ParamConvertor

    @Bean
    @ConditionalOnMissingBean(BoolParamConvertor.class)
    public BoolParamConvertor boolParamConvertor() {
        return new BoolParamConvertor();
    }

    @Bean
    @ConditionalOnMissingBean(NumberParamConvertor.class)
    public NumberParamConvertor numberParamConvertor() {
        return new NumberParamConvertor();
    }

    @Bean
    @ConditionalOnMissingBean(DateParamConvertor.class)
    public DateParamConvertor dateParamConvertor(BeanSearcherParams config) {
        return new DateParamConvertor(config.getConvertor().getDateTarget());
    }

    @Bean
    @ConditionalOnMissingBean(TimeParamConvertor.class)
    public TimeParamConvertor timeParamConvertor(BeanSearcherParams config) {
        return new TimeParamConvertor(config.getConvertor().getTimeTarget());
    }

    @Bean
    @ConditionalOnMissingBean(DateTimeParamConvertor.class)
    public DateTimeParamConvertor dateTimeParamConvertor(BeanSearcherParams config) {
        BeanSearcherParams.Convertor conf = config.getConvertor();
        DateTimeParamConvertor convertor = new DateTimeParamConvertor(conf.getDateTimeTarget());
        convertor.setZoneId(conf.getZoneId());
        return convertor;
    }

    @Bean
    @ConditionalOnMissingBean(EnumParamConvertor.class)
    public EnumParamConvertor enumParamConvertor() {
        return new EnumParamConvertor();
    }

    // FieldConvertor

    @Bean
    @ConditionalOnProperty(name = "bean-searcher.field-convertor.use-number", havingValue = "true", matchIfMissing = true)
    @ConditionalOnMissingBean(NumberFieldConvertor.class)
    public NumberFieldConvertor numberFieldConvertor() {
        return new NumberFieldConvertor();
    }

    @Bean
    @ConditionalOnProperty(name = "bean-searcher.field-convertor.use-str-num", havingValue = "true", matchIfMissing = true)
    @ConditionalOnMissingBean(StrNumFieldConvertor.class)
    public StrNumFieldConvertor strNumFieldConvertor() {
        return new StrNumFieldConvertor();
    }

    @Bean
    @ConditionalOnProperty(name = "bean-searcher.field-convertor.use-bool-num", havingValue = "true", matchIfMissing = true)
    @ConditionalOnMissingBean(BoolNumFieldConvertor.class)
    public BoolNumFieldConvertor boolNumFieldConvertor() {
        return new BoolNumFieldConvertor();
    }

    @Bean
    @ConditionalOnProperty(name = "bean-searcher.field-convertor.use-bool", havingValue = "true", matchIfMissing = true)
    @ConditionalOnMissingBean(BoolFieldConvertor.class)
    public BoolFieldConvertor boolFieldConvertor(BeanSearcherFieldConvertor config) {
        String[] falseValues = config.getBoolFalseValues();
        BoolFieldConvertor convertor = new BoolFieldConvertor();
        if (falseValues != null) {
            convertor.addFalseValues(falseValues);
        }
        return convertor;
    }

    @Bean
    @ConditionalOnProperty(name = "bean-searcher.field-convertor.use-date", havingValue = "true", matchIfMissing = true)
    @ConditionalOnMissingBean(DateFieldConvertor.class)
    public DateFieldConvertor dateFieldConvertor(BeanSearcherFieldConvertor config) {
        DateFieldConvertor convertor = new DateFieldConvertor();
        ZoneId zoneId = config.getZoneId();
        if (zoneId != null) {
            convertor.setZoneId(zoneId);
        }
        return convertor;
    }

    @Bean
    @ConditionalOnProperty(name = "bean-searcher.field-convertor.use-time", havingValue = "true", matchIfMissing = true)
    @ConditionalOnMissingBean(TimeFieldConvertor.class)
    public TimeFieldConvertor timeFieldConvertor() {
        return new TimeFieldConvertor();
    }

    @Bean
    @ConditionalOnProperty(name = "bean-searcher.field-convertor.use-enum", havingValue = "true", matchIfMissing = true)
    @ConditionalOnMissingBean(EnumFieldConvertor.class)
    public EnumFieldConvertor enumFieldConvertor(BeanSearcherFieldConvertor config) {
        EnumFieldConvertor convertor = new EnumFieldConvertor();
        convertor.setFailOnError(config.isEnumFailOnError());
        convertor.setIgnoreCase(config.isEnumIgnoreCase());
        return convertor;
    }

    @Bean
    @ConditionalOnProperty(name = "bean-searcher.field-convertor.use-list", havingValue = "true", matchIfMissing = true)
    @ConditionalOnMissingBean(ListFieldConvertor.class)
    public ListFieldConvertor listFieldConvertor(BeanSearcherFieldConvertor config,
                                                 ObjectProvider<List<ListFieldConvertor.Convertor<?>>> convertorsProvider) {
        ListFieldConvertor convertor = new ListFieldConvertor(config.getListItemSeparator());
        BeanSearcherAutoConfiguration.ifAvailable(convertorsProvider, convertor::setConvertors);
        return convertor;
    }

    @Bean
    @ConditionalOnProperty(name = "bean-searcher.field-convertor.use-date-format", havingValue = "true", matchIfMissing = true)
    @ConditionalOnMissingBean(DateFormatFieldConvertor.class)
    public DateFormatFieldConvertor dateFormatFieldConvertor(BeanSearcherFieldConvertor config) {
        DateFormatFieldConvertor convertor = new DateFormatFieldConvertor();
        Map<String, String> dateFormats = config.getDateFormats();
        if (dateFormats != null) {
            dateFormats.forEach((key, value) -> {
                // 由于在 yml 的 key 中的 `:` 会被自动过滤，所以这里做下特殊处理，在 yml 中可以用 `-` 替代
                String scope = key.replace('-', ':');
                convertor.setFormat(scope, value);
            });
        }
        ZoneId zoneId = config.getZoneId();
        if (zoneId != null) {
            convertor.setZoneId(zoneId);
        }
        return convertor;
    }

    @Bean
    @ConditionalOnProperty(name = "bean-searcher.field-convertor.use-string", havingValue = "true", matchIfMissing = true)
    @ConditionalOnMissingBean(StringFieldConvertor.class)
    public StringFieldConvertor stringFieldConvertor() {
        return new StringFieldConvertor();
    }

    @Bean
    @ConditionalOnProperty(name = "bean-searcher.field-convertor.use-b2-m", havingValue = "true")
    @ConditionalOnMissingBean(B2MFieldConvertor.class)
    public B2MFieldConvertor b2mFieldConvertor(ObjectProvider<List<FieldConvertor.BFieldConvertor>> convertors) {
        List<FieldConvertor.BFieldConvertor> list = convertors.getIfAvailable();
        if (list != null) {
            return new B2MFieldConvertor(list);
        }
        return new B2MFieldConvertor(Collections.emptyList());
    }

}
