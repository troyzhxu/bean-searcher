# 文件导出器

## BeanExporter

> since v4.5.0

`BeanExporter` 是数据导出的核心接口，提供了一组重载方法来满足不同导出场景的需求。

### 方法概览

所有方法均声明抛出 `IOException`，方法参数含义如下：

| 参数 | 说明 |
|------|------|
| `name` | 导出文件名（无需扩展名，框架会自动添加 `.csv`） |
| `beanClass` | SearchBean 类型，决定查询数据和导出字段 |
| `paraMap` | 额外的检索参数，不传则使用空 Map |
| `batchSize` | 每批次查询的条数，不传则使用默认值（`1000`） |
| `mapper` | 数据映射函数（since v4.7.0），可对每批数据做额外处理后再导出 |
| `writer` | 直接传入 `FileWriter` 实例，可完全控制输出目标（since v4.5.0） |

### 使用方式（SpringBoot / Solon）

在 SpringBoot 或 Solon 项目中，只要引入了 `bean-searcher-exporter` 依赖，框架会自动装配 `BeanExporter` Bean，在 Controller 中注入即可使用：

```java
@RestController
public class OrderController {

    @Autowired
    private BeanExporter beanExporter;

    // 方式一：最简用法，使用默认参数
    @GetMapping("/order/export")
    public void exportOrders(HttpServletResponse response) throws IOException {
        beanExporter.export("订单数据", OrderExportVO.class);
    }

    // 方式二：携带检索参数（与检索接口共用参数）
    @GetMapping("/order/export")
    public void exportOrders(@RequestParam Map<String, Object> paraMap) throws IOException {
        beanExporter.export("订单数据", OrderExportVO.class, paraMap);
    }

    // 方式三：指定批次大小
    @GetMapping("/order/export")
    public void exportOrders(@RequestParam Map<String, Object> paraMap) throws IOException {
        beanExporter.export("订单数据", OrderExportVO.class, paraMap, 500);
    }

    // 方式四：使用 mapper 对每批数据进行处理（since v4.7.0）
    @GetMapping("/order/export")
    public void exportOrders(@RequestParam Map<String, Object> paraMap) throws IOException {
        beanExporter.export("订单数据", OrderExportVO.class, paraMap, list -> {
            // 对每批数据进行额外处理，例如填充标签字段
            list.forEach(order -> order.setStatusLabel(translateStatus(order.getStatus())));
            return list;
        });
    }
}
```

::: tip 框架自动处理 HTTP 响应
在 SpringBoot 项目中，框架自动配置的 `FileWriter.Factory` 会读取当前请求上下文中的 `HttpServletResponse`，自动设置响应头（`Content-Disposition`、`Content-Type` 等），无需手动操作响应对象。
:::

### 使用 FileWriter（自定义输出目标）

若不希望直接输出到 HTTP 响应，或需要输出到文件系统、对象存储等其它目标，可以直接传入 `FileWriter` 实例：

```java
// 输出到本地文件
try (FileOutputStream fos = new FileOutputStream("/data/export/orders.csv")) {
    beanExporter.export(new CsvFileWriter(fos), OrderExportVO.class, paraMap);
}

// 输出到 OSS/对象存储
OutputStream ossStream = ossClient.openStream("orders.csv");
beanExporter.export(new CsvFileWriter(ossStream), OrderExportVO.class, paraMap);
```

## DefaultBeanExporter

`DefaultBeanExporter` 是 `BeanExporter` 的默认实现，包含以下核心可配置组件：

| 属性 | 类型 | 说明 |
|------|------|------|
| `fieldResolver` | `ExportFieldResolver` | 导出字段解析器，用于解析 `@Export` 注解 |
| `fileWriterFactory` | `FileWriter.Factory` | 文件写入器工厂，用于创建实际的输出通道 |
| `fileNamer` | `FileNamer` | 文件命名策略 |
| `delayPolicy` | `DelayPolicy` | 批次间延迟策略 |

### 配置项

在 SpringBoot / Solon 项目中，可通过配置文件调整 `DefaultBeanExporter` 的默认行为：

```properties
# 每批次查询的条数，默认 1000
bean-searcher.exporter.batch-size=1000

# 每批次查询后的延迟时间（用于降低数据库压力），默认 100ms
bean-searcher.exporter.batch-delay=100ms

# 最大并发导出线程数（超出后新请求进入等待），默认 10
bean-searcher.exporter.max-exporting-threads=10

# 最大总线程数（超出后拒绝新请求），默认 30
bean-searcher.exporter.max-threads=30

# 导出文件名是否追加时间戳（如 "订单数据_20240601120000.csv"），默认 true
bean-searcher.exporter.timestamp-filename=true

# 并发过高时返回给前端的提示信息
bean-searcher.exporter.too-many-requests-message=大人请息怒，当前导出数据的人实在太多了，请稍后再试一下子哈！
```

### 自定义 BeanExporter（非 Boot 项目）

```java
BeanSearcher beanSearcher = ...; // 已配置好的检索器

DefaultBeanExporter exporter = new DefaultBeanExporter(beanSearcher);

// 可选：自定义各组件
exporter.setFieldResolver(new DefaultExportFieldResolver(myExpresser, myFormatter));
exporter.setFileWriterFactory(filename -> new CsvFileWriter(new FileOutputStream("/export/" + filename)));
exporter.setFileNamer(name -> name + "_" + LocalDate.now());
```

## FileNamer

`FileNamer` 是文件命名策略接口，用于在传入 `name` 参数后生成最终的文件名。

```java
public interface FileNamer {
    String filename(String name);
}
```

框架提供了两种默认策略：

| 策略 | 说明 |
|------|------|
| `FileNamer.SELF` | 直接使用传入的 `name`，不做任何处理 |
| 时间戳策略（默认）| 在 `name` 后追加 `_yyyyMMddHHmmss` 时间戳，例如 `"订单数据_20240601120000"` |

时间戳策略由配置项 `bean-searcher.exporter.timestamp-filename=true`（默认）启用。

自定义文件命名：

```java
@Bean
public FileNamer fileNamer() {
    // 示例：追加日期（只有日期，没有时间）
    return name -> name + "_" + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
}
```

## ExportFieldResolver

`ExportFieldResolver` 负责解析 SearchBean 中所有被 `@Export` 注解标注的字段，并将其转换为 `ExportField` 列表。

```java
public interface ExportFieldResolver {
    List<ExportField> resolve(Class<?> beanClass);
    void clearCache();   // 清除解析缓存（since v4.8.0）
}
```

框架内置的 `DefaultExportFieldResolver` 实现了字段解析的缓存机制，每个 `beanClass` 的解析结果会被缓存，避免重复反射。

### 自定义 ExportFieldResolver

```java
@Bean
public ExportFieldResolver exportFieldResolver(Expresser expresser) {
    // 使用自定义 Formatter 替换默认格式化器
    return new DefaultExportFieldResolver(expresser, new MyFormatter());
}
```
