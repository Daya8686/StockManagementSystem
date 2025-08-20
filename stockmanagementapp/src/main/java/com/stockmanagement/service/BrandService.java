package com.stockmanagement.service;

import java.time.LocalDateTime;
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

import com.stockmanagement.DTO.BrandDTO;
import com.stockmanagement.DTO.CreateBrandDTO;
import com.stockmanagement.Exception.BrandServiceExceptionHandler;
import com.stockmanagement.Exception.CategoryServiceExceptionHandler;
import com.stockmanagement.Exception.ImageSaverServiceExceptionHandler;
import com.stockmanagement.entity.Brand;
import com.stockmanagement.entity.Category;
import com.stockmanagement.entity.Users;
import com.stockmanagement.principal.UserPrincipal;
import com.stockmanagement.repository.BrandRepository;
import com.stockmanagement.repository.CategoryRepository;
import com.stockmanagement.util.ApiResponseHandler;
import com.stockmanagement.util.UserPrincipleObject;

import jakarta.transaction.Transactional;

@Service
public class BrandService {
	
	@Autowired
	private BrandRepository brandRepository;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	private final static String baseImageDir = "src/main/resources/static/images/brands";

	@Transactional
	@Modifying
	public ApiResponseHandler createNewBrand(UUID categoryId, CreateBrandDTO createBrandDTO, MultipartFile image) {
		
		
		Brand brand = modelMapper.map(createBrandDTO, Brand.class);
		
		Users user = UserPrincipleObject.getUser();
		brand.setOrganization(user.getOrganization());
		
		Optional<Category> category = categoryRepository.findById(categoryId);
		boolean existsByBrandNameAndCategory = brandRepository.existsByBrandNameAndCategory(createBrandDTO.getBrandName(), category.get());
		if(existsByBrandNameAndCategory) {
			throw new BrandServiceExceptionHandler("Brand with this name: "+createBrandDTO.getBrandName()+" already exists in this Category: "+category.get().getCategoryName(), HttpStatus.CONFLICT);
		}
		brand.setCategory(category.get());
		brand.setLastUpdated(LocalDateTime.now());
		try {
		Brand savedBrand = brandRepository.save(brand);
		if(image!=null&&!image.isEmpty()) {
		
		String imagePathForImage = ImageService.getImagePathForImage(image, savedBrand.getBrandId(), baseImageDir);
		savedBrand.setImagePath("brands/"+imagePathForImage);
		}

		
		Brand finalSavedBrand = brandRepository.save(savedBrand);
		BrandDTO brandDTO = modelMapper.map(finalSavedBrand, BrandDTO.class);
		return new ApiResponseHandler(brandDTO, HttpStatus.CREATED.value(), "Success");
		
		} catch (Exception e) {
	        // If an exception occurs, delete the image (if it was saved)
	        if (image != null && !image.isEmpty()) {
	            ImageService.rollbackImage(image, baseImageDir);
	        }
	        // Rollback the transaction and throw the exception
	        throw new BrandServiceExceptionHandler("Failed to create brand: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	    }
		
	}

	@Transactional
	public ApiResponseHandler findBrandsByCategory(UUID categoryId) {
		Optional<Category> categoryById = categoryRepository.findById(categoryId);
		if(categoryById.isEmpty()) {
			throw new CategoryServiceExceptionHandler("Category is not available", HttpStatus.NOT_FOUND);
		}
		List<Category> brandsByCategory = brandRepository.findBrandsByCategory(categoryById.get());
		if(brandsByCategory.isEmpty()) {
			throw new BrandServiceExceptionHandler("No Brands found in this category: "+categoryById.get().getCategoryName(), HttpStatus.NO_CONTENT);
		}
		List<BrandDTO> AllBrandByCategory = brandsByCategory.stream().map(brand->{
			BrandDTO brandDto = modelMapper.map(brand, BrandDTO.class);
			return brandDto;
		}).collect(Collectors.toList());
		
		
		return new ApiResponseHandler(AllBrandByCategory, HttpStatus.OK.value(), "Success");
	}
			

}
