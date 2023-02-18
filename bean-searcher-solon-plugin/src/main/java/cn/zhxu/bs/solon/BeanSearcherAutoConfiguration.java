package cn.zhxu.bs.solon;

import cn.zhxu.bs.*;
import cn.zhxu.bs.FieldConvertor.BFieldConvertor;
import cn.zhxu.bs.FieldConvertor.MFieldConvertor;
import cn.zhxu.bs.dialect.*;
import cn.zhxu.bs.filter.SizeLimitParamFilter;
import cn.zhxu.bs.group.DefaultGroupResolver;
import cn.zhxu.bs.group.DefaultParserFactory;
import cn.zhxu.bs.group.ExprParser;
import cn.zhxu.bs.group.GroupResolver;
import cn.zhxu.bs.solon.BeanSearcherProperties.Sql;
import cn.zhxu.bs.convertor.*;
import cn.zhxu.bs.implement.*;
import cn.zhxu.bs.solon.beans.ObjectProvider;
import cn.zhxu.bs.util.LRUCache;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Condition;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.AopContext;

import javax.sql.DataSource;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;


@Configuration
public class BeanSearcherAutoConfiguration {

	@Inject
	AopContext context;

	@Bean
	public PageExtractor pageExtractor(BeanSearcherProperties config) {
		if(context.hasWrap(PageExtractor.class)){
			return null;
		}

		BeanSearcherProperties.Params.Pagination conf = config.getParams().getPagination();
		String type = conf.getType();
		BasePageExtractor extractor;
		if (BeanSearcherProperties.Params.Pagination.TYPE_PAGE.equals(type)) {
			PageSizeExtractor p = new PageSizeExtractor();
			p.setPageName(conf.getPage());
			extractor = p;
		}  else
		if (BeanSearcherProperties.Params.Pagination.TYPE_OFFSET.equals(type)) {
			PageOffsetExtractor p = new PageOffsetExtractor();
			p.setOffsetName(conf.getOffset());
			extractor = p;
		} else {
			throw new IllegalConfigException("Invalid config: [bean-searcher.params.pagination.type: " + type + "], only 'page' / 'offset' allowed.");
		}
		int defaultSize = conf.getDefaultSize();
		int maxAllowedSize = conf.getMaxAllowedSize();
		long maxAllowedOffset = conf.getMaxAllowedOffset();
		if (defaultSize > maxAllowedSize) {
			throw new IllegalConfigException("Invalid config: [bean-searcher.params.pagination.default-size: " + defaultSize + "] can not greater than [bean-searcher.params.pagination.max-allowed-size: " + maxAllowedSize + "].");
		}
		if (defaultSize < 1) {
			throw new IllegalConfigException("Invalid config: [bean-searcher.params.pagination.default-size: " + defaultSize + "] must greater equal 1");
		}
		if (maxAllowedOffset < 1) {
			throw new IllegalConfigException("Invalid config: [bean-searcher.params.pagination.max-allowed-offset: " + maxAllowedOffset + "] must greater equal 1");
		}
		extractor.setMaxAllowedSize(maxAllowedSize);
		extractor.setMaxAllowedOffset(maxAllowedOffset);
		extractor.setDefaultSize(defaultSize);
		extractor.setSizeName(conf.getSize());
		extractor.setStart(conf.getStart());
		return extractor;
	}

	@Bean
	public Dialect dialect(BeanSearcherProperties config) {
		if(context.hasWrap(Dialect.class)){
			return null;
		}

		BeanSearcherProperties.Sql.Dialect dialect = config.getSql().getDialect();
		if (dialect == null) {
			throw new IllegalConfigException("Invalid config: [bean-searcher.sql.dialect] can not be null.");
		}
		switch (dialect) {
			case MySQL:
				return new MySqlDialect();
			case Oracle:
				return new OracleDialect();
			case PostgreSQL:
			case PgSQL:
				return new PostgreSqlDialect();
			case SqlServer:
				return new SqlServerDialect();
		}
		throw new IllegalConfigException("Invalid config: [bean-searcher.sql.dialect: " + dialect + "] only `MySql` / `Oracle` / `PostgreSQL` / `SqlServer` allowed. Please see https://bs.zhxu.cn/guide/latest/advance.html#sql-%E6%96%B9%E8%A8%80%EF%BC%88dialect%EF%BC%89 for help.");
	}

	@Bean
	public FieldOpPool fieldOpPool(Dialect dialect, ObjectProvider<List<FieldOp>> fieldOps) {
		if(context.hasWrap(FieldOpPool.class)){
			return null;
		}

		FieldOpPool pool = new FieldOpPool();
		ifAvailable(fieldOps, ops -> ops.forEach(pool::addFieldOp));
		pool.setDialect(dialect);
		return pool;
	}

