package com.stockmanagement.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stockmanagement.entity.Brand;
import com.stockmanagement.entity.Category;

@Repository
public interface BrandRepository extends JpaRepository<Brand, UUID> {

	public List<Category> findBrandsByCategory(Category category);
	
	public boolean existsByBrandNameAndCategory(String brandName, Category category);

}
