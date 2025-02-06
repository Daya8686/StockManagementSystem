package com.stockmanagement.DTO;

import com.stockmanagement.entity.Contact;
import com.stockmanagement.entity.Organization;
import com.stockmanagement.entity.Role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AllUsersResponseDTO {

	private String username;

	private String firstName;

	private String lastName;

//	private String password;

	private Role role; // MANAGER, SALESMAN, SUPER_ADMIN

	private boolean isUserActive;

	private ContactDTO contactDto;

	private OrganizationDTO organizationDTO;

	private boolean termsAndConditions;

	private String gender;

	private String imagePath;

}
