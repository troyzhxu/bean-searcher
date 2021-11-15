package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
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

//	@Bean
//	public FieldConvertor numberFieldConvertor() {
//		return new NumberFieldConvertor();
//	}
//
//	@Bean
//	public FieldConvertor dateFormatFieldConvertor() {
//		DateFormatFieldConvertor convertor = new DateFormatFieldConvertor();
//		convertor.addFormat("com.example", "yyyy-MM-dd HH:mm");
//		convertor.addFormat("com.example.sbean", "yyyy-MM-dd HH");
//		convertor.addFormat("com.example.sbean.Employee", "yyyy-MM-dd");
////		convertor.addFormat("com.example.sbean.Employee.entryDate", null);
//		return convertor;
//	}

}
