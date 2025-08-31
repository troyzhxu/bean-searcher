package cn.zhxu.bs.solon;

import cn.zhxu.bs.BeanSearcher;
import cn.zhxu.bs.ex.*;
import cn.zhxu.bs.ex.Formatter;
import cn.zhxu.bs.solon.prop.BeanSearcherExProps;
import cn.zhxu.bs.solon.prop.BeanSearcherProperties;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Condition;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.handle.Context;
import org.noear.solon.expression.snel.SnEL;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Configuration
@Condition(onClass = BeanExporter.class, onBean = BeanSearcher.class)
public class BeanSearcherConfigOnExporter {

    //放到这儿，减少注入处理代码
    @Inject
    BeanSearcherProperties props;

    @Bean
    @Condition(onMissingBean = ExprComputer.class, onClass = SnEL.class)
    public ExprComputer exprComputer() {
        return (expr, obj, value) -> {
            Map<String, Object> context = new HashMap<>();
            List<Field> fields = getALlFields(obj.getClass());
            for (Field field : fields) {
                try {
                    context.put(field.getName(), field.get(obj));
                } catch (IllegalAccessException e) {
                    throw new ExportException("Can not reflect the value of " + field.getName() + " on " + obj.getClass().getName(), e);
                }
            }
            context.put("#v", value);
            return SnEL.eval(expr, context);
        };
    }

    static List<Field> getALlFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
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
                field.setAccessible(true);
                fields.add(field);
                names.add(name);
            }
            clazz = clazz.getSuperclass();
        }
        return fields;
    }

    @Bean
    @Condition(onMissingBean = ExportFieldResolver.class)
    public ExportFieldResolver exportFieldResolver(@Inject(required = false) ExprComputer exprComputer,
                                                   @Inject(required = false) Formatter formatter) {
        if (formatter != null) {
            return new DefaultExportFieldResolver(exprComputer, formatter);
        }
        return new DefaultExportFieldResolver(exprComputer);
    }

    @Bean
    @Condition(onMissingBean = FileWriter.Factory.class)
    public FileWriter.Factory fileWriterFactory() {
        return filename -> {
            Context context = Context.current();
            return new CsvFileWriter(context.outputStream()) {
                @Override
                public void writeStart(List<ExportField> fields) throws IOException {
                    String encodedName = URLEncoder.encode(CsvFileWriter.withFileExt(filename), StandardCharsets.UTF_8);
                    context.contentType("application/octet-stream");
                    context.headerAdd("Content-Disposition", "attachment; filename=" + encodedName);
                    context.headerAdd("Transfer-Encoding", "chunked");
                    super.writeStart(fields);
                }
                @Override
                public void writeTooManyRequests() throws IOException {
                    context.status(429);
                    context.contentType("application/text");
                    context.headerAdd("Transfer-Encoding", "no-cache");
                    context.headerAdd("Cache-Control", "no-cache");
                    writeAndFlush(props.getExporter().getTooManyRequestsMessage());
                }
            };
        };
    }

    @Bean
    @Condition(onMissingBean = FileNamer.class)
    public FileNamer fileNamer() {
        if (props.getExporter().isTimestampFilename()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("_yyyyMMddHHmmss");
            return name -> name + formatter.format(LocalDateTime.now());
        }
        return FileNamer.SELF;
    }

    @Bean
    @Condition(onMissingBean = BeanExporter.class)
    public BeanExporter beanExporter(BeanSearcher beanSearcher, ExportFieldResolver fieldResolver,
                                     @Inject(required = false) FileWriter.Factory fileWriterFactory,
                                     @Inject(required = false) FileNamer fileNamer) {
        BeanSearcherExProps conf = props.getExporter();
        DefaultBeanExporter beanExporter = new DefaultBeanExporter(
                beanSearcher,
                conf.getBatchSize(),
                conf.getBatchDelay(),
                conf.getMaxExportingThreads(),
                conf.getMaxThreads()
        );
        beanExporter.setFieldResolver(fieldResolver);
        if (fileWriterFactory != null) {
            beanExporter.setFileWriterFactory(fileWriterFactory);
        }
        if (fileNamer != null) {
            beanExporter.setFileNamer(fileNamer);
        }
        return beanExporter;
    }

}
