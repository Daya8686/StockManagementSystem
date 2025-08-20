package com.stockmanagement.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stockmanagement.entity.Product;
import com.stockmanagement.entity.UserWiseProductStock;
import com.stockmanagement.entity.Users;

@Repository
public interface UserWiseProductStockRepository extends JpaRepository<UserWiseProductStock, UUID> {
	
	public List<UserWiseProductStock> findByUser(Users user);

	public Optional<UserWiseProductStock> findByUserAndProduct(Users user, Product product);

}
