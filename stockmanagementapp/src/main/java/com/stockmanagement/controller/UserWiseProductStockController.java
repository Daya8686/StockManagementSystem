package com.stockmanagement.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stockmanagement.DTO.CreateUserWiseProductStockDTO;
import com.stockmanagement.service.UserWiseProductStockService;
import com.stockmanagement.util.ApiResponseHandler;

@RestController
@RequestMapping("/api/stock")
public class UserWiseProductStockController {
	
	@Autowired
	private UserWiseProductStockService userWiseProductStockService;
	
	@PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','SALESMAN')")
	@PostMapping("/add/{productId}")
	public ResponseEntity<ApiResponseHandler> addStockPriceAndQuantity(@PathVariable UUID productId, @RequestBody CreateUserWiseProductStockDTO productStockDTO){
		ApiResponseHandler stockPriceAndQuantity = userWiseProductStockService.addStockPriceAndQuantity(productId,productStockDTO);
		return ResponseEntity.status(HttpStatus.OK).body(stockPriceAndQuantity);
		
	}
	
	public ResponseEntity<ApiResponseHandler> getProductsWithStock(){
		ApiResponseHandler productsWithStockPriceAndQuantity = userWiseProductStockService.getProductsWithStockPriceAndQuantity();
		return null;
	}
	
	public ResponseEntity<ApiResponseHandler> getProductWithStockById(@PathVariable UUID stockId){
		return null;
	}
	

}
