package com.stockmanagement.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCategoryDTO {
	
		@NotBlank(message = "Category Name cannot be left blank, Please fill it!!")
		@Size(min = 2, max = 128, message = "Category name must contains atleast 2 letters!")
		private String categoryName;  
		
		
		private String description;

}
