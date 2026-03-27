# 并发控制

数据导出通常是一个耗时且占用数据库资源的操作。如果同时有大量用户触发导出，可能会对数据库造成严重压力，甚至导致服务不可用。Bean Searcher Exporter 内置了一套**双层并发控制**机制来应对这一问题。

## 并发控制机制

`DefaultBeanExporter` 使用两个原子计数器实现双层并发控制：

```
用户发起导出请求
      │
      ▼
┌─────────────────────────────────────────────┐
│  threads（总线程数）>= maxThreads（默认 30） │  → 立即拒绝：调用 onTooManyRequests()
└─────────────────────────────────────────────┘
      │ 未超限
      ▼
  threads.incrementAndGet()   （进入等待/导出状态）
      │
      ▼ 写出表头（writeStart）
      │
      ▼
┌──────────────────────────────────────────────────────────┐
│  exportingThreads（正在导出数）>= maxExportingThreads（10）│  → 进入等待（每次等 10ms）
└──────────────────────────────────────────────────────────┘
      │ 队列中有空位
      ▼
  exportingThreads.incrementAndGet()   （进入正式导出阶段）
      │
      ▼ 分批查询数据 → writeAndFlush → 批间延迟 → ...
      │
      ▼
  exportingThreads.decrementAndGet()
  threads.decrementAndGet()
      │
      ▼ 写出结束（writeStop）
```

### 两个并发上限的含义

| 参数 | 说明 | 默认值 |
|------|------|--------|
| `maxExportingThreads` | 最大**正在导出**（真正查询数据库）的线程数 | `10` |
| `maxThreads` | 最大**总参与**线程数（含等待中的线程） | `30` |

- 当导出线程数达到 `maxExportingThreads` 时，新请求**不会被拒绝**，而是进入等待队列，等前面的导出完成后自动开始；
- 当总线程数达到 `maxThreads` 时，新请求**直接被拒绝**，调用 `FileWriter.onTooManyRequests()`。

## 配置并发上限

在 SpringBoot / Solon 项目中，通过配置文件调整：

```properties
# 最大并发导出线程数，超出后新请求排队等待，默认 10
bean-searcher.exporter.max-exporting-threads=10

# 最大总线程数，超出后直接拒绝，默认 30
bean-searcher.exporter.max-threads=30
```

## 批次延迟策略

为了进一步降低对数据库的压力，`DefaultBeanExporter` 在每批次查询之间会插入一段延迟时间，这一行为由 `DelayPolicy` 控制。

### DelayPolicy 接口

```java
public interface DelayPolicy {
    /**
     * 获取每批次查询后的延迟时间
     * @param delayMills          基础延迟时间（毫秒，来自配置项 batch-delay）
     * @param exportingThreads    当前正在导出的线程数
     * @param maxExportingThreads 最大并发导出线程数
     * @return 实际延迟时间（毫秒）
     */
    int batchDelay(int delayMills, int exportingThreads, int maxExportingThreads);
}
```

### 内置策略：RandomInflate（随机放大）

框架内置的默认策略是 `DelayPolicy.RandomInflate`，其逻辑为：

```
实际延迟 = 随机值（范围：[基础延迟, 基础延迟 + 放大量)）
```

这种随机性可以**打散**多个并发导出请求的查询时间点，避免它们同时命中数据库。

### 配置基础延迟时间

```properties
# 每批次查询后的基础延迟时间，默认 100ms
bean-searcher.exporter.batch-delay=100ms

# 也支持其他时间格式
bean-searcher.exporter.batch-delay=200ms
bean-searcher.exporter.batch-delay=1s
```

### 自定义延迟策略

```java
@Bean
public DelayPolicy myDelayPolicy() {
    // 示例：根据并发数线性放大延迟
    return (delayMills, exportingThreads, maxExportingThreads) -> {
        // 并发越高，延迟越长
        float ratio = (float) exportingThreads / maxExportingThreads;
        return (int) (delayMills * (1 + ratio));
    };
}
```

若需要完全禁用延迟，可返回 `0`：

```java
@Bean
public DelayPolicy noDelayPolicy() {
    return (delayMills, exportingThreads, maxExportingThreads) -> 0;
}
```

## 处理并发过高的情况

当 `threads >= maxThreads` 时，框架会调用 `FileWriter.onTooManyRequests()` 方法。

### 默认行为

框架自动配置的 `FileWriter.Factory` 在这种情况下：
- **SpringBoot / Grails**：返回 HTTP 429 状态码，并输出 `tooManyRequestsMessage` 配置的文案；
- **Solon**：返回 HTTP 429 状态码，输出相同的提示文案。

### 自定义提示文案

```properties
bean-searcher.exporter.too-many-requests-message=系统繁忙，请稍后再试
```

### 自定义处理逻辑

如果希望实现更复杂的逻辑（例如返回 JSON 格式的错误响应），可以在自定义 `FileWriter.Factory` 中覆盖 `onTooManyRequests()`：

```java
@Bean
public FileWriter.Factory fileWriterFactory() {
    return filename -> {
        HttpServletResponse response = ...;
        return new CsvFileWriter(response.getOutputStream()) {
            @Override
            public void onTooManyRequests() throws IOException {
                response.setStatus(429);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":429,\"msg\":\"导出人数过多，请稍后再试\"}");
            }
        };
    };
}
```

## 前端处理示例

由于 `onTooManyRequests` 返回的是 HTTP 429 状态码，前端可以通过拦截响应状态码来提示用户：

```javascript
// 以 axios 为例
axios.get('/api/order/export', {
    params: { /* 检索参数 */ },
    responseType: 'blob'
}).then(response => {
    // 正常导出：创建下载链接
    const url = URL.createObjectURL(response.data);
    const a = document.createElement('a');
    a.href = url;
    a.click();
}).catch(error => {
    if (error.response?.status === 429) {
        alert('当前导出人数过多，请稍后再试！');
    }
});
```
