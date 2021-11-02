package com.example;

import com.ejlchina.searcher.FieldConvertor;
import com.ejlchina.searcher.implement.NumberFieldConvertor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@SpringBootApplication
public class Application implements WebMvcConfigurer {

	
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/static/**")
				.addResourceLocations("classpath:/static/");
	}

	@Bean
	public FieldConvertor fieldConvertor() {
		return new NumberFieldConvertor();
	}


}
