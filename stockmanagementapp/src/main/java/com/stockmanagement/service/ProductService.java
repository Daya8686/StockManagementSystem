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
import org.springframework.web.multipart.MultipartFile;

import com.stockmanagement.DTO.CreateProductDTO;
import com.stockmanagement.DTO.ProductDTO;
import com.stockmanagement.Exception.ImageSaverServiceExceptionHandler;
import com.stockmanagement.Exception.ProductServiceExceptionHandler;
import com.stockmanagement.entity.Brand;
import com.stockmanagement.entity.Product;
import com.stockmanagement.entity.Users;
import com.stockmanagement.principal.UserPrincipal;
import com.stockmanagement.repository.BrandRepository;
import com.stockmanagement.repository.ProductRepository;
import com.stockmanagement.util.ApiResponseHandler;

import jakarta.transaction.Transactional;

@Service
public class ProductService {
	
	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private BrandRepository brandRepository;
	
	private final static String baseImageDir = "src/main/resources/static/images/products";

	@Transactional
	@Modifying
	public ApiResponseHandler createProduct(UUID brandId, CreateProductDTO createProductDTO, MultipartFile image) {
		
		
		Product product = modelMapper.map(createProductDTO, Product.class);
		
		Optional<Brand> brandById = brandRepository.findById(brandId);
		product.setBrand(brandById.get());
		
		UserDetails userDetails =(UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Users user = ((UserPrincipal)userDetails).getUser();
		product.setOrganization(user.getOrganization());
		try {
		Product savedProduct = productRepository.save(product);
		if(image!=null && !image.isEmpty()) {
		String imagePathForImage = ImageService.getImagePathForImage(image, savedProduct.getProductId(), baseImageDir);
		savedProduct.setImagePath("products/"+imagePathForImage);
		}
		
		
		Product savedFinalProduct = productRepository.save(savedProduct);
		
		ProductDTO productDto = modelMapper.map(savedFinalProduct, ProductDTO.class);
		
		return new ApiResponseHandler(productDto, HttpStatus.CREATED.value(), "Success");
		
		} catch (Exception e) {
			// If an exception occurs, delete the image (if it was saved)
			if (image != null && !image.isEmpty()) {
				ImageService.rollbackImage(image, baseImageDir);
			}
			// Rollback the transaction and throw the exception
			throw new ProductServiceExceptionHandler ("Failed to create product: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		
	}

	@Transactional
	public ApiResponseHandler getAllProductsOfBrand(UUID brandId) {
		Optional<Brand> brandById = brandRepository.findById(brandId);
		if(brandById.isEmpty()) {
			throw new ProductServiceExceptionHandler("Brand is unavailable!!", HttpStatus.NOT_FOUND);
		}
		List<Product> allProductsOfBrand = productRepository.findProductsByBrand(brandById.get());
		if(allProductsOfBrand.isEmpty() || allProductsOfBrand==null) {
			throw new ProductServiceExceptionHandler("No products Listed with this brand: "+brandById.get().getBrandName(), HttpStatus.NO_CONTENT);
		}
		List<ProductDTO> allProductsOfBrandDto = allProductsOfBrand.stream().map(product->{
			ProductDTO productDto = modelMapper.map(product, ProductDTO.class);
			return productDto;
		}).collect(Collectors.toList());
		
		return new ApiResponseHandler(allProductsOfBrandDto, HttpStatus.OK.value(), "Success");
	}

}
