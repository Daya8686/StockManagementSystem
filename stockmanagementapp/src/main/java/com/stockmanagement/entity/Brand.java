package com.stockmanagement.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Brand {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "brand_id")
	private UUID brandId;

	@Column(name="brand_name", nullable = false)
	private String brandName; 
	
	@Column(name="brand_description")
	private String description;
	
	@Column(nullable = false)
	private LocalDateTime lastUpdated;
	
	@Column(name = "brand_imagepath")
	private String imagePath;

	@ManyToOne
	@JoinColumn(name = "organization_id",nullable = false)
	private Organization organization; // Linked to the organization

	@ManyToOne
	@JoinColumn(name = "category_id", nullable = false)
	private Category category; // Linked to a category within the organization

	@OneToMany(mappedBy = "brand",cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Product> products;

	// Constructors, Getters, and Setters
}