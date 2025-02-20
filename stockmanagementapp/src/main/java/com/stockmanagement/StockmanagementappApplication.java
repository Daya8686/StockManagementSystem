package com.stockmanagement;



import java.time.Duration;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.stockmanagement.Exception.ImageSaverServiceExceptionHandler;

@SpringBootApplication
@EnableScheduling
public class StockmanagementappApplication implements WebMvcConfigurer {
	
	@Value("${image.base-directory}")
	private String baseImageUrl;

	public static void main(String[] args) {
		SpringApplication.run(StockmanagementappApplication.class, args);
	}
	
	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}

	 	@Bean
	    public ObjectMapper objectMapper() {
	        ObjectMapper objectMapper = new ObjectMapper();
	       
	        JavaTimeModule javaTimeModule = new JavaTimeModule();
	        
	        // Register the custom module
	        objectMapper.registerModule(javaTimeModule);
	        
	        // Enable formatting for LocalDateTime
	        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
	        objectMapper.setDateFormat(new java.text.SimpleDateFormat("dd-MM-yyyy'T'HH:mm:ss.SSS"));
	        
	        return objectMapper;
	    }
	 
	 	@Override
	 	public void addResourceHandlers(ResourceHandlerRegistry registry) {
	 	    String imageDirectoryPath = "file:" + baseImageUrl;
	 	    registry.addResourceHandler("/images/**")
	 	            .addResourceLocations(imageDirectoryPath)
	 	            .setCacheControl(CacheControl.maxAge(Duration.ofDays(30)).cachePublic())
	 	            .resourceChain(true)
	 	            .addResolver(new PathResourceResolver() {
	 	                @Override
	 	                protected Resource getResource(String resourcePath, Resource location) {
	 	                    try {
	 	                        Resource resource = super.getResource(resourcePath, location);
	 	                        if (resource != null && resource.exists()) {
	 	                            return resource;
	 	                        }
	 	                    } catch (Exception e) {
	 	                        throw new ImageSaverServiceExceptionHandler("Something went wrong with cache", HttpStatus.INTERNAL_SERVER_ERROR);
	 	                    }
	 	                    return null;
	 	                }
	 	            });
	 	}
}
