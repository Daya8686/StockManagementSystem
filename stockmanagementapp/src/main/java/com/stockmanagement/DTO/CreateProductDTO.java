package com.stockmanagement.DTO;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.stockmanagement.entity.StorageType;
import com.stockmanagement.entity.UnitOfMeasurement;
import com.stockmanagement.util.StorageTypeDeserializer;
import com.stockmanagement.util.UnitOfMeasurementDeserializer;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductDTO {
	
	@Size(min = 2, max = 128, message = "Product Name field cannot left black or less then 2 character!")
    private String productName;  
    
	
	@Size(min = 2, max = 128, message = "Product description field cannot left black or less then 2 character!")
	private String description;
    
	
	@NotNull(message = "Product measure quantity must be not left empty")
	@PositiveOrZero(message = "Product measure quantity must be positive or zero")
	private Integer measureQuantity;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "unit of measurement of product cannot be left empty")
    @JsonDeserialize(using = UnitOfMeasurementDeserializer.class)
    private UnitOfMeasurement unitOfMeasurement;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "storge type of product cannot be left empty")
    @JsonDeserialize(using = StorageTypeDeserializer.class)
    private StorageType storageType;
    
    @PositiveOrZero(message = "Pieces in carton must be positive or zero")
    private int piecesInCarton;


}
