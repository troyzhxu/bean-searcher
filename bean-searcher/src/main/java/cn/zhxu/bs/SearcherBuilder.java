package cn.zhxu.bs;

import cn.zhxu.bs.implement.AbstractSearcher;
import cn.zhxu.bs.implement.DefaultBeanSearcher;
import cn.zhxu.bs.implement.DefaultMapSearcher;
import cn.zhxu.bs.FieldConvertor.MFieldConvertor;

import java.util.ArrayList;
import java.util.List;

/**
 * 检索器 Builder
 * 
 * @author Troy.Zhou @ 2017-03-20
 * 
 * */
public class SearcherBuilder {

	/**
	 * 用于构建一个 BeanSearcher 实例
	 * @return BeanSearcherBuilder
	 */
	public static BeanSearcherBuilder beanSearcher() {
		return new BeanSearcherBuilder();
	}

	/**
	 * 用于构建一个 MapSearcher 实例
	 * @return MapSearcherBuilder
	 */
	public static MapSearcherBuilder mapSearcher() {
		return new MapSearcherBuilder();
	}

	@SuppressWarnings("unchecked")
	static class BaseSearcherBuilder<Builder extends BaseSearcherBuilder<?>> {

		private ParamResolver paramResolver;

		private SqlResolver sqlResolver;

		private SqlExecutor sqlExecutor;

		private MetaResolver metaResolver;

		private final List<SqlInterceptor> interceptors = new ArrayList<>();

		private final List<ResultFilter> resultFilters = new ArrayList<>();

		public Builder paramResolver(ParamResolver paramResolver) {
			this.paramResolver = paramResolver;
			return (Builder) this;
		}

		public Builder sqlResolver(SqlResolver sqlResolver) {
			this.sqlResolver = sqlResolver;
			return (Builder) this;
		}

		public Builder sqlExecutor(SqlExecutor sqlExecutor) {
			this.sqlExecutor = sqlExecutor;
			return (Builder) this;
		}

		public Builder metaResolver(MetaResolver metaResolver) {
			this.metaResolver = metaResolver;
			return (Builder) this;
		}

		public Builder addInterceptor(SqlInterceptor interceptor) {
			if (interceptor != null) {
				interceptors.add(interceptor);
			}
			return (Builder) this;
		}

		/**
		 * @since v3.6.1
		 * @param resultFilter 结果过滤器
		 * @return Builder
		 */
		public Builder addResultFilter(ResultFilter resultFilter) {
			if (resultFilter != null) {
				resultFilters.add(resultFilter);
			}
			return (Builder) this;
		}

		protected void buildInternal(AbstractSearcher mainSearcher) {
			if (paramResolver != null) {
				mainSearcher.setParamResolver(paramResolver);
			}
			if (sqlResolver != null) {
				mainSearcher.setSqlResolver(sqlResolver);
			}
			if (sqlExecutor != null) {
				mainSearcher.setSqlExecutor(sqlExecutor);
			} else if (mainSearcher.getSqlExecutor() == null) {
				throw new SearchException("You must set a sqlExecutor before building. ");
			}
			if (metaResolver != null) {
				mainSearcher.setMetaResolver(metaResolver);
			}
			mainSearcher.setInterceptors(interceptors);
			mainSearcher.setResultFilters(resultFilters);
		}

	}


	public static class BeanSearcherBuilder extends BaseSearcherBuilder<BeanSearcherBuilder> {

		private BeanReflector beanReflector;

		public BeanSearcherBuilder beanReflector(BeanReflector beanReflector) {
			this.beanReflector = beanReflector;
			return this;
		}

		public BeanSearcher build() {
			DefaultBeanSearcher beanSearcher = new DefaultBeanSearcher();
			buildInternal(beanSearcher);
			if (beanReflector != null) {
				beanSearcher.setBeanReflector(beanReflector);
			}
			return beanSearcher;
		}

	}

	public static class MapSearcherBuilder extends BaseSearcherBuilder<MapSearcherBuilder> {

		private final List<MFieldConvertor> convertors = new ArrayList<>();

		public MapSearcher build() {
			DefaultMapSearcher beanSearcher = new DefaultMapSearcher();
			buildInternal(beanSearcher);
			beanSearcher.setConvertors(convertors);
			return beanSearcher;
		}

		public MapSearcherBuilder addFieldConvertor(MFieldConvertor convertor) {
			if (convertor != null) {
				this.convertors.add(convertor);
			}
			return this;
		}

	}
	
}
