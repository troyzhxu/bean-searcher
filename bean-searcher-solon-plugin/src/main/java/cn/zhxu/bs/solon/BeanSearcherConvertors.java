package cn.zhxu.bs.solon;

import cn.zhxu.bs.FieldConvertor;
import cn.zhxu.bs.convertor.*;
import cn.zhxu.bs.solon.prop.BeanSearcherFieldConvertor;
import cn.zhxu.bs.solon.prop.BeanSearcherParams;
import cn.zhxu.bs.solon.prop.BeanSearcherProperties;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Condition;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Configuration
public class BeanSearcherConvertors {

    //放到这儿，减少注入处理代码
    @Inject
    BeanSearcherProperties config;

    // ParamConvertor

    @Bean
    @Condition(onMissingBean = BoolParamConvertor.class)
    public BoolParamConvertor boolParamConvertor() {
        return new BoolParamConvertor();
    }

    @Bean
    @Condition(onMissingBean = NumberParamConvertor.class)
    public NumberParamConvertor numberParamConvertor() {
        return new NumberParamConvertor();
    }

    @Bean
    @Condition(onMissingBean = DateParamConvertor.class)
    public DateParamConvertor dateParamConvertor() {
        return new DateParamConvertor(config.getParams().getConvertor().getDateTarget());
    }

    @Bean
    @Condition(onMissingBean = TimeParamConvertor.class)
    public TimeParamConvertor timeParamConvertor() {
        return new TimeParamConvertor(config.getParams().getConvertor().getTimeTarget());
    }

    @Bean
    @Condition(onMissingBean = DateTimeParamConvertor.class)
    public DateTimeParamConvertor dateTimeParamConvertor() {
        BeanSearcherParams.Convertor conf = config.getParams().getConvertor();
        DateTimeParamConvertor convertor = new DateTimeParamConvertor(conf.getDateTimeTarget());
        convertor.setZoneId(conf.getZoneId());
        return convertor;
    }

    @Bean
    @Condition(onMissingBean = EnumParamConvertor.class)
    public EnumParamConvertor enumParamConvertor() {
        return new EnumParamConvertor();
    }

    // FieldConvertor

    @Bean
    @Condition(onMissingBean = NumberFieldConvertor.class,
            onProperty = "${bean-searcher.field-convertor.use-number:true}=true")
    public NumberFieldConvertor numberFieldConvertor() {
        return new NumberFieldConvertor();
    }

    @Bean
    @Condition(onMissingBean = StrNumFieldConvertor.class,
            onProperty = "${bean-searcher.field-convertor.use-str-num:true}=true")
    public StrNumFieldConvertor strNumFieldConvertor() {
        return new StrNumFieldConvertor();
    }

    @Bean
    @Condition(onMissingBean = BoolNumFieldConvertor.class,
            onProperty = "${bean-searcher.field-convertor.use-bool-num:true}=true")
    public BoolNumFieldConvertor boolNumFieldConvertor() {
        return new BoolNumFieldConvertor();
    }

    @Bean
    @Condition(onMissingBean = BoolFieldConvertor.class,
            onProperty = "${bean-searcher.field-convertor.use-bool:true}=true")
    public BoolFieldConvertor boolFieldConvertor() {
        String[] falseValues = config.getFieldConvertor().getBoolFalseValues();
        BoolFieldConvertor convertor = new BoolFieldConvertor();
        if (falseValues != null) {
            convertor.addFalseValues(falseValues);
        }
        return convertor;
    }

    @Bean
    @Condition(onMissingBean = DateFieldConvertor.class,
            onProperty = "${bean-searcher.field-convertor.use-date:true}=true")
    public DateFieldConvertor dateFieldConvertor() {
        DateFieldConvertor convertor = new DateFieldConvertor();
        ZoneId zoneId = config.getFieldConvertor().getZoneId();
        if (zoneId != null) {
            convertor.setZoneId(zoneId);
        }
        return convertor;
    }

