package com.stockmanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stockmanagement.DTO.CreateCategoryDTO;
import com.stockmanagement.service.CategoryService;
import com.stockmanagement.util.ApiResponseHandler;



@RestController
@RequestMapping("api/category")
public class CategoryController {
	
	@Autowired
	private CategoryService categoryService;
	
	@PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
	@PostMapping(value="/add")
	public ResponseEntity<ApiResponseHandler> createCategory(@RequestBody CreateCategoryDTO categoryDTO){
		ApiResponseHandler savedCategoryResponse = categoryService.saveCategory(categoryDTO);
		return ResponseEntity.status(HttpStatus.CREATED).body(savedCategoryResponse);
	}

}
