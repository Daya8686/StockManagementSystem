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
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {
	@Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "category_id")
    private UUID categoryId;
	
	@Column(nullable = false)
    private String categoryName;  // e.g., Beverages, Snacks, etc.
	
	@Column(nullable = false)
	private LocalDateTime lastUpdate;
	
	@Column(name = "category_description")
	private String description;
	
//	@Column(name = "category_imagepath")
//	private String imagePath;

    @ManyToOne
    @JoinColumn(name = "organization_id",nullable = false)
    private Organization organization;  // Linked to the organization

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Brand> brands;

    
}