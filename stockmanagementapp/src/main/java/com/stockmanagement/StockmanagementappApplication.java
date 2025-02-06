package com.stockmanagement;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@SpringBootApplication
@EnableScheduling
public class StockmanagementappApplication {

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
	 
	 	
}
