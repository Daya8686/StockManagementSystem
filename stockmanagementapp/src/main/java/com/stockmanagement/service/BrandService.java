package com.stockmanagement.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

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
import com.stockmanagement.Exception.ImageSaverServiceExceptionHandler;
import com.stockmanagement.entity.Brand;
import com.stockmanagement.entity.Category;
import com.stockmanagement.entity.Users;
import com.stockmanagement.principal.UserPrincipal;
import com.stockmanagement.repository.BrandRepository;
import com.stockmanagement.repository.CategoryRepository;
import com.stockmanagement.util.ApiResponseHandler;

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
		
		if(image.getContentType()==null || !ImageService.ALLOWED_IMAGE_TYPES.contains(image.getContentType())) {
			throw new ImageSaverServiceExceptionHandler("Invalid image type. Allowed types are PNG, JPG, JPEG, SVG.", HttpStatus.BAD_REQUEST);
		}
		
		Brand brand = modelMapper.map(createBrandDTO, Brand.class);
		
		UserDetails userDetails =(UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Users user = ((UserPrincipal)userDetails).getUser();
		brand.setOrganization(user.getOrganization());
		
		Optional<Category> category = categoryRepository.findById(categoryId);
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
			

}
