package cn.zhxu.bs.ex;

import cn.zhxu.bs.BeanSearcher;
import cn.zhxu.bs.util.MapBuilder;
import cn.zhxu.bs.util.MapUtils;

import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

/**
 * Bean 导出器（导出成 CSV 文件）
 * @author Troy.Zhou @ 2025-08-28
 * @since v4.5.0
 */
public class DefaultBeanExporter implements BeanExporter {

    // 数据检索器，用于从数据库中加载数据
    private final BeanSearcher beanSearcher;
    // 数据加载起始页码，默认 0
    private final int startPage;
    // 每批次查询的条数，默认 1000
    private final int defaultBatchSize;
    // 每批次查询后的延迟时间，默认 100毫秒，用于降低数据库压力
    private final Duration batchDelay;
    // 最大同时导出的并发数，当同时导出操作的人达到这个值（默认 10），新导出的人会处于等待状态
    private final int maxExportingThreads;
    // 最大线程数，当同时导出操作的人太多（默认 30），新导出的人会收到稍后操作的提示
    private final int maxThreads;

    // 正在导出的线程数
    private final AtomicInteger exportingThreads = new AtomicInteger();
    // 导出+等待中的总线程数
    private final AtomicInteger threads = new AtomicInteger();

    private ExportFieldResolver fieldResolver = new DefaultExportFieldResolver();
    private FileWriter.Factory fileWriterFactory;
    private FileNamer fileNamer = FileNamer.SELF;
    private DelayPolicy delayPolicy = new DelayPolicy.RandomInflate();

    public DefaultBeanExporter(BeanSearcher beanSearcher) {
        this(beanSearcher, 10, 30);
    }

    public DefaultBeanExporter(BeanSearcher beanSearcher, int maxExportingThreads, int maxThreads) {
        this(beanSearcher, 0, 1000, Duration.ofMillis(100), maxExportingThreads, maxThreads);
    }

    public DefaultBeanExporter(BeanSearcher beanSearcher, int startPage, int defaultBatchSize, Duration batchDelay, int maxExportingThreads, int maxThreads) {
        this.beanSearcher = Objects.requireNonNull(beanSearcher);
        this.startPage = startPage;
        this.defaultBatchSize = defaultBatchSize;
        this.batchDelay = Objects.requireNonNull(batchDelay);
        this.maxExportingThreads = maxExportingThreads;
        this.maxThreads = maxThreads;
    }

    @Override
    public <T> void export(String name, Class<T> beanClass) throws IOException {
        export(name, beanClass, null, defaultBatchSize, null);
    }

    @Override
    public <T> void export(String name, Class<T> beanClass, int batchSize) throws IOException {
        export(name, beanClass, null, batchSize, null);
    }

    @Override
    public <T> void export(String name, Class<T> beanClass, Map<String, Object> paraMap) throws IOException {
        export(name, beanClass, paraMap, defaultBatchSize, null);
    }

    @Override
    public <T> void export(String name, Class<T> beanClass, Map<String, Object> paraMap, int batchSize) throws IOException {
        export(name, beanClass, paraMap, batchSize, null);
    }

    @Override
    public <T> void export(String name, Class<T> beanClass, Function<List<T>, List<T>> mapper) throws IOException {
        export(name, beanClass, null, defaultBatchSize, mapper);
    }

    @Override
    public <T> void export(String name, Class<T> beanClass, int batchSize, Function<List<T>, List<T>> mapper) throws IOException {
        export(name, beanClass, null, batchSize, mapper);
    }

    @Override
    public <T> void export(String name, Class<T> beanClass, Map<String, Object> paraMap, Function<List<T>, List<T>> mapper) throws IOException {
        export(name, beanClass, paraMap, defaultBatchSize, mapper);
    }

    @Override
    public <T> void export(String name, Class<T> beanClass, Map<String, Object> paraMap, int batchSize, Function<List<T>, List<T>> mapper) throws IOException {
        if (name == null) {
            throw new ExportException("You must set a name before exporting.");
        }
        FileWriter.Factory factory = fileWriterFactory;
        if (factory == null) {
            throw new ExportException("You must set a fileWriterFactory before exporting.");
        }
        export(factory.create(fileNamer.filename(name)), beanClass, paraMap, batchSize, mapper);
    }

    @Override
    public <T> void export(FileWriter writer, Class<T> beanClass) throws IOException {
        export(writer, beanClass, null, defaultBatchSize, null);
    }

    @Override
    public <T> void export(FileWriter writer, Class<T> beanClass, int batchSize) throws IOException {
        export(writer, beanClass, null, batchSize, null);
    }

    @Override
    public <T> void export(FileWriter writer, Class<T> beanClass, Map<String, Object> paraMap) throws IOException {
        export(writer, beanClass, paraMap, defaultBatchSize, null);
    }

