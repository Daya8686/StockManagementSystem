package com.stockmanagement.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(
	    name = "user_wise_product_stock",
	    uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "product_id"})
	)
public class UserWiseProductStock {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "user_wise_product_stock_id")
	private UUID userWiseProductStockId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "quantity_of_stock", nullable = false)
    private double quantityOfStock;  // Stock quantity for this user
    
    
    
    @Column(name = "product_price", nullable = false)
	private double price; // Sale price
    
}