	@Bean
	public ExprParser.Factory parserFactory() {
		if(context.hasWrap(ExprParser.Factory.class)){
			return null;
		}

		return new DefaultParserFactory();
	}

	@Bean
	public GroupResolver groupResolver(BeanSearcherProperties config, ExprParser.Factory parserFactory) {
		if(context.hasWrap(GroupResolver.class)){
			return null;
		}

		DefaultGroupResolver groupResolver = new DefaultGroupResolver();
		BeanSearcherProperties.Params.Group conf = config.getParams().getGroup();
		groupResolver.setEnabled(conf.isEnable());
		groupResolver.setCache(new LRUCache<>(conf.getCacheSize()));
		groupResolver.setMaxExprLength(conf.getMaxExprLength());
		groupResolver.setParserFactory(parserFactory);
		return groupResolver;
	}

	@Bean
	public BoolParamConvertor boolParamConvertor() {
		if(context.hasWrap(BoolParamConvertor.class)){
			return null;
		}

		return new BoolParamConvertor();
	}

	@Bean
	public NumberParamConvertor numberParamConvertor() {
		if(context.hasWrap(NumberParamConvertor.class)){
			return null;
		}

		return new NumberParamConvertor();
	}

	@Bean
	public DateParamConvertor dateParamConvertor() {
		if(context.hasWrap(DateParamConvertor.class)){
			return null;
		}

		return new DateParamConvertor();
	}

	@Bean
	public TimeParamConvertor timeParamConvertor() {
		if(context.hasWrap(TimeParamConvertor.class)){
			return null;
		}

		return new TimeParamConvertor();
	}

	@Bean
	public DateTimeParamConvertor dateTimeParamConvertor() {
		if(context.hasWrap(DateTimeParamConvertor.class)){
			return null;
		}

		return new DateTimeParamConvertor();
	}

	@Bean
	public SizeLimitParamFilter sizeLimitParamFilter(BeanSearcherProperties config) {
		if(context.hasWrap(SizeLimitParamFilter.class)){
			return null;
		}

		return new SizeLimitParamFilter(config.getParams().getFilter().getMaxParaMapSize());
	}

	@Bean
	public ParamResolver paramResolver(PageExtractor pageExtractor,
									   FieldOpPool fieldOpPool,
									   List<ParamFilter> paramFilters,
									   List<ParamResolver.Convertor> convertors,
									   GroupResolver groupResolver,
									   BeanSearcherProperties config) {

		if(context.hasWrap(ParamResolver.class)){
			return null;
		}

		DefaultParamResolver paramResolver = new DefaultParamResolver(convertors, paramFilters);
		paramResolver.setPageExtractor(pageExtractor);
		paramResolver.setFieldOpPool(fieldOpPool);
		BeanSearcherProperties.Params conf = config.getParams();
		paramResolver.setOperatorSuffix(conf.getOperatorKey());
		paramResolver.setIgnoreCaseSuffix(conf.getIgnoreCaseKey());
		paramResolver.setOrderName(conf.getOrder());
		paramResolver.setSortName(conf.getSort());
		paramResolver.setOrderByName(conf.getOrderBy());
		paramResolver.setSeparator(conf.getSeparator());
		paramResolver.setOnlySelectName(conf.getOnlySelect());
		paramResolver.setSelectExcludeName(conf.getSelectExclude());
		BeanSearcherProperties.Params.Group group = conf.getGroup();
		paramResolver.setGexprName(group.getExprName());
		paramResolver.setGroupSeparator(group.getSeparator());
		paramResolver.setGroupResolver(groupResolver);
		return paramResolver;
	}

	@Bean
	public SqlResolver sqlResolver(Dialect dialect) {
		if(context.hasWrap(SqlResolver.class)){
			return null;
		}

		return new DefaultSqlResolver(dialect);
	}

	@Bean
	public SqlExecutor sqlExecutor(ObjectProvider<DataSource> dataSource,
								   ObjectProvider<List<NamedDataSource>> namedDataSources,
								   ObjectProvider<SqlExecutor.SlowListener> slowListener,
								   BeanSearcherProperties config) {
		if(context.hasWrap(SqlExecutor.class)){
			return null;
		}

		DefaultSqlExecutor executor = new DefaultSqlExecutor(dataSource.getIfAvailable());
		ifAvailable(namedDataSources, ndsList -> {
			for (NamedDataSource nds: ndsList) {
				executor.setDataSource(nds.getName(), nds.getDataSource());
			}
		});
		ifAvailable(slowListener, executor::setSlowListener);
		executor.setSlowSqlThreshold(config.getSql().getSlowSqlThreshold());
		return executor;
	}

