package com.stockmanagement.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stockmanagement.DTO.CreateUserWiseProductStockDTO;
import com.stockmanagement.service.UserWiseProductStockService;
import com.stockmanagement.util.ApiResponseHandler;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/stock")
public class UserWiseProductStockController {
	
	@Autowired
	private UserWiseProductStockService userWiseProductStockService;
	
	@PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','SALESMAN')")
	@PostMapping("/add/{productId}")
	public ResponseEntity<ApiResponseHandler> addStockPriceAndQuantityForSpecificUser(@PathVariable UUID productId, @Valid @RequestBody CreateUserWiseProductStockDTO productStockDTO){
		ApiResponseHandler stockPriceAndQuantity = userWiseProductStockService.addStockPriceAndQuantity(productId,productStockDTO);
		return ResponseEntity.status(HttpStatus.OK).body(stockPriceAndQuantity);
		
	}
	
	@GetMapping()
	@PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','SALESMAN')")
	public ResponseEntity<ApiResponseHandler> getProductsWithStockForSpecificUser(){
		ApiResponseHandler productsWithStockPriceAndQuantity = userWiseProductStockService.getProductsWithStockPriceAndQuantityForSpecificUser();
		return ResponseEntity.status(HttpStatus.OK).body(productsWithStockPriceAndQuantity);
	}
	
	@GetMapping("/{userWiseProductStockId}")
	@PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','SALESMAN')")
	public ResponseEntity<ApiResponseHandler> getProductWithStockAndPriceWithUserWiseId(@PathVariable UUID userWiseProductStockId){
		ApiResponseHandler productWithStockAndPrice =userWiseProductStockService.getProductWithStockAndPriceWithProdId(userWiseProductStockId);
		return ResponseEntity.status(HttpStatus.OK).body(productWithStockAndPrice);
	}
	
	@PutMapping("/update/{userWiseProductStockId}")
	@PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','SALESMAN')")
	public ResponseEntity<ApiResponseHandler> updateProductWithStockAndPriceForUser(@PathVariable UUID userWiseProductStockId, @Valid @RequestBody CreateUserWiseProductStockDTO updateStock){
		ApiResponseHandler updateStockAndPriceForUser = userWiseProductStockService.updateStockAndPriceForUser(userWiseProductStockId, updateStock);
		return ResponseEntity.status(HttpStatus.OK).body(updateStockAndPriceForUser);
	}
	
	@DeleteMapping("/delete/{userWiseProductStockId}")
	@PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','SALESMAN')")
	public ResponseEntity<ApiResponseHandler> deleteStockAndPriceOfProductForUser(@PathVariable UUID userWiseProductStockId){
		ApiResponseHandler deleteStockAndPriceForUser=userWiseProductStockService.deleteStockAndPriceOfProduct(userWiseProductStockId);
		return ResponseEntity.status(HttpStatus.OK).body(deleteStockAndPriceForUser);
	}

}
