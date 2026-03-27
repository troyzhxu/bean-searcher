# Concurrency Control

Data export is typically a time-consuming, database-intensive operation. If many users trigger exports simultaneously, database pressure can spike dramatically. Bean Searcher Exporter provides a built-in **two-tier concurrency control** mechanism to address this.

## Concurrency Control Mechanism

`DefaultBeanExporter` uses two atomic counters to implement the two tiers:

```
User triggers an export request
        │
        ▼
┌────────────────────────────────────────────┐
│  threads >= maxThreads (default 30)        │  → Reject immediately: call onTooManyRequests()
└────────────────────────────────────────────┘
        │ within limit
        ▼
  threads.incrementAndGet()    (enter waiting/exporting state)
        │
        ▼  writeStart (write headers)
        │
        ▼
┌───────────────────────────────────────────────────────────┐
│  exportingThreads >= maxExportingThreads (default 10)     │  → Wait (10ms per loop)
└───────────────────────────────────────────────────────────┘
        │ slot available
        ▼
  exportingThreads.incrementAndGet()    (start active export)
        │
        ▼  fetch batch → writeAndFlush → batch delay → ...
        │
  exportingThreads.decrementAndGet()
  threads.decrementAndGet()
        │
        ▼  writeStop
```

### The Two Limits Explained

| Parameter | Description | Default |
|-----------|-------------|---------|
| `maxExportingThreads` | Max threads **actively querying the database** | `10` |
| `maxThreads` | Max threads **total** (including those waiting in queue) | `30` |

- When `exportingThreads` reaches `maxExportingThreads`, new requests **enter a queue** and wait for a slot to open — they are not rejected.
- When `threads` reaches `maxThreads`, new requests are **immediately rejected** via `FileWriter.onTooManyRequests()`.

## Configuring the Limits

In SpringBoot / Solon:

```properties
# Max concurrent exporting threads; default 10
bean-searcher.exporter.max-exporting-threads=10

# Max total threads; default 30
bean-searcher.exporter.max-threads=30
```

## Batch Delay Strategy

To further reduce database pressure, `DefaultBeanExporter` inserts a configurable delay between batch queries. This behavior is controlled by `DelayPolicy`.

### DelayPolicy Interface

```java
public interface DelayPolicy {
    /**
     * Returns the delay in milliseconds to apply after each batch query.
     * @param delayMills          base delay in ms (from the batch-delay config)
     * @param exportingThreads    current number of active exporting threads
     * @param maxExportingThreads configured maximum
     * @return actual delay in ms
     */
    int batchDelay(int delayMills, int exportingThreads, int maxExportingThreads);
}
```

### Built-in Strategy: RandomInflate

The default strategy is `DelayPolicy.RandomInflate`, which adds a random component to the base delay:

```
actual delay = random value in [base delay, base delay + inflation)
```

This randomness **staggers** the query timing of concurrent exports, preventing them from all hitting the database at the same moment.

### Configuring the Base Delay

```properties
# Base delay between batch queries; default 100ms
bean-searcher.exporter.batch-delay=100ms

# Other valid formats:
bean-searcher.exporter.batch-delay=200ms
bean-searcher.exporter.batch-delay=1s
```

### Custom Delay Policy

```java
@Bean
public DelayPolicy myDelayPolicy() {
    // Linearly scale delay with concurrency
    return (delayMills, exportingThreads, maxExportingThreads) -> {
        float ratio = (float) exportingThreads / maxExportingThreads;
        return (int) (delayMills * (1 + ratio));
    };
}
```

To disable delays entirely:

```java
@Bean
public DelayPolicy noDelayPolicy() {
    return (delayMills, exportingThreads, maxExportingThreads) -> 0;
}
```

## Handling Too-Many-Requests

When `threads >= maxThreads`, the framework calls `FileWriter.onTooManyRequests()`.

### Default Behavior

The auto-configured `FileWriter.Factory`:
- **SpringBoot / Grails**: responds with HTTP 429 and the `tooManyRequestsMessage` text;
- **Solon**: same — HTTP 429 with the configured message.

### Custom Message

```properties
bean-searcher.exporter.too-many-requests-message=System busy. Please try again later.
```

### Custom Handler

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
                response.getWriter().write("{\"code\":429,\"msg\":\"Too many requests. Please try again later.\"}");
            }
        };
    };
}
```

## Front-End Handling Example

Since `onTooManyRequests` returns HTTP 429, the front end can intercept it to display a user-friendly message:

```javascript
// axios example
axios.get('/api/order/export', {
    params: { /* search parameters */ },
    responseType: 'blob'
}).then(response => {
    const url = URL.createObjectURL(response.data);
    const a = document.createElement('a');
    a.href = url;
    a.click();
}).catch(error => {
    if (error.response?.status === 429) {
        alert('Too many exports in progress. Please try again later.');
    }
});
```
