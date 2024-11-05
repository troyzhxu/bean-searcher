package cn.zhxu.bs.solon.prop;

import cn.zhxu.bs.BeanSearcher;
import cn.zhxu.bs.MapSearcher;
import cn.zhxu.bs.convertor.*;

import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

public class BeanSearcherFieldConvertor {

    /**
     * 是否启用 {@link NumberFieldConvertor }，默认为 true
     */
    private boolean useNumber = true;

    /**
     * 是否启用 {@link StrNumFieldConvertor }，默认为 true
     */
    private boolean useStrNum = true;

    /**
     * 是否启用 {@link BoolNumFieldConvertor }，默认为 true
     */
    private boolean useBoolNum = true;

    /**
     * 是否启用 {@link BoolFieldConvertor }，默认为 true
     */
    private boolean useBool = true;

    /**
     * 可转换为 false 的值，可配多个，默认为：`0,OFF,FALSE,N,NO,F`，将作为 {@link BoolFieldConvertor } 的参数，
     * @see BoolFieldConvertor#setFalseValues(String[])
     */
    private String[] boolFalseValues;

    /**
     * 是否启用 {@link DateFieldConvertor }，默认为 true
     */
    private boolean useDate = true;

    /**
     * 是否启用 {@link DateFormatFieldConvertor }，启用后，它会把 {@link MapSearcher } 检索结果中的日期字段格式化为指定格式的字符串，默认为 true，
     * 注意：并不是所有实体类中的日期字段都会被转换，它只转换 {@link #dateFormats } 指定的范围内的实体类与字段
     */
    private boolean useDateFormat = true;

    /**
     * 是否启用 {@link TimeFieldConvertor }，默认为 true
     */
    private boolean useTime = true;

    /**
     * 是否启用 {@link OracleTimestampFieldConvertor }，默认为 true
     * @since v4.4.0
     */
    private boolean useOracleTimestamp = true;

    /**
     * 时区 ID，将作为 {@link DateFieldConvertor } 与 {@link DateFormatFieldConvertor } 的参数，默认取值：{@link ZoneId#systemDefault() }，
     * @see DateFieldConvertor#setZoneId(ZoneId)
     * @see DateFormatFieldConvertor#setZoneId(ZoneId)
     */
    private ZoneId zoneId = null;

    /**
     * 日期/时间格式，{@link Map} 形式，键为 scope（生效范围，可以是 全类名.字段名、全类名:字段类型名、包名:字段类型名 或 包名，范围越小，使用优先级越高）, 值为 format（日期格式），
     * 它将作为 {@link DateFormatFieldConvertor } 的参数
     * @see DateFormatFieldConvertor#setFormat(String, String)
     */
    private Map<String, String> dateFormats = new HashMap<>();

    /**
     * 是否启用 {@link EnumFieldConvertor }，默认为 true
     */
    private boolean useEnum = true;

    /**
     * 当数据库值不能转换为对应的枚举时，是否抛出异常
     * @see EnumFieldConvertor#setFailOnError(boolean)
     * @since v3.7.0
     */
    private boolean enumFailOnError = true;

    /**
     * 当数据库值为字符串，匹配枚举时是否忽略大小写
     * @see EnumFieldConvertor#setIgnoreCase(boolean)
     * @since v3.7.0
     */
    private boolean enumIgnoreCase = false;

    /**
     * 是否启用 {@link B2MFieldConvertor }，默认为 false。
     * 未启用时，{@link MapSearcher } 检索结果的字段值 未经过 {@link cn.zhxu.bs.FieldConvertor.BFieldConvertor } 的转换，所以字段类型都是原始类，可能与实体类声明的类型不一致；
     * 启用后，将与 {@link BeanSearcher } 一样，检索结果的值类型 将被转换为 实体类中声明的类型。
     * 注意，当 {@link #useDateFormat } 为 true 时，日期时间类型的字段可能仍会被 {@link DateFormatFieldConvertor } 格式化为字符串。
     */
    private boolean useB2M = false;

