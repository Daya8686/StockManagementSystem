package com.stockmanagement.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.stockmanagement.entity.Organization;
import com.stockmanagement.entity.Users;

import jakarta.transaction.Transactional;

@Repository
public interface UsersRepo extends JpaRepository<Users, UUID> {
	
	public Users findByUsername(String username);
	
	@Query("SELECT u FROM Users u LEFT JOIN u.contact c WHERE u.username = :identifier OR c.email = :identifier OR c.mobile = :identifier") 
	public Optional<Users> findByUsernameOrEmailOrPhoneNumber(@Param("identifier") String identifier);
	
	
	public List<Users> findByOrganization(Organization organization);

	 @Query("SELECT u FROM Users u WHERE u.contact.email = :email")
	    Optional<Users> findByEmail(@Param("email") String email);
	


}
