package com.stockmanagement.DTO;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BrandDTO {

	private UUID brandId;

	private String brandName;

	private String description;

	private LocalDateTime lastUpdated;

	private String imagePath;

}
