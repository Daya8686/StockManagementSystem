package com.stockmanagement.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Users_info")
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id")
    private UUID id;

    @Column(nullable = false, unique = true)
    private String username;
    
    @Column(nullable = false, name = "first_name")
    private String firstName;
    
    @Column(nullable = false, name = "last_name")
    private String lastName;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_roles", nullable = false)
    private Role role; // MANAGER, SALESMAN, SUPER_ADMIN
    
    @Column(nullable = false)
    private boolean isUserActive;
    
    @Column(nullable = false)
    private boolean isUserValid;
    
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "contact_id", nullable = false)
    private Contact contact;

    @ManyToOne
    @JoinColumn(name = "organization_id")
    private Organization organization;
    
    @Column(name= "terms_and_conditions",nullable = false)
    private boolean termsAndConditions;
    
    @Column(nullable = false)
    private String gender;
    
    
    private String imagePath;
//    @Lob
//    @Column(name = "user_image", columnDefinition = "MEDIUMBLOB")
//    private byte[] userImage;
    
    @OneToMany(mappedBy = "user")
    private List<UserWiseProductStock> productStocks;

    @OneToMany(mappedBy = "user")
    private List<SalesRecord> salesRecords;
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Users users = (Users) o;
        return Objects.equals(id, users.id); // Compare only by id or username
    }

    @Override
    public int hashCode() {
        return Objects.hash(id); // Use only id to generate hash code
    }
    
}
