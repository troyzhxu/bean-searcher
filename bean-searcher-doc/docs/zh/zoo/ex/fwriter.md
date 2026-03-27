# 文件写出

## FileWriter

> since v4.5.0

`FileWriter` 是文件输出的核心接口，负责将导出字段和数据写入目标输出流。框架在导出过程中按以下顺序调用其方法：

```
writeStart(fields)           ← 导出开始（写表头等初始化操作）
  writeAndFlush(fields, batch1)  ← 每批数据到来时调用（可调用多次）
  writeAndFlush(fields, batch2)
  ...
writeStop(fields)            ← 导出结束（收尾清理）
```

若同时导出的人数超出上限，则调用：

```
onTooManyRequests()          ← 并发过高时调用（替代上述流程）
```

### 接口定义

```java
public interface FileWriter {

    /** 导出开始时调用（写表头） */
    void writeStart(List<ExportField> fields) throws IOException;

    /** 每批数据到来时调用 */
    void writeAndFlush(List<ExportField> fields, List<?> dataList) throws IOException;

    /** 导出结束时调用 */
    void writeStop(List<ExportField> fields) throws IOException;

    /** 并发过高时调用 */
    void onTooManyRequests() throws IOException;

    /** FileWriter 工厂接口 */
    interface Factory {
        FileWriter create(String filename) throws IOException;
    }
}
```

## CsvFileWriter

`CsvFileWriter` 是框架内置的 CSV 格式文件写入器，将数据以 UTF-8 编码写出为标准 CSV 文件。

### 构造方式

```java
// 从 OutputStream 构建（内部使用 UTF-8 编码）
new CsvFileWriter(response.getOutputStream())

// 从 Writer 构建（可自定义字符集）
new CsvFileWriter(new OutputStreamWriter(fos, StandardCharsets.UTF_8))
```

### 文件名处理

`CsvFileWriter` 提供了一个静态工具方法，用于自动为文件名添加 `.csv` 后缀：

```java
CsvFileWriter.withFileExt("订单数据")          // → "订单数据.csv"
CsvFileWriter.withFileExt("订单数据.csv")       // → "订单数据.csv"（不重复添加）
```

### CSV 转义规则

当字段文本中包含逗号 `,` 时，`CsvFileWriter` 会自动将其包裹在双引号中，并转义内部的双引号（`"` → `""`）：

```
无逗号：  hello world        → hello world
含逗号：  hello, world       → "hello, world"
含引号：  say "hello"        → "say ""hello"""
```

### 自定义 FileWriter

如果需要支持其他格式（如 Excel），可以实现 `FileWriter` 接口：

```java
public class ExcelFileWriter implements FileWriter {

    private final OutputStream output;
    private Workbook workbook;
    private Sheet sheet;
    private int rowIndex = 0;

    public ExcelFileWriter(OutputStream output) {
        this.output = output;
    }

    @Override
    public void writeStart(List<ExportField> fields) throws IOException {
        workbook = new SXSSFWorkbook();
        sheet = workbook.createSheet("数据");
        // 写表头
        Row header = sheet.createRow(rowIndex++);
        for (int i = 0; i < fields.size(); i++) {
            header.createCell(i).setCellValue(fields.get(i).getExName());
        }
    }

    @Override
    public void writeAndFlush(List<ExportField> fields, List<?> dataList) throws IOException {
        for (Object data : dataList) {
            Row row = sheet.createRow(rowIndex++);
            for (int i = 0; i < fields.size(); i++) {
                row.createCell(i).setCellValue(fields.get(i).text(data));
            }
        }
    }

    @Override
    public void writeStop(List<ExportField> fields) throws IOException {
        workbook.write(output);
        workbook.close();
    }

    @Override
    public void onTooManyRequests() throws IOException {
        throw new ExportException.TooManyRequests("导出人数过多，请稍后再试");
    }
}
```

## FileWriter.Factory

`FileWriter.Factory` 是创建 `FileWriter` 实例的工厂接口，每次发起导出时，框架会调用 `Factory.create(filename)` 来获取一个新的 `FileWriter`。

### 在 SpringBoot 项目中自定义

框架默认的 `FileWriter.Factory` 会将数据写入当前 HTTP 响应流。如需写入其他目标，声明一个 `FileWriter.Factory` Bean 即可覆盖默认实现：

```java
@Bean
public FileWriter.Factory fileWriterFactory() {
    // 示例：将导出文件保存到本地磁盘
    return filename -> {
        File file = new File("/data/export/" + filename + ".csv");
        return new CsvFileWriter(new FileOutputStream(file));
    };
}
```

### 在 SpringBoot 项目中同时输出到 HTTP 响应和文件

```java
@Bean
public FileWriter.Factory fileWriterFactory() {
    return filename -> {
        HttpServletResponse response = ...;  // 从 RequestContextHolder 获取
        String encodedName = URLEncoder.encode(filename + ".csv", StandardCharsets.UTF_8);
        response.setContentType("application/octet-stream");
        response.addHeader("Content-Disposition", "attachment; filename=" + encodedName);
        return new CsvFileWriter(response.getOutputStream());
    };
}
```

## Formatter（格式化器）

`Formatter` 接口用于对字段值进行格式化输出：

```java
public interface Formatter {
    String format(String format, Object value);
}
```

框架内置的 `Formatter.DEFAULT` 支持以下类型：

| 值类型 | 格式化方式 | 示例 |
|--------|-----------|------|
| `Date` | `SimpleDateFormat` | `"yyyy-MM-dd"` |
| `TemporalAccessor`（LocalDate 等） | `DateTimeFormatter` | `"yyyy-MM-dd HH:mm:ss"` |
| `Number` | `DecimalFormat` | `"#,##0.00"` |
| 其他 | `String.format` | `"%s（元）"` |

### 自定义 Formatter

```java
@Bean
public Formatter myFormatter() {
    return (format, value) -> {
        // 自定义格式化逻辑
        if (value instanceof BigDecimal) {
            return new DecimalFormat(format).format(value) + " 元";
        }
        return Formatter.DEFAULT.format(format, value);
    };
}
```

## Expresser（表达式计算器）

`Expresser` 接口用于计算 `@Export.expr` 中定义的表达式：

```java
public interface Expresser {
    /** 在表达式中用 @ 引用当前字段的值 */
    String VALUE_REF = "@";

    Object evaluate(String expr, Object obj, Object value);
}
```

- `expr`：表达式字符串；
- `obj`：当前数据对象（可在表达式中用字段名引用其属性）；
- `value`：当前字段的值（在表达式中用 `@` 引用）。

::: tip 框架已自动配置 Expresser
- **SpringBoot / Grails** 项目：自动注册基于 **SpEL** 的 `Expresser`；
- **Solon** 项目：自动注册基于 **SnEL** 的 `Expresser`；
- 如需替换，只需声明一个自定义的 `Expresser` Bean 即可。
:::

### 自定义 Expresser

```java
@Bean
public Expresser myExpresser() {
    return (expr, obj, value) -> {
        // 自定义表达式解析，例如使用 MVEL 引擎
        Map<String, Object> context = new HashMap<>();
        context.put("_", value);   // _ 代表当前字段值
        // 将对象所有字段的值也放入 context
        // ...
        return MVEL.eval(expr, context);
    };
}
```
