package com.stockmanagement.DTO;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserWiseProductStockDTO {
	@PositiveOrZero(message = "Quantity must be in positive number or zero !!")
	private double quantityOfStock;

	@PositiveOrZero(message = "Price must be in positive number or zero !!")
	private double price;

}
