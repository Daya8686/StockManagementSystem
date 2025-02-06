package com.stockmanagement.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalesProductDetail {
	
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "sales_id")
	private UUID salesId;

    @ManyToOne
    @JoinColumn(name = "sales_record_id",nullable = false)
    private SalesRecord salesRecord;

    @ManyToOne
    @JoinColumn(name = "product_id",nullable = false)
    private Product product;

    private double quantitySold;   // Quantity sold for this product
    private double pricePerUnit;  // Price per unit of the product
    private double totalPrice;    // Total price = pricePerUnit * quantitySold
}
