package com.stockmanagement.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stockmanagement.entity.Organization;

public interface OrganizationRepo extends JpaRepository<Organization, UUID>{

	boolean existsByOrganizationCode(String organizationCode);
	
	public Organization findByOrganizationCode(String organizationCode);
	

}
