package com.stockmanagement.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactDTO {
	
	 	@NotBlank(message = "Mobile number can't be left blank!!")
	    @Pattern(regexp = "^[0-9]{10}$", message = "Mobile number must be exactly 10 digits long.")
	    private String mobile;

	    @NotBlank(message = "Email field can't be left blank!!")
	    @Email(message = "Email must be in a valid format (e.g., user@example.com).")
	    private String email;

}
