package com.stockmanagement.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stockmanagement.entity.UserWiseProductStock;

@Repository
public interface UserWiseProductStockRepository extends JpaRepository<UserWiseProductStock, UUID> {

}
