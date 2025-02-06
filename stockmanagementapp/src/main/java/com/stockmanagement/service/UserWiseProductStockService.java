package com.stockmanagement.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.stockmanagement.DTO.CreateUserWiseProductStockDTO;
import com.stockmanagement.DTO.ProductDTO;
import com.stockmanagement.DTO.UserWiseProductStockDTO;
import com.stockmanagement.Exception.ProductServiceExceptionHandler;
import com.stockmanagement.entity.Product;
import com.stockmanagement.entity.UserWiseProductStock;
import com.stockmanagement.entity.Users;
import com.stockmanagement.principal.UserPrincipal;
import com.stockmanagement.repository.ProductRepository;
import com.stockmanagement.repository.UserWiseProductStockRepository;
import com.stockmanagement.util.ApiResponseHandler;

@Service
public class UserWiseProductStockService {

	@Autowired
	private UserWiseProductStockRepository userWiseProductStockRepository;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private ModelMapper mapper;

	public ApiResponseHandler addStockPriceAndQuantity(UUID productId, CreateUserWiseProductStockDTO productStockDTO) {

		Optional<Product> productById = Optional.of(productRepository.findById(productId)
				.orElseThrow(() -> new ProductServiceExceptionHandler("Product with Product ID is not found!",
						HttpStatus.BAD_REQUEST)));
		UserWiseProductStock productStock = mapper.map(productStockDTO, UserWiseProductStock.class);

		productStock.setProduct(productById.get());

		UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Users user = ((UserPrincipal) userDetails).getUser();
		productStock.setUser(user);

		UserWiseProductStock savedStockInfo = userWiseProductStockRepository.save(productStock);
		Product product = savedStockInfo.getProduct();
		ProductDTO productDTO = mapper.map(product, ProductDTO.class);
		UserWiseProductStockDTO stockDTO = mapper.map(savedStockInfo, UserWiseProductStockDTO.class);
		stockDTO.setProductDto(productDTO);
		return new ApiResponseHandler(stockDTO, HttpStatus.OK.value(), "Success");
	}

	public ApiResponseHandler getProductsWithStockPriceAndQuantity() {
		List<UserWiseProductStock> ListOfuserWiseProductStock = userWiseProductStockRepository.findAll();
		return null;
		
	}

}