	@Bean
	@Condition(hasProperty = "${bean-searcher.field-convertor.use-number}=true")
	public NumberFieldConvertor numberFieldConvertor() {
		if(context.hasWrap(NumberFieldConvertor.class)){
			return null;
		}

		return new NumberFieldConvertor();
	}

	@Bean
	@Condition(hasProperty = "${bean-searcher.field-convertor.use-str-num}=true")
	public StrNumFieldConvertor strNumFieldConvertor() {
		if(context.hasWrap(StrNumFieldConvertor.class)){
			return null;
		}

		return new StrNumFieldConvertor();
	}

	@Bean
	@Condition(hasProperty = "${bean-searcher.field-convertor.use-bool-num}=true")
	public BoolNumFieldConvertor boolNumFieldConvertor() {
		if(context.hasWrap(BoolNumFieldConvertor.class)){
			return null;
		}

		return new BoolNumFieldConvertor();
	}

	@Bean
	@Condition(hasProperty = "${bean-searcher.field-convertor.use-bool}=true")
	public BoolFieldConvertor boolFieldConvertor(BeanSearcherProperties config) {
		if(context.hasWrap(BoolFieldConvertor.class)){
			return null;
		}

		String[] falseValues = config.getFieldConvertor().getBoolFalseValues();
		BoolFieldConvertor convertor = new BoolFieldConvertor();
		if (falseValues != null) {
			convertor.addFalseValues(falseValues);
		}
		return convertor;
	}

	@Bean
	@Condition(hasProperty = "${bean-searcher.field-convertor.use-date}=true")
	public DateFieldConvertor dateFieldConvertor(BeanSearcherProperties config) {
		if(context.hasWrap(DateFieldConvertor.class)){
			return null;
		}

		DateFieldConvertor convertor = new DateFieldConvertor();
		ZoneId zoneId = config.getFieldConvertor().getZoneId();
		if (zoneId != null) {
			convertor.setZoneId(zoneId);
		}
		return convertor;
	}

	@Bean
	@Condition(hasProperty = "${bean-searcher.field-convertor.use-time}=true")
	public TimeFieldConvertor timeFieldConvertor() {
		if(context.hasWrap(TimeFieldConvertor.class)){
			return null;
		}

		return new TimeFieldConvertor();
	}

	@Bean
	@Condition(hasProperty = "${bean-searcher.field-convertor.use-enum}=true")
	public EnumFieldConvertor enumFieldConvertor(BeanSearcherProperties config) {
		if(context.hasWrap(EnumFieldConvertor.class)){
			return null;
		}

		BeanSearcherProperties.FieldConvertor conf = config.getFieldConvertor();
		EnumFieldConvertor convertor = new EnumFieldConvertor();
		convertor.setFailOnError(conf.isEnumFailOnError());
		convertor.setIgnoreCase(conf.isEnumIgnoreCase());
		return convertor;
	}



	@Bean
	@Condition(hasProperty = "${bean-searcher.field-convertor.use-list}=true")
	public ListFieldConvertor listFieldConvertor(BeanSearcherProperties config,
				ObjectProvider<List<ListFieldConvertor.Convertor<?>>> convertorsProvider) {
		if(context.hasWrap(ListFieldConvertor.class) == false){
			return null;
		}

		BeanSearcherProperties.FieldConvertor conf = config.getFieldConvertor();
		ListFieldConvertor convertor = new ListFieldConvertor(conf.getListItemSeparator());
		ifAvailable(convertorsProvider, convertor::setConvertors);
		return convertor;
	}

	@Bean
	public BeanReflector beanReflector(ObjectProvider<List<BFieldConvertor>> convertorsProvider) {
		if(context.hasWrap(BeanReflector.class) == false){
			return null;
		}

		List<BFieldConvertor> convertors = convertorsProvider.getIfAvailable();
		if (convertors != null) {
			return new DefaultBeanReflector(convertors);
		}
		return new DefaultBeanReflector();
	}

	@Bean
	public DbMapping dbMapping(BeanSearcherProperties config) {
		if(context.hasWrap(DbMapping.class) == false){
			return null;
		}

		DefaultDbMapping mapping = new DefaultDbMapping();
		Sql.DefaultMapping conf = config.getSql().getDefaultMapping();
		mapping.setTablePrefix(conf.getTablePrefix());
		mapping.setUpperCase(conf.isUpperCase());
		mapping.setUnderlineCase(conf.isUnderlineCase());
		mapping.setRedundantSuffixes(conf.getRedundantSuffixes());
		mapping.setIgnoreFields(conf.getIgnoreFields());
		mapping.setDefaultInheritType(conf.getInheritType());
		mapping.setDefaultSortType(conf.getSortType());
		mapping.setAroundChar(conf.getAroundChar());
		return mapping;
	}

