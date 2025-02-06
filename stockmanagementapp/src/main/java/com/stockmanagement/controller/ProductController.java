package com.stockmanagement.controller;

import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stockmanagement.DTO.CreateProductDTO;
import com.stockmanagement.service.ProductService;
import com.stockmanagement.util.ApiResponseHandler;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;

@RestController
@RequestMapping("/api/products")
public class ProductController {

	@Autowired
	private ProductService productService;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	private final Validator validator;
	
	
	
	
	public ProductController (Validator validator) {
		this.validator=validator;
	}
	
	@PostMapping(value = "/add/{brandId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
	public ResponseEntity<ApiResponseHandler> addProduct(@PathVariable(name = "brandId") UUID brandId, 
							@RequestPart String createProductDtoStr, @RequestPart(value = "image") MultipartFile image) throws JsonMappingException, JsonProcessingException{
		
		CreateProductDTO createProductDTO = objectMapper.readValue(createProductDtoStr, CreateProductDTO.class);
		 
		Set<ConstraintViolation<CreateProductDTO>> violations = validator.validate(createProductDTO);
	        if (!violations.isEmpty()) {
	            throw new ConstraintViolationException(violations);
	        }
		
		ApiResponseHandler savedProductResponse = productService.createProduct(brandId, createProductDTO, image);
		
		return ResponseEntity.status(HttpStatus.CREATED).body(savedProductResponse);
	}
	
}
