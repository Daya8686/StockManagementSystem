package com.stockmanagement.DTO;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationDTO {

	private UUID orgId;

	private String name;

	private String organizationOwnerName;

	private String address;

	private String pincode;

	private String imagePath;

	private String organizationCode;

}
