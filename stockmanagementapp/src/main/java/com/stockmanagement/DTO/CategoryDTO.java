package com.stockmanagement.DTO;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {

	private UUID categoryId;

	private String categoryName; 

	private String description;
	
	private LocalDateTime lastUpdate;

}