    @Bean
    @Condition(onMissingBean = TimeFieldConvertor.class,
            onProperty = "${bean-searcher.field-convertor.use-time:true}=true")
    public TimeFieldConvertor timeFieldConvertor() {
        return new TimeFieldConvertor();
    }

    @Bean
    @Condition(onMissingBean = EnumFieldConvertor.class,
            onProperty = "${bean-searcher.field-convertor.use-enum:true}=true")
    public EnumFieldConvertor enumFieldConvertor() {
        BeanSearcherFieldConvertor conf = config.getFieldConvertor();
        EnumFieldConvertor convertor = new EnumFieldConvertor();
        convertor.setFailOnError(conf.isEnumFailOnError());
        convertor.setIgnoreCase(conf.isEnumIgnoreCase());
        return convertor;
    }

    // 在 springboot 那边，是用单独类处理的；在 solon 这边，用函数
    @Bean
    @Condition(onMissingBean = JsonFieldConvertor.class, onClass = cn.zhxu.xjson.JsonKit.class,
            onProperty = "${bean-searcher.field-convertor.use-json:true}=true")
    public JsonFieldConvertor jsonFieldConvertor() {
        BeanSearcherFieldConvertor conf = config.getFieldConvertor();
        return new JsonFieldConvertor(conf.isJsonFailOnError());
    }

    @Bean
    @Condition(onMissingBean = OracleTimestampFieldConvertor.class, onClass = oracle.sql.TIMESTAMP.class,
            onProperty = "${bean-searcher.field-convertor.use-oracle-timestamp:true}=true")
    public OracleTimestampFieldConvertor oracleTimestampFieldConvertor() {
        return new OracleTimestampFieldConvertor();
    }

    @Bean
    @Condition(onMissingBean = ListFieldConvertor.class,
            onProperty = "${bean-searcher.field-convertor.use-list:true}=true")
    @SuppressWarnings("all")
    public ListFieldConvertor listFieldConvertor(List<ListFieldConvertor.Convertor> convertors0) {
        List<ListFieldConvertor.Convertor<?>> convertors = new ArrayList<>();
        convertors0.forEach(convertors::add);
        BeanSearcherFieldConvertor conf = config.getFieldConvertor();
        ListFieldConvertor convertor = new ListFieldConvertor(conf.getListItemSeparator());
        if (convertors != null) {
            convertor.setConvertors(convertors);
        }
        return convertor;
    }

    @Bean
    @Condition(onMissingBean = DateFormatFieldConvertor.class,
            onProperty = "${bean-searcher.field-convertor.use-date-format:true}=true")
    public DateFormatFieldConvertor dateFormatFieldConvertor() {
        BeanSearcherFieldConvertor conf = config.getFieldConvertor();
        Map<String, String> dateFormats = conf.getDateFormats();
        ZoneId zoneId = conf.getZoneId();
        DateFormatFieldConvertor convertor = new DateFormatFieldConvertor();
        if (dateFormats != null) {
            dateFormats.forEach((key, value) -> {
                // 由于在 yml 的 key 中的 `:` 会被自动过滤，所以这里做下特殊处理，在 yml 中可以用 `-` 替代
                String scope = key.replace('-', ':');
                convertor.setFormat(scope, value);
            });
        }
        if (zoneId != null) {
            convertor.setZoneId(zoneId);
        }
        return convertor;
    }

    @Bean
    @Condition(onMissingBean = B2MFieldConvertor.class,
            onProperty = "${bean-searcher.field-convertor.use-b2-m}=true")
    public B2MFieldConvertor b2mFieldConvertor(List<FieldConvertor.BFieldConvertor> convertors) {
        if (convertors != null) {
            return new B2MFieldConvertor(convertors);
        }
        return new B2MFieldConvertor(Collections.emptyList());
    }

}