    @Override
    public <T> void export(FileWriter writer, Class<T> beanClass, Map<String, Object> paraMap, int batchSize) throws IOException {
        export(writer, beanClass, paraMap, batchSize, null);
    }

    @Override
    public <T> void export(FileWriter writer, Class<T> beanClass, Function<List<T>, List<T>> mapper) throws IOException {
        export(writer, beanClass, null, defaultBatchSize, mapper);
    }

    @Override
    public <T> void export(FileWriter writer, Class<T> beanClass, int batchSize, Function<List<T>, List<T>> mapper) throws IOException {
        export(writer, beanClass, null, batchSize, mapper);
    }

    @Override
    public <T> void export(FileWriter writer, Class<T> beanClass, Map<String, Object> paraMap, Function<List<T>, List<T>> mapper) throws IOException {
        export(writer, beanClass, paraMap, defaultBatchSize, mapper);
    }

    @Override
    public <T> void export(FileWriter writer, Class<T> beanClass, Map<String, Object> paraMap, int batchSize, Function<List<T>, List<T>> mapper) throws IOException {
        if (writer == null) {
            throw new ExportException("You must set a fileWriter before exporting.");
        }
        if (batchSize < 1) {
            throw new ExportException("The batchSize must be greater than 0.");
        }
        if (threads.get() >= maxThreads) {
            writer.onTooManyRequests();
            return;
        }
        // 此处不必过于追求完美的并发控制，虽然在高并发下有可能多于 maxThreads 个线程进入此处，但概率极小且危害不大
        try {
            threads.incrementAndGet();
            List<ExportField> fields = fieldResolver.resolve(beanClass)
                    .stream()
                    .filter(field -> field.onlyIf(paraMap))
                    .toList();
            writer.writeStart(fields);
            while (exportingThreads.get() >= maxExportingThreads) {
                // 进入等待状态，等钱前面导出的人结束
                tryDelay(10);
            }
            try {
                // 进入导出状态，最多 maxExportingThreads 个人同时导出
                exportingThreads.incrementAndGet();
                loadDataAndExportToWriter(
                        writer, fields, beanClass,
                        paraMap != null ? paraMap : new HashMap<>(),
                        batchSize, mapper
                );
                writer.writeStop(fields);
            } finally {
                exportingThreads.decrementAndGet();
            }
        } finally {
            threads.decrementAndGet();
        }
    }

    protected <T> void loadDataAndExportToWriter(FileWriter writer, List<ExportField> fields, Class<T> beanClass,
                                                 Map<String, Object> paraMap, int batchSize,
                                                 Function<List<T>, List<T>> mapper) throws IOException {
        MapBuilder builder = MapUtils.builder(paraMap);
        int pageNum = startPage;
        while (true) {
            List<T> list = beanSearcher.searchList(beanClass, builder.page(pageNum, batchSize).build());
            writer.writeAndFlush(fields, mapper != null ? mapper.apply(list) : list);
            if (list.size() < batchSize) {
                break;
            }
            tryDelay(delayPolicy.batchDelay((int) batchDelay.toMillis(), exportingThreads.get(), maxExportingThreads));
            pageNum++;
        }
    }

    private void tryDelay(long millis) {
        if (millis == 0) {
            return;
        }
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }

    public BeanSearcher getBeanSearcher() {
        return beanSearcher;
    }

    public int getStartPage() {
        return startPage;
    }

    public int getDefaultBatchSize() {
        return defaultBatchSize;
    }

    public Duration getBatchDelay() {
        return batchDelay;
    }

    public int getMaxExportingThreads() {
        return maxExportingThreads;
    }

    public int getMaxThreads() {
        return maxThreads;
    }

    public AtomicInteger getExportingThreads() {
        return exportingThreads;
    }

    public AtomicInteger getThreads() {
        return threads;
    }

    public ExportFieldResolver getFieldResolver() {
        return fieldResolver;
    }

    public void setFieldResolver(ExportFieldResolver fieldResolver) {
        this.fieldResolver = Objects.requireNonNull(fieldResolver);
    }

    public FileWriter.Factory getFileWriterFactory() {
        return fileWriterFactory;
    }

    public void setFileWriterFactory(FileWriter.Factory fileWriterFactory) {
        this.fileWriterFactory = fileWriterFactory;
    }

    public FileNamer getFileNamer() {
        return fileNamer;
    }

    public void setFileNamer(FileNamer fileNamer) {
        this.fileNamer = Objects.requireNonNull(fileNamer);
    }

    public DelayPolicy getDelayPolicy() {
        return delayPolicy;
    }

    public void setDelayPolicy(DelayPolicy delayPolicy) {
        this.delayPolicy = Objects.requireNonNull(delayPolicy);
    }

}
