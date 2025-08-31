package cn.zhxu.bs.boot;

import cn.zhxu.bs.BeanSearcher;
import cn.zhxu.bs.boot.prop.BeanSearcherExProps;
import cn.zhxu.bs.boot.prop.BeanSearcherParams;
import cn.zhxu.bs.ex.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Configuration
@ConditionalOnClass(BeanExporter.class)
@EnableConfigurationProperties(BeanSearcherExProps.class)
public class BeanSearcherConfigOnExporter {

    @Bean
    @ConditionalOnMissingBean(ExprComputer.class)
    public ExprComputer exprComputer() {
        ExpressionParser expressionParser = new SpelExpressionParser();
        return (expr, obj, value) -> {
            EvaluationContext context = new StandardEvaluationContext(obj);
            context.setVariable("v", value);
            return expressionParser.parseExpression(expr).getValue(context);
        };
    }

    @Bean
    @ConditionalOnMissingBean(ExportFieldResolver.class)
    public ExportFieldResolver exportFieldResolver(ExprComputer exprComputer, ObjectProvider<Formatter> formatter) {
        var f = formatter.getIfAvailable();
        if (f != null) {
            return new DefaultExportFieldResolver(exprComputer, f);
        }
        return new DefaultExportFieldResolver(exprComputer);
    }

    @Configuration
    @ConditionalOnClass(RequestContextHolder.class)
    public static class WebMvcConfiguration {

        static HttpServletResponse requireResponse() {
            RequestAttributes attributes = RequestContextHolder.currentRequestAttributes();
            if (attributes instanceof ServletRequestAttributes) {
                HttpServletResponse response = ((ServletRequestAttributes) attributes).getResponse();
                if (response != null) {
                    return response;
                }
            }
            throw new IllegalStateException("Can not get Current response from RequestContextHolder!");
        }

        @Bean
        @ConditionalOnMissingBean(FileWriter.Factory.class)
        public FileWriter.Factory fileWriterFactory(BeanSearcherExProps props) {
            return filename -> {
                HttpServletResponse response = requireResponse();
                return new CsvFileWriter(response.getOutputStream()) {
                    @Override
                    public void writeStart(List<ExportField> fields) throws IOException {
                        String encodedName = URLEncoder.encode(CsvFileWriter.withFileExt(filename), StandardCharsets.UTF_8);
                        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
                        response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + encodedName);
                        response.addHeader(HttpHeaders.TRANSFER_ENCODING, "chunked");
                        super.writeStart(fields);
                    }
                    @Override
                    public void onTooManyRequests() throws IOException {
                        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                        response.setContentType("application/text");
                        response.setHeader(HttpHeaders.CACHE_CONTROL, "no-cache");
                        writeAndFlush(props.getTooManyRequestsMessage());
                    }
                };
            };
        }

        @Bean
        @ConditionalOnMissingBean(FileNamer.class)
        public FileNamer fileNamer(BeanSearcherExProps props) {
            if (props.isTimestampFilename()) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("_yyyyMMddHHmmss");
                return name -> name + formatter.format(LocalDateTime.now());
            }
            return FileNamer.SELF;
        }

    }

    @Bean
//  @ConditionalOnBean(BeanSearcher.class)  这里不能使用这个条件，否则该 Bean 会注册不了，这可能是 SpringBoot 的 BUG
    @ConditionalOnMissingBean(BeanExporter.class)
    @ConditionalOnProperty(name = "bean-searcher.use-bean-searcher", havingValue = "true", matchIfMissing = true)
    public BeanExporter beanExporter(BeanSearcher beanSearcher, ExportFieldResolver fieldResolver,
                                     ObjectProvider<FileWriter.Factory> fileWriterFactory,
                                     ObjectProvider<FileNamer> fileNamer,
                                     BeanSearcherExProps props, BeanSearcherParams params) {
        DefaultBeanExporter beanExporter = new DefaultBeanExporter(
                beanSearcher,
                params.getPagination().getStart(),
                props.getBatchSize(),
                props.getBatchDelay(),
                props.getMaxExportingThreads(),
                props.getMaxThreads()
        );
        beanExporter.setFieldResolver(fieldResolver);
        BeanSearcherAutoConfiguration.ifAvailable(fileWriterFactory, beanExporter::setFileWriterFactory);
        BeanSearcherAutoConfiguration.ifAvailable(fileNamer, beanExporter::setFileNamer);
        return beanExporter;
    }

}
