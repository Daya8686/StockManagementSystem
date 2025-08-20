package com.stockmanagement.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.stockmanagement.DTO.CreateUserWiseProductStockDTO;
import com.stockmanagement.DTO.ProductDTO;
import com.stockmanagement.DTO.UserWiseProductStockDTO;
import com.stockmanagement.Exception.ProductServiceExceptionHandler;
import com.stockmanagement.Exception.UserWiseProductStockExceptionHandler;
import com.stockmanagement.entity.Product;
import com.stockmanagement.entity.UserWiseProductStock;
import com.stockmanagement.entity.Users;
import com.stockmanagement.principal.UserPrincipal;
import com.stockmanagement.repository.ProductRepository;
import com.stockmanagement.repository.UserWiseProductStockRepository;
import com.stockmanagement.util.ApiResponseHandler;
import com.stockmanagement.util.UserPrincipleObject;

import jakarta.transaction.Transactional;

@Service
public class UserWiseProductStockService {

	@Autowired
	private UserWiseProductStockRepository userWiseProductStockRepository;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private ModelMapper mapper;
	
	@Transactional
	@Modifying
	public ApiResponseHandler addStockPriceAndQuantity(UUID productId, CreateUserWiseProductStockDTO productStockDTO) {

		Optional<Product> productById = Optional.of(productRepository.findById(productId)
				.orElseThrow(() -> new ProductServiceExceptionHandler("Product with Product ID is not found!",
						HttpStatus.BAD_REQUEST)));
		
		Users user = UserPrincipleObject.getUser();
		Optional<UserWiseProductStock> byUserAndProduct = userWiseProductStockRepository.findByUserAndProduct(user, productById.get());
		if(byUserAndProduct.isPresent()) {
			throw new UserWiseProductStockExceptionHandler("Stock and price is already added please check in your products list", HttpStatus.BAD_REQUEST);
		}
		UserWiseProductStock productStock = mapper.map(productStockDTO, UserWiseProductStock.class);

		productStock.setProduct(productById.get());

		
		productStock.setUser(user);

		UserWiseProductStock savedStockInfo = userWiseProductStockRepository.save(productStock);
		Product product = savedStockInfo.getProduct();
		ProductDTO productDTO = mapper.map(product, ProductDTO.class);
		UserWiseProductStockDTO stockDTO = mapper.map(savedStockInfo, UserWiseProductStockDTO.class);
		stockDTO.setProductDto(productDTO);
		return new ApiResponseHandler(stockDTO, HttpStatus.OK.value(), "Success");
	}

	@Transactional
	public ApiResponseHandler getProductsWithStockPriceAndQuantityForSpecificUser() {
		Users user = UserPrincipleObject.getUser();
		List<UserWiseProductStock> listOfuserWiseProductStock = userWiseProductStockRepository.findByUser(user);
		if(listOfuserWiseProductStock.isEmpty()|| listOfuserWiseProductStock.size()==0) {
			throw new UserWiseProductStockExceptionHandler("No products with stock and quantity added found.", HttpStatus.NOT_FOUND);
		}
		List<UserWiseProductStockDTO> listOfUserWiseStockAndPrice = listOfuserWiseProductStock.stream().map( productStock->{
			UserWiseProductStockDTO userWiseProductStockDTO = mapper.map(productStock, UserWiseProductStockDTO.class);
			Product product = productStock.getProduct();
			ProductDTO productDto = mapper.map(product, ProductDTO.class);
			userWiseProductStockDTO.setProductDto(productDto);
			return userWiseProductStockDTO;
		}).collect(Collectors.toList());
		return new ApiResponseHandler(listOfUserWiseStockAndPrice, HttpStatus.OK.value(), "Success");
		
	}

	@Transactional
	public ApiResponseHandler getProductWithStockAndPriceWithProdId(UUID userWiseProductStockId) {
		Optional<UserWiseProductStock> stockAndQuantity = userWiseProductStockRepository.findById(userWiseProductStockId);
		if(stockAndQuantity.isEmpty()) {
			throw new UserWiseProductStockExceptionHandler("No Product with stock and quantity to display", HttpStatus.NOT_FOUND);
		}
		Users user = UserPrincipleObject.getUser();
		if(!stockAndQuantity.get().getUser().equals(user)) {
			throw new UserWiseProductStockExceptionHandler("You are not allowed to do this!", HttpStatus.FORBIDDEN);
		}
		
		UserWiseProductStockDTO userWiseProductStockDTO = mapper.map(stockAndQuantity, UserWiseProductStockDTO.class);
		Product product2 = stockAndQuantity.get().getProduct();
		ProductDTO productDto = mapper.map(product2, ProductDTO.class);
		userWiseProductStockDTO.setProductDto(productDto);
		return new ApiResponseHandler(userWiseProductStockDTO, HttpStatus.OK.value(), "Success");
	}

	@Transactional
	@Modifying
	public ApiResponseHandler updateStockAndPriceForUser(UUID userWiseProductStockId, CreateUserWiseProductStockDTO updateStock) {
		Users user = UserPrincipleObject.getUser();
		Optional<UserWiseProductStock> stockAndQuantity = userWiseProductStockRepository.findById(userWiseProductStockId);
		if(stockAndQuantity.isEmpty()) {
			throw new UserWiseProductStockExceptionHandler("No Product and stock with provided Id not found!!", HttpStatus.NOT_FOUND);
		}
		if(!stockAndQuantity.get().getUser().equals(user)) {
			throw new UserWiseProductStockExceptionHandler("You are not allowed to do this!", HttpStatus.FORBIDDEN);
		}
		
		UserWiseProductStock userWiseProductStock = stockAndQuantity.get();
		userWiseProductStock.setPrice(updateStock.getPrice());
		userWiseProductStock.setQuantityOfStock(updateStock.getQuantityOfStock());
		UserWiseProductStock savedUserWiseProductStock = userWiseProductStockRepository.save(userWiseProductStock);
		UserWiseProductStockDTO userWiseProductStockDTO = mapper.map(savedUserWiseProductStock, UserWiseProductStockDTO.class);
		Product product2 = userWiseProductStock.getProduct();
		ProductDTO productDto = mapper.map(product2, ProductDTO.class);
		userWiseProductStockDTO.setProductDto(productDto);
		return new ApiResponseHandler(userWiseProductStockDTO, HttpStatus.OK.value(), "Success");
		
	}

	@Transactional
	@Modifying
	public ApiResponseHandler deleteStockAndPriceOfProduct(UUID userWiseProductStockId) {
		Optional<UserWiseProductStock> stockAndQuantity = userWiseProductStockRepository.findById(userWiseProductStockId);
		if(stockAndQuantity.isEmpty()) {
			throw new UserWiseProductStockExceptionHandler("No Stock and Price is available with this Id", HttpStatus.NOT_FOUND);
		}
		Users user = UserPrincipleObject.getUser();
		if(!stockAndQuantity.get().getUser().equals(user)) {
			throw new UserWiseProductStockExceptionHandler("You are not allowed to do this!", HttpStatus.FORBIDDEN);
		}
		userWiseProductStockRepository.deleteById(userWiseProductStockId);
		return new ApiResponseHandler(null, HttpStatus.OK.value(), "Success");
	}
}
