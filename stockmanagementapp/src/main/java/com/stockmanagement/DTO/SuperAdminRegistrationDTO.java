package com.stockmanagement.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SuperAdminRegistrationDTO {
	 @Size(min = 3, max = 125, message = "Username must be in between 3 and 125 characters")
	    @NotBlank(message = "Username field can't left blank!!")
	    private String username;
	    
	    @Size(min = 3, max = 125, message = "firstname must be in between 3 and 125 characters")
	    @NotBlank(message = "firstname field can't left blank!!")
	    private String firstName;
	    
	    @Size(min = 3, max = 125, message = "lastname must be in between 3 and 125 characters")
	    @NotBlank(message = "lastname field can't left blank!!")
	    private String lastName;

	    @NotBlank(message = "Password field can't be left blank!!")
	    @Size(min = 8, max = 125, message = "Password must be between 8 and 125 characters with at least one of these symbols (@, #, _) and a number.")
	    @Pattern(regexp = "^(?=.*[0-9])(?=.*[@#_]).{8,125}$", message = "Password must contain at least one number and one special character (@, #, or _) & must be between 8 and 125 characters.")
	    private String password;
	
//	    @Enumerated(EnumType.STRING)
//	    @Column(name = "user_roles", nullable = false)
//	    private Role role; // MANAGER, SALESMAN, SUPER_ADMIN
//	    
//	    @Column(nullable = false)
//	    private boolean isUserActive;
	    
	    
	    @NotNull(message = "Contact information can't be null")
	    private ContactDTO contactDto;
	    
//	    @NotNull(message = "Organization should not be left empty!!")
//	    private OrganizationDTO organizationDto;
	    
	    private boolean termsAndConditions;
	    
	    @NotBlank(message = "User should mention there gender!!")
	    private String gender;
	    
//	    @Lob
//	    private byte[] userImage;


}

