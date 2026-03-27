# File Exporter

## BeanExporter

> since v4.5.0

`BeanExporter` is the core export interface. It provides a set of overloaded methods to cover different export scenarios.

### Method Parameters

| Parameter | Description |
|-----------|-------------|
| `name` | Export file name (no extension needed — the framework appends `.csv` automatically) |
| `beanClass` | The SearchBean type that determines the query and export fields |
| `paraMap` | Additional search parameters; omit or pass `null` for an empty map |
| `batchSize` | Records per batch query; omit to use the configured default (`1000`) |
| `mapper` | A data-mapping function (since v4.7.0) to post-process each batch before writing |
| `writer` | A `FileWriter` instance for full control over the output target |

### Usage in SpringBoot / Solon

After adding the `bean-searcher-exporter` dependency, the framework auto-configures a `BeanExporter` Bean. Just inject it in your controller:

```java
@RestController
public class OrderController {

    @Autowired
    private BeanExporter beanExporter;

    // Simplest form — uses all defaults
    @GetMapping("/order/export")
    public void exportOrders() throws IOException {
        beanExporter.export("orders", OrderExportVO.class);
    }

    // With search parameters (shared with the search endpoint)
    @GetMapping("/order/export")
    public void exportOrders(@RequestParam Map<String, Object> paraMap) throws IOException {
        beanExporter.export("orders", OrderExportVO.class, paraMap);
    }

    // Custom batch size
    @GetMapping("/order/export")
    public void exportOrders(@RequestParam Map<String, Object> paraMap) throws IOException {
        beanExporter.export("orders", OrderExportVO.class, paraMap, 500);
    }

    // With a per-batch data mapper (since v4.7.0)
    @GetMapping("/order/export")
    public void exportOrders(@RequestParam Map<String, Object> paraMap) throws IOException {
        beanExporter.export("orders", OrderExportVO.class, paraMap, list -> {
            list.forEach(order -> order.setStatusLabel(translateStatus(order.getStatus())));
            return list;
        });
    }
}
```

::: tip HTTP Response is Handled Automatically
The auto-configured `FileWriter.Factory` in SpringBoot reads the current `HttpServletResponse` from `RequestContextHolder`, sets `Content-Disposition`, `Content-Type`, and other headers automatically. You do not need to touch the response object.
:::

::: warning Relax the Rate-Limit Before Exporting
Bean Searcher's default pagination guard allows at most **100** records per query (`maxAllowedSize`) and a maximum offset of **20000** (`maxAllowedOffset`). Because `BeanExporter` fetches data in batches of `batchSize` (default 1000), these defaults will cause an `IllegalParamException` during export.

Always add `maxSize` and `maxOffset` to the export SearchBean:

```java
@SearchBean(
    tables = "order",
    maxSize = 2000,             // must be >= batchSize
    maxOffset = Long.MAX_VALUE  // allow full-table export
)
public class OrderExportVO { ... }
```

See [Export Annotation → Rate-Limit Configuration](/zoo/ex/anno#rate-limit-configuration) for details.
:::

### Using FileWriter (Custom Output Target)

To write to a local file, object storage, or any other target instead of the HTTP response, pass a `FileWriter` directly:

```java
// Write to a local file
try (FileOutputStream fos = new FileOutputStream("/data/export/orders.csv")) {
    beanExporter.export(new CsvFileWriter(fos), OrderExportVO.class, paraMap);
}

// Write to cloud object storage
OutputStream ossStream = ossClient.openStream("orders.csv");
beanExporter.export(new CsvFileWriter(ossStream), OrderExportVO.class, paraMap);
```

## DefaultBeanExporter

`DefaultBeanExporter` is the default implementation of `BeanExporter`. Its core configurable components are:

| Property | Type | Description |
|----------|------|-------------|
| `fieldResolver` | `ExportFieldResolver` | Resolves `@Export`-annotated fields |
| `fileWriterFactory` | `FileWriter.Factory` | Creates the actual output channel |
| `fileNamer` | `FileNamer` | File naming strategy |
| `delayPolicy` | `DelayPolicy` | Inter-batch delay strategy |

### Configuration Properties

In SpringBoot / Solon, adjust `DefaultBeanExporter` behavior via configuration files:

```properties
# Records per batch query; default 1000
bean-searcher.exporter.batch-size=1000

# Delay between batch queries (reduces DB pressure); default 100ms
bean-searcher.exporter.batch-delay=100ms

# Max concurrent exporting threads (excess requests wait); default 10
bean-searcher.exporter.max-exporting-threads=10

# Max total threads (excess requests are rejected); default 30
bean-searcher.exporter.max-threads=30

# Append a timestamp to the file name (e.g. "orders_20240601120000.csv"); default true
bean-searcher.exporter.timestamp-filename=true

# Message returned to the client when concurrency is too high
bean-searcher.exporter.too-many-requests-message=Too many export requests. Please try again later.
```

### Manual Configuration (Non-Boot Projects)

```java
BeanSearcher beanSearcher = ...;

DefaultBeanExporter exporter = new DefaultBeanExporter(beanSearcher);

exporter.setFieldResolver(new DefaultExportFieldResolver(myExpresser, myFormatter));
exporter.setFileWriterFactory(filename -> new CsvFileWriter(new FileOutputStream("/export/" + filename)));
exporter.setFileNamer(name -> name + "_" + LocalDate.now());
```

## FileNamer

`FileNamer` is a strategy interface for deriving the final file name from the `name` argument passed to `export(...)`.

```java
public interface FileNamer {
    String filename(String name);
}
```

Two built-in strategies are available:

| Strategy | Description |
|----------|-------------|
| `FileNamer.SELF` | Returns the `name` unchanged |
| Timestamp strategy (default) | Appends `_yyyyMMddHHmmss` to `name`, e.g. `"orders_20240601120000"` |

The timestamp strategy is enabled by `bean-searcher.exporter.timestamp-filename=true` (the default).

Custom file naming:

```java
@Bean
public FileNamer fileNamer() {
    return name -> name + "_" + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
}
```

## ExportFieldResolver

`ExportFieldResolver` parses all `@Export`-annotated fields on a SearchBean and converts them into an ordered list of `ExportField` objects.

```java
public interface ExportFieldResolver {
    List<ExportField> resolve(Class<?> beanClass);
    void clearCache();   // clear the parse cache (since v4.8.0)
}
```

The built-in `DefaultExportFieldResolver` caches parse results per `beanClass` to avoid repeated reflection.

### Custom ExportFieldResolver

```java
@Bean
public ExportFieldResolver exportFieldResolver(Expresser expresser) {
    return new DefaultExportFieldResolver(expresser, new MyFormatter());
}
```
