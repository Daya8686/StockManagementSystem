package com.stockmanagement.DTO;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateBrandDTO {
	
	@Size(min = 2, max = 128, message = "Brand Name cannot be left empty, must contains atleast 2 letters!")
	private String brandName; 
	
	
	private String description;

}
