package com.stockmanagement.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stockmanagement.entity.Brand;
import com.stockmanagement.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

	public List<Product> findProductsByBrand(Brand brand);

}
