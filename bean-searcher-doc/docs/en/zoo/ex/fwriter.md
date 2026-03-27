# File Writing

## FileWriter

> since v4.5.0

`FileWriter` is the core interface for writing export output. The framework calls its methods in the following order during an export:

```
writeStart(fields)              ← called once at the start (write headers)
  writeAndFlush(fields, batch1) ← called for each data batch (may be called many times)
  writeAndFlush(fields, batch2)
  ...
writeStop(fields)               ← called once at the end (cleanup)
```

If the concurrent export limit is exceeded before the export starts:

```
onTooManyRequests()             ← called instead of the above flow
```

### Interface Definition

```java
public interface FileWriter {

    /** Called at the start of export (e.g., write the header row) */
    void writeStart(List<ExportField> fields) throws IOException;

    /** Called for each batch of data */
    void writeAndFlush(List<ExportField> fields, List<?> dataList) throws IOException;

    /** Called at the end of export (cleanup) */
    void writeStop(List<ExportField> fields) throws IOException;

    /** Called when the concurrency limit is exceeded */
    void onTooManyRequests() throws IOException;

    /** Factory for creating FileWriter instances */
    interface Factory {
        FileWriter create(String filename) throws IOException;
    }
}
```

## CsvFileWriter

`CsvFileWriter` is the built-in CSV writer. It outputs data in UTF-8 encoded CSV format.

### Construction

```java
// From an OutputStream (uses UTF-8 internally)
new CsvFileWriter(response.getOutputStream())

// From a Writer (you control the charset)
new CsvFileWriter(new OutputStreamWriter(fos, StandardCharsets.UTF_8))
```

### File Extension Helper

```java
CsvFileWriter.withFileExt("orders")       // → "orders.csv"
CsvFileWriter.withFileExt("orders.csv")   // → "orders.csv" (no duplicate extension)
```

### CSV Escaping Rules

When a field value contains a comma (`,`), `CsvFileWriter` wraps the value in double quotes and escapes any embedded double quotes (`"` → `""`):

```
No comma:     hello world     → hello world
With comma:   hello, world    → "hello, world"
With quotes:  say "hello"     → "say ""hello"""
```

### Custom FileWriter

To support other formats such as Excel, implement `FileWriter`:

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
        sheet = workbook.createSheet("Data");
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
        throw new ExportException.TooManyRequests("Too many export requests.");
    }
}
```

## FileWriter.Factory

`FileWriter.Factory` creates a new `FileWriter` instance each time an export is triggered. The framework calls `Factory.create(filename)` at the beginning of every export.

### Custom Factory in SpringBoot

The default factory writes to the current HTTP response. Override it by declaring a `FileWriter.Factory` Bean:

```java
@Bean
public FileWriter.Factory fileWriterFactory() {
    // Write to local disk instead
    return filename -> {
        File file = new File("/data/export/" + filename + ".csv");
        return new CsvFileWriter(new FileOutputStream(file));
    };
}
```

## Formatter

`Formatter` formats a field value (or the result of an `expr` expression) into a string:

```java
public interface Formatter {
    String format(String format, Object value);
}
```

The built-in `Formatter.DEFAULT` supports:

| Value Type | Formatting | Example |
|-----------|------------|---------|
| `Date` | `SimpleDateFormat` | `"yyyy-MM-dd"` |
| `TemporalAccessor` (LocalDate, etc.) | `DateTimeFormatter` | `"yyyy-MM-dd HH:mm:ss"` |
| `Number` | `DecimalFormat` | `"#,##0.00"` |
| Other | `String.format` | `"%s (USD)"` |

### Custom Formatter

```java
@Bean
public Formatter myFormatter() {
    return (format, value) -> {
        if (value instanceof BigDecimal) {
            return new DecimalFormat(format).format(value) + " USD";
        }
        return Formatter.DEFAULT.format(format, value);
    };
}
```

## Expresser

`Expresser` evaluates the expression defined in `@Export.expr`:

```java
public interface Expresser {
    /** @ in expressions refers to the current field's value */
    String VALUE_REF = "@";

    Object evaluate(String expr, Object obj, Object value);
}
```

- `expr`: the expression string;
- `obj`: the current data object (fields accessible by name in the expression);
- `value`: the current field's value (referenced as `@` in the expression).

::: tip Auto-Configured
- **SpringBoot / Grails**: a **SpEL**-based `Expresser` is auto-registered;
- **Solon**: a **SnEL**-based `Expresser` is auto-registered;
- Override by declaring a custom `Expresser` Bean.
:::

### Custom Expresser

```java
@Bean
public Expresser myExpresser() {
    return (expr, obj, value) -> {
        Map<String, Object> context = new HashMap<>();
        context.put("_", value);   // _ refers to the current field value
        // add all object fields to context...
        return MVEL.eval(expr, context);
    };
}
```
