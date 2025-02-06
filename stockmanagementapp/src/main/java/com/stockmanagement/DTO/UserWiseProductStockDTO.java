package com.stockmanagement.DTO;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserWiseProductStockDTO {
	
	private UUID userWiseProductStockId;

   
    private ProductDTO productDto;

   
    private double quantityOfStock;  
    
    
	private double price; 

}
