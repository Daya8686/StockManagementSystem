package com.stockmanagement.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stockmanagement.entity.Contact;

public interface ContactRepo extends JpaRepository<Contact, UUID>{
	
	public Contact findByMobile(String mobileNumber);
	
	public Contact findByEmail(String email);

}
