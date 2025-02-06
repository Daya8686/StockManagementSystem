package com.stockmanagement.DTO;

import java.util.UUID;

import com.stockmanagement.entity.StorageType;
import com.stockmanagement.entity.UnitOfMeasurement;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {

	private UUID productId;

	private String productName;

	private String description;

	private String imagePath;

	private int measureQuantity;

	@Enumerated(EnumType.STRING)
	private UnitOfMeasurement unitOfMeasurement;

	@Enumerated(EnumType.STRING)
	private StorageType storageType;
	
	private int piecesInCarton;

}
