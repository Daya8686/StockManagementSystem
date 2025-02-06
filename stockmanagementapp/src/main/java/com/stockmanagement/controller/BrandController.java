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
import com.stockmanagement.DTO.BrandDTO;
import com.stockmanagement.DTO.CreateBrandDTO;
import com.stockmanagement.service.BrandService;
import com.stockmanagement.util.ApiResponseHandler;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;

@RestController
@RequestMapping("/api/brand")
public class BrandController {

	@Autowired
	private BrandService brandService;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	private final Validator validator;
	
	
	
	
	public BrandController (Validator validator) {
		this.validator=validator;
	}
	
	@PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
	@PostMapping(value="/add/{categoryId}",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<ApiResponseHandler> createBrand(@PathVariable UUID categoryId, @RequestPart String createBrandDTOstr,  @RequestPart(value = "image") MultipartFile image) throws JsonMappingException, JsonProcessingException{
		CreateBrandDTO createBrandDTO = objectMapper.readValue(createBrandDTOstr, CreateBrandDTO.class);
		
		 Set<ConstraintViolation<CreateBrandDTO>> violations = validator.validate(createBrandDTO);
	        if (!violations.isEmpty()) {
	            throw new ConstraintViolationException(violations);
	        }
	        
		ApiResponseHandler savedBrandDTO = brandService.createNewBrand(categoryId,createBrandDTO, image);
		return ResponseEntity.status(HttpStatus.CREATED).body(savedBrandDTO);
		
	}
	
}
