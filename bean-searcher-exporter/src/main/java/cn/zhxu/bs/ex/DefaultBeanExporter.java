package cn.zhxu.bs.ex;

import cn.zhxu.bs.BeanSearcher;
import cn.zhxu.bs.util.MapBuilder;
import cn.zhxu.bs.util.MapUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Bean 导出器（导出成 CSV 文件）
 * @author Troy.Zhou @ 2025-08-28
 * @since v4.5.0
 */
public class DefaultBeanExporter implements BeanExporter {

    // 数据检索器，用于从数据库中加载数据
    private final BeanSearcher beanSearcher;
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

    private FileWriter.Factory fileWriterFactory;
    private ExprComputer exprComputer;
    private FileNamer fileNamer = FileNamer.SELF;

    public DefaultBeanExporter(BeanSearcher beanSearcher) {
        this(beanSearcher, 10, 30);
    }

    public DefaultBeanExporter(BeanSearcher beanSearcher, int maxExportingThreads, int maxThreads) {
        this(beanSearcher, 1000, Duration.ofMillis(100), maxExportingThreads, maxThreads);
    }

    public DefaultBeanExporter(BeanSearcher beanSearcher, int defaultBatchSize, Duration batchDelay, int maxExportingThreads, int maxThreads) {
        this.beanSearcher = Objects.requireNonNull(beanSearcher);
        this.defaultBatchSize = defaultBatchSize;
        this.batchDelay = Objects.requireNonNull(batchDelay);
        this.maxExportingThreads = maxExportingThreads;
        this.maxThreads = maxThreads;
    }

    @Override
    public <T> void export(String name, Class<T> beanClass) throws IOException {
        export(name, beanClass, null, defaultBatchSize);
    }

    @Override
    public <T> void export(String name, Class<T> beanClass, int batchSize) throws IOException {
        export(name, beanClass, null, batchSize);
    }

    @Override
    public <T> void export(String name, Class<T> beanClass, Map<String, Object> paraMap) throws IOException {
        export(name, beanClass, paraMap, defaultBatchSize);
    }

    @Override
    public <T> void export(String name, Class<T> beanClass, Map<String, Object> paraMap, int batchSize) throws IOException {
        if (name == null) {
            throw new ExportException("You must set a name before exporting.");
        }
        FileWriter.Factory factory = fileWriterFactory;
        if (factory == null) {
            throw new ExportException("You must set a fileWriterFactory before exporting.");
        }
        export(factory.create(fileNamer.filename(name)), beanClass, paraMap, batchSize);
    }

    @Override
    public <T> void export(FileWriter writer, Class<T> beanClass) throws IOException {
        export(writer, beanClass, null, defaultBatchSize);
    }

    @Override
    public <T> void export(FileWriter writer, Class<T> beanClass, int batchSize) throws IOException {
        export(writer, beanClass, null, batchSize);
    }

    @Override
    public <T> void export(FileWriter writer, Class<T> beanClass, Map<String, Object> paraMap) throws IOException {
        export(writer, beanClass, paraMap, defaultBatchSize);
    }

    @Override
    public <T> void export(FileWriter writer, Class<T> beanClass, Map<String, Object> paraMap, int batchSize) throws IOException {
        if (writer == null) {
            throw new ExportException("You must set a fileWriter before exporting.");
        }
        if (threads.get() >= maxThreads) {
            writer.writeTooManyRequests();
            return;
        }
        // 此处不必过于追求完美的并发控制，虽然在高并发下有可能多于 maxThreads 个线程进入此处，但概率极小且危害不大
        try {
            threads.incrementAndGet();
            List<ExportField> fields = resolveExportFields(beanClass);
            writer.writeStart(fields);
            while (exportingThreads.get() >= maxExportingThreads) {
                // 进入等待状态，等钱前面导出的人结束
                tryDelay(Duration.ofMillis(10));
            }
            try {
                // 进入导出状态，最多 maxExportingThreads 个人同时导出
                exportingThreads.incrementAndGet();
                doLoadDataAndExport(
                        writer, fields, beanClass,
                        paraMap != null ? paraMap : new HashMap<>(),
                        batchSize
                );
                writer.writeStop(fields);
            } finally {
                exportingThreads.decrementAndGet();
            }
        } finally {
            threads.decrementAndGet();
        }
    }

    protected List<ExportField> resolveExportFields(Class<?> clazz) {
        List<ExportField> exportFields = new ArrayList<>();
        Set<String> names = new HashSet<>();
        while (clazz != Object.class) {
            for (Field field : clazz.getDeclaredFields()) {
                int modifiers = field.getModifiers();
                String name = field.getName();
                if (field.isSynthetic() || Modifier.isStatic(modifiers)
                        || Modifier.isTransient(modifiers)
                        || names.contains(name)) {
                    continue;
                }
                Export prop = field.getAnnotation(Export.class);
                if (prop != null) {
                    field.setAccessible(true);
                    exportFields.add(new ExportField(exprComputer, field, prop));
                    names.add(name);
                }
            }
            clazz = clazz.getSuperclass();
        }
        exportFields.sort(Comparator.comparingInt(ExportField::idx));
        return exportFields;
    }

    protected <T> void doLoadDataAndExport(FileWriter writer, List<ExportField> fields,
                                           Class<T> beanClass, Map<String, Object> paraMap,
                                           int batchSize) throws IOException {
        MapBuilder builder = MapUtils.builder(paraMap);
        int pageNum = 0;
        do {
            List<T> list = beanSearcher.searchList(
                    beanClass,
                    builder.page(pageNum, batchSize).build()
            );
            writer.writeAndFlush(fields, list);
            if (list.size() >= batchSize) {
                tryDelay(batchDelay);
                pageNum++;
            } else {
                pageNum = -1;
            }
        } while (pageNum >= 0);
    }

    private void tryDelay(Duration delay) {
        long millis = delay.toMillis();
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

    public FileWriter.Factory getFileWriterFactory() {
        return fileWriterFactory;
    }

    public void setFileWriterFactory(FileWriter.Factory fileWriterFactory) {
        this.fileWriterFactory = fileWriterFactory;
    }

    public ExprComputer getExprComputer() {
        return exprComputer;
    }

    public void setExprComputer(ExprComputer exprComputer) {
        this.exprComputer = exprComputer;
    }

    public FileNamer getFileNamer() {
        return fileNamer;
    }

    public void setFileNamer(FileNamer fileNamer) {
        this.fileNamer = Objects.requireNonNull(fileNamer);
    }

}
