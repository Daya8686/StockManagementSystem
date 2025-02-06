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

import com.stockmanagement.DTO.CategoryDTO;
import com.stockmanagement.DTO.CreateCategoryDTO;
import com.stockmanagement.Exception.CategoryServiceExceptionHandler;
import com.stockmanagement.entity.Category;
import com.stockmanagement.entity.Users;
import com.stockmanagement.principal.UserPrincipal;
import com.stockmanagement.repository.CategoryRepository;
import com.stockmanagement.repository.UsersRepo;
import com.stockmanagement.util.ApiResponseHandler;

import jakarta.transaction.Transactional;

@Service
public class CategoryService {
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private UsersRepo usersRepo;
	
	
	@Transactional
	@Modifying
	public ApiResponseHandler saveCategory(CreateCategoryDTO createCategoryDTO) {
		String categoryName=createCategoryDTO.getCategoryName();
		UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		 UUID userId = ((UserPrincipal) userDetails).getUser().getId();
		 Optional<Users> userById = usersRepo.findById(userId);		
		 
		 categoryRepository.findByCategoryNameAndOrganization(categoryName, userById.get().getOrganization())
         .ifPresent(existingCategory -> {
             throw new CategoryServiceExceptionHandler("Category name '" + categoryName + "' already exists in this organization.",HttpStatus.CONFLICT);
         });
		 
		 
		Category category = modelMapper.map(createCategoryDTO, Category.class);
		category.setLastUpdate(LocalDateTime.now());
		
		
		category.setOrganization( userById.get().getOrganization());
		Category savedCategory = categoryRepository.save(category);
		
		CategoryDTO categoryDTO = modelMapper.map(savedCategory, CategoryDTO.class);
		
		return new ApiResponseHandler(categoryDTO, HttpStatus.CREATED.value(), "Success");
		
	}
	
	

}
