package com.stockmanagement.DTO;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrganizationDTO {
	
	
	@NotBlank(message = "Organization name cannot be blank")
    @Size(min = 3, max = 100, message = "Organization name must be between 3 and 100 characters")
    private String name;

    @NotBlank(message = "Organization owner name cannot be blank")
    @Size(min = 3, max = 100, message = "Organization owner name must be between 3 and 100 characters")
    private String organizationOwnerName;

    @NotBlank(message = "Address cannot be blank")
    @Size(min = 10, max = 200, message = "Address must be between 10 and 200 characters")
    private String address;

    @NotBlank(message = "Pincode cannot be blank")
    @Pattern(regexp = "^[0-9]{6}$", message = "Pincode must be a 6-digit number")
    private String pincode;

    


    
    


}