    /**
     * 是否启用 {@link JsonFieldConvertor }（必要条件），默认为 true，但需要注意的是，即使该参数为 true, 也不一定能成功启用 {@link JsonFieldConvertor }，
     * 您必须还得添加 <a href="https://gitee.com/troyzhxu/xjsonkit">xjsonkit</a> 的 json 相关实现的依赖才可以，目前这些依赖有（你可以任选其一）：
     * <pre>
     * implementation 'cn.zhxu:xjsonkit-fastjson:最新版本' // Fastjson 实现
     * implementation 'cn.zhxu:xjsonkit-fastjson2:最新版本'// Fastjson2 实现
     * implementation 'cn.zhxu:xjsonkit-gson:最新版本'     // Gson 实现
     * implementation 'cn.zhxu:xjsonkit-jackson:最新版本'  // Jackson 实现
     * implementation 'cn.zhxu:xjsonkit-snack3:最新版本'   // Snack3 实现
     * </pre>
     * @since v4.0.0
     */
    private boolean useJson = true;

    /**
     * 使用 {@link JsonFieldConvertor } 时，当遇到某些值 JSON 解析异常时，是否抛出异常
     * @see JsonFieldConvertor#setFailOnError(boolean)
     * @since v4.0.1
     */
    private boolean jsonFailOnError = true;

    /**
     * 是否启用 {@link ListFieldConvertor }，默认为 true
     * @since v4.0.0
     */
    private boolean useList = true;

    /**
     * @see ListFieldConvertor#setItemSeparator(String)
     * @since v4.0.0
     */
    private String listItemSeparator = ",";

    public boolean isUseNumber() {
        return useNumber;
    }

    public void setUseNumber(boolean useNumber) {
        this.useNumber = useNumber;
    }

    public boolean isUseStrNum() {
        return useStrNum;
    }

    public void setUseStrNum(boolean useStrNum) {
        this.useStrNum = useStrNum;
    }

    public boolean isUseBoolNum() {
        return useBoolNum;
    }

    public void setUseBoolNum(boolean useBoolNum) {
        this.useBoolNum = useBoolNum;
    }

    public boolean isUseBool() {
        return useBool;
    }

    public void setUseBool(boolean useBool) {
        this.useBool = useBool;
    }

    public String[] getBoolFalseValues() {
        return boolFalseValues;
    }

    public void setBoolFalseValues(String[] boolFalseValues) {
        this.boolFalseValues = boolFalseValues;
    }

    public boolean isUseDate() {
        return useDate;
    }

    public void setUseDate(boolean useDate) {
        this.useDate = useDate;
    }

    public boolean isUseDateFormat() {
        return useDateFormat;
    }

    public void setUseDateFormat(boolean useDateFormat) {
        this.useDateFormat = useDateFormat;
    }

    public Map<String, String> getDateFormats() {
        return dateFormats;
    }

    public void setDateFormats(Map<String, String> dateFormats) {
        this.dateFormats = dateFormats;
    }

    public boolean isUseTime() {
        return useTime;
    }

    public void setUseTime(boolean useTime) {
        this.useTime = useTime;
    }

    public boolean isUseOracleTimestamp() {
        return useOracleTimestamp;
    }

    public void setUseOracleTimestamp(boolean useOracleTimestamp) {
        this.useOracleTimestamp = useOracleTimestamp;
    }

    public ZoneId getZoneId() {
        return zoneId;
    }

    public void setZoneId(ZoneId zoneId) {
        this.zoneId = zoneId;
    }

    public boolean isUseEnum() {
        return useEnum;
    }

    public void setUseEnum(boolean useEnum) {
        this.useEnum = useEnum;
    }

    public boolean isEnumFailOnError() {
        return enumFailOnError;
    }

    public void setEnumFailOnError(boolean enumFailOnError) {
        this.enumFailOnError = enumFailOnError;
    }

    public boolean isEnumIgnoreCase() {
        return enumIgnoreCase;
    }

    public void setEnumIgnoreCase(boolean enumIgnoreCase) {
        this.enumIgnoreCase = enumIgnoreCase;
    }

    public boolean isUseB2M() {
        return useB2M;
    }

    public void setUseB2M(boolean useB2M) {
        this.useB2M = useB2M;
    }

    public boolean isUseJson() {
        return useJson;
    }

    public void setUseJson(boolean useJson) {
        this.useJson = useJson;
    }

    public boolean isJsonFailOnError() {
        return jsonFailOnError;
    }

    public void setJsonFailOnError(boolean jsonFailOnError) {
        this.jsonFailOnError = jsonFailOnError;
    }

    public boolean isUseList() {
        return useList;
    }

    public void setUseList(boolean useList) {
        this.useList = useList;
    }

    public String getListItemSeparator() {
        return listItemSeparator;
    }

    public void setListItemSeparator(String listItemSeparator) {
        this.listItemSeparator = listItemSeparator;
    }

}
