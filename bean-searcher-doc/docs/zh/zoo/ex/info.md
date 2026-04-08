# 介绍（since v4.5）

## 版本说明

从 v4.5 起，Bean Searcher 提供了 Bean Searcher Exporter 组件。它可以根据 SearchBean 直接导出到数据文件。

## 数据导出

数据导出系统为 BeanSearcher 提供 **内存高效**、**实时** 的数据导出功能。它支持将大型数据集流式传输到文件（默认 CSV 格式），并内置 **并发控制**、自适应数据库负载管理和基于表达式的字段转换功能。导出器使用 BeanSearcher 分批检索数据，通过可配置的转换进行处理，然后将其写入输出流，而无需将整个数据集加载到内存中。

## 架构图

导出系统由多个松耦合的组件组成，这些组件协同工作，以提供灵活、高性能的数据导出：

![](/ex_arch.png)

## 快速上手

### 第一步：引入依赖

在原有 `bean-searcher-boot-starter`（或 `bean-searcher-solon-plugin`）基础上，追加 `bean-searcher-exporter` 依赖：

::: code-group
```groovy [Gradle]
implementation 'cn.zhxu:bean-searcher-exporter:4.8.6'
```
```xml [Maven]
<dependency>
    <groupId>cn.zhxu</groupId>
    <artifactId>bean-searcher-exporter</artifactId>
    <version>4.8.6</version>
</dependency>
```
:::

### 第二步：在 SearchBean 上标注 @Export

只需在需要导出的字段上添加 `@Export` 注解：

```java
@SearchBean(
    tables = "order",
    maxSize = 2000,             // 放开单批查询条数限制（须 >= batchSize，默认 1000）
    maxOffset = Long.MAX_VALUE  // 放开分页深度限制，允许导出全量数据
)
public class OrderExportVO {

    @Export(name = "订单编号", idx = 1)
    private String orderNo;

    @Export(name = "买家姓名", idx = 2)
    private String buyerName;

    @Export(name = "下单时间", idx = 3, format = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Export(name = "金额（元）", idx = 4, format = "#,##0.00")
    private BigDecimal amount;

    // 省略 Getter / Setter
}
```

### 第三步：在 Controller 中注入 BeanExporter 并导出

```java
@RestController
public class OrderController {

    @Autowired
    private BeanExporter beanExporter;

    @GetMapping("/order/export")
    public void export(@RequestParam Map<String, Object> paraMap) throws IOException {
        // 一行代码完成导出，文件将以流的形式推送给浏览器下载
        beanExporter.export("订单数据", OrderExportVO.class, paraMap);
    }
}
```

::: tip 无需操作 HttpServletResponse
框架会自动获取当前请求的 `HttpServletResponse`，自动设置响应头并将数据流写入响应输出流，你只需调用 `export(...)` 即可。
:::

## 组件说明

| 组件 | 说明 |
|------|------|
| [`@Export`](/zoo/ex/anno) | 字段导出注解，声明导出列名、顺序、表达式、格式及条件 |
| [`BeanExporter`](/zoo/ex/exporter) | 文件导出器接口，提供多种重载导出方法 |
| [`FileWriter`](/zoo/ex/fwriter) | 文件写出接口，内置 `CsvFileWriter` 实现 |
| [`Formatter`](/zoo/ex/fwriter#formatter-格式化器) | 格式化器，支持日期、数字、字符串 |
| [`Expresser`](/zoo/ex/fwriter#expresser-表达式计算器) | 表达式计算器，支持 SpEL / SnEL |
| [`DelayPolicy`](/zoo/ex/control) | 批次延迟策略，用于降低数据库压力 |
