package com.stockmanagement.entity;

import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
	
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "product_id")
	private UUID productId;
	
	@Column(name = "product_name", nullable = false)
    private String productName;  
    
	
	@Column(name = "product_description")
	private String description;
    
	@Column(name = "product_imagepath")
    private String imagePath;
	
	@Column(name = "measure_quantity", nullable = false)
	private int measureQuantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "unit_of_measurement",nullable = false)
    private UnitOfMeasurement unitOfMeasurement;

    @Enumerated(EnumType.STRING)
    @Column(name="storage_type_of_product",nullable = false)
    private StorageType storageType;
    
    @Column(name = "pieces_in_carton", nullable = false)
    private int piecesInCarton;

    @ManyToOne
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand;
    
    @OneToMany(mappedBy = "product",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserWiseProductStock> productStocks;

    @ManyToOne
    @JoinColumn(name = "organization_id",nullable = false)
    private Organization organization;  // Linked to the organization

   
    @OneToMany(mappedBy = "product",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<SalesProductDetail> salesProductDetails;

    // Constructors, Getters, and Setters
}
