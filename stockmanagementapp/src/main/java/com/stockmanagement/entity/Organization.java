package com.stockmanagement.entity;

import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Organization {
	
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID orgId;
    
    @Column(name = "organization_name", nullable = false)
    private String name;
    @Column(name="organization_owner_name", nullable = false)
    private String organizationOwnerName;
    @Column(name="organization_address", nullable = false)
    private String address;
    @Column(name = "organization_area_pincode", nullable = false)
    private String pincode;
//    @Lob
//    @Column(name = "organization_image", columnDefinition = "MEDIUMBLOB")
//    private byte[] organisationImage;

    private String imagePath;

    @Column(name="organization_code", nullable = false, unique = true)
    private String organizationCode;

    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Users> users;
    
    @OneToMany(mappedBy = "organization",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Product> products;
    
    @OneToMany(mappedBy = "organization",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Brand> brands;
    
    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Category> categories;
}

