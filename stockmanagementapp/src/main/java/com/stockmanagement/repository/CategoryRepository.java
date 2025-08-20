package com.stockmanagement.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stockmanagement.entity.Category;
import com.stockmanagement.entity.Organization;
import com.stockmanagement.util.ApiResponseHandler;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID>{
	
	public Optional<Category> findByCategoryNameAndOrganization(String categoryName, Organization organization);

	

}
