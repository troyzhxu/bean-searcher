package com.ejlchina.searcher.util;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.SystemPropertyUtils;

import com.ejlchina.searcher.SearcherException;


public class ClassScanner {
  
	
    private ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();  
  
    private MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(this.resourcePatternResolver);  
  

    public Set<Class<?>> scan(String basePackage) {  
        return scan(basePackage, null);  
    }  
  
    
    public Set<Class<?>> scan(String[] basePackages) {  
        return scan(basePackages, null);
    }  
    
    public Set<Class<?>> scan(String[] basePackages, Class<? extends Annotation>[] annotations) {  
        Set<Class<?>> classes = new HashSet<>();  
        for (String basePackage : basePackages) { 
            classes.addAll(scan(basePackage, annotations));
        }
        return classes;
    }
  

    public Set<Class<?>> scan(String basePackage, Class<? extends Annotation>[] annotations) {
    	TypeFilter[] includeFilters = null;
    	if (annotations != null) {
	        includeFilters = new TypeFilter[annotations.length];
	        for (int i = 0; i < annotations.length; i++) {
	        	includeFilters[i] = new AnnotationTypeFilter(annotations[i]);
	        }
    	}
        Set<Class<?>> classes = new HashSet<>();
        try {  
            String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX  
                    + org.springframework.util.ClassUtils  
                            .convertClassNameToResourcePath(SystemPropertyUtils  
                                    .resolvePlaceholders(basePackage))  
                    + "/**/*.class";
            Resource[] resources = this.resourcePatternResolver  
                    .getResources(packageSearchPath);  
  
            for (int i = 0; i < resources.length; i++) {  
                Resource resource = resources[i];  
                if (resource.isReadable()) {  
                    MetadataReader metadataReader = this.metadataReaderFactory
                    		.getMetadataReader(resource);  
                    if (includeFilters == null || matches(includeFilters, metadataReader)) {
                    	String className = metadataReader.getClassMetadata().getClassName();
                        try {
							classes.add(Class.forName(className));  
                        } catch (ClassNotFoundException e) {
                            throw new SearcherException("加载类【" + className + "】失败：", e);
                        }
                    }
                }
            }
        } catch (IOException ex) {  
            throw new SearcherException("I/O failure during classpath scanning", ex);
        }
        return classes;  
    }  
  
    protected boolean matches(TypeFilter[] includeFilters, MetadataReader metadataReader) throws IOException {  
        for (TypeFilter tf : includeFilters) {  
            if (tf.match(metadataReader, this.metadataReaderFactory)) {  
                return true;  
            }  
        }  
        return false;  
    }  
  
}  