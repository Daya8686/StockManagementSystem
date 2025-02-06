package com.stockmanagement.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateDTO {
	
	 @Size(min = 3, max = 125, message = "Username must be in between 3 and 125 characters")
	    @NotBlank(message = "Username field can't left blank!!")
	    private String username;
	    
	    @Size(min = 3, max = 125, message = "firstname must be in between 3 and 125 characters")
	    @NotBlank(message = "firstname field can't left blank!!")
	    private String firstName;
	    
	    @Size(min = 3, max = 125, message = "lastname must be in between 3 and 125 characters")
	    @NotBlank(message = "lastname field can't left blank!!")
	    private String lastName;
	    
	    @NotNull(message = "Contact information can't be null")
	    private ContactDTO contactDto;
	    
	    @NotBlank(message = "User should mention there gender!!")
	    private String gender;

}