	@Bean
	public MetaResolver metaResolver(DbMapping dbMapping, ObjectProvider<SnippetResolver> snippetResolver) {
		if(context.hasWrap(MetaResolver.class) == false){
			return null;
		}

		DefaultMetaResolver metaResolver = new DefaultMetaResolver(dbMapping);
		ifAvailable(snippetResolver, metaResolver::setSnippetResolver);
		return metaResolver;
	}

	@Bean
	@Condition(hasProperty = "${bean-searcher.use-bean-searcher}=true")
	public BeanSearcher beanSearcher(MetaResolver metaResolver,
									 ParamResolver paramResolver,
									 SqlResolver sqlResolver,
									 SqlExecutor sqlExecutor,
									 BeanReflector beanReflector,
									 ObjectProvider<List<SqlInterceptor>> interceptors,
									 ObjectProvider<List<ResultFilter>> processors) {
		if(context.hasWrap(BeanSearcher.class) == false){
			return null;
		}

		DefaultBeanSearcher searcher = new DefaultBeanSearcher();
		searcher.setMetaResolver(metaResolver);
		searcher.setParamResolver(paramResolver);
		searcher.setSqlResolver(sqlResolver);
		searcher.setSqlExecutor(sqlExecutor);
		searcher.setBeanReflector(beanReflector);
		ifAvailable(interceptors, searcher::setInterceptors);
		ifAvailable(processors, searcher::setResultFilters);
		return searcher;
	}

	@Bean
	@Condition(hasProperty = "${bean-searcher.field-convertor.use-date-format}=true")
	public DateFormatFieldConvertor dateFormatFieldConvertor(BeanSearcherProperties config) {
		if(context.hasWrap(DateFormatFieldConvertor.class) == false){
			return null;
		}

		BeanSearcherProperties.FieldConvertor conf = config.getFieldConvertor();
		Map<String, String> dateFormats = conf.getDateFormats();
		ZoneId zoneId = conf.getZoneId();
		DateFormatFieldConvertor convertor = new DateFormatFieldConvertor();
		if (dateFormats != null) {
			dateFormats.forEach((key, value) -> {
				// 由于在 yml 的 key 中的 `:` 会被自动过滤，所以这里做下特殊处理，在 yml 中可以用 `-` 替代
				String scope = key.replace('-', ':');
				convertor.setFormat(scope, value);
			});
		}
		if (zoneId != null) {
			convertor.setZoneId(zoneId);
		}
		return convertor;
	}

	@Bean
	@Condition(hasProperty = "${bean-searcher.field-convertor.use-b2-m}=true")
	public B2MFieldConvertor b2mFieldConvertor(ObjectProvider<List<BFieldConvertor>> convertors) {
		if(context.hasWrap(B2MFieldConvertor.class) == false){
			return null;
		}

		List<BFieldConvertor> list = convertors.getIfAvailable();
		if (list != null) {
			return new B2MFieldConvertor(list);
		}
		return new B2MFieldConvertor(Collections.emptyList());
	}

	@Bean
	@Condition(hasProperty = "${bean-searcher.use-map-searcher}=true")
	public MapSearcher mapSearcher(MetaResolver metaResolver,
								   ParamResolver paramResolver,
								   SqlResolver sqlResolver,
								   SqlExecutor sqlExecutor,
								   ObjectProvider<List<MFieldConvertor>> convertors,
								   ObjectProvider<List<SqlInterceptor>> interceptors,
								   ObjectProvider<List<ResultFilter>> resultFilters) {

		if(context.hasWrap(MapSearcher.class) == false){
			return null;
		}

		DefaultMapSearcher searcher = new DefaultMapSearcher();
		searcher.setMetaResolver(metaResolver);
		searcher.setParamResolver(paramResolver);
		searcher.setSqlResolver(sqlResolver);
		searcher.setSqlExecutor(sqlExecutor);
		List<MFieldConvertor> list = convertors.getIfAvailable();
		if (list != null) {
			List<MFieldConvertor> newList = new ArrayList<>(list);
			// 让 DateFormatFieldConvertor 排在前面
			newList.sort((o1, o2) -> {
				if (o1 instanceof DateFormatFieldConvertor) {
					return -1;
				}
				if (o2 instanceof DateFormatFieldConvertor) {
					return 1;
				}
				return 0;
			});
			searcher.setConvertors(newList);
		}
		ifAvailable(interceptors, searcher::setInterceptors);
		ifAvailable(resultFilters, searcher::setResultFilters);
		return searcher;
	}

	private <T> void ifAvailable(ObjectProvider<T> provider, Consumer<T> consumer) {
		// 为了兼容 1.x 的 SpringBoot，最低兼容到 v1.4
		// 不直接使用 ObjectProvider.ifAvailable 方法
		T dependency = provider.getIfAvailable();
		if (dependency != null) {
			consumer.accept(dependency);
		}
	}

}
