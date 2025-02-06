package com.stockmanagement.entity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalesRecord {
	
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "sales_record_id")
	private UUID salesRecordId;

    @ManyToOne
    @JoinColumn(name = "user_id",nullable = false)
    private Users user; // The salesman making the sale

    @OneToMany(mappedBy = "salesRecord", cascade = CascadeType.ALL)
    private List<SalesProductDetail> productDetails; // Details of products in this sale
    
//    @ManyToOne
//    @JoinColumn(name = "product_id", nullable = false)
//    private Product product; /// change

    private double totalAmount;    // Sum of (price * quantitySold) for all products
    private double discountAmount; // Discount applied
    private double petrolExpense;  // Expense for petrol
    private double foodExpense;    // Expense for food
    private double otherExpense;   // Other expenses
    private String description;    // Additional description of the sale
    private double billDuesAmount; //Bill dues of sale
    private String aboutBillDueDescription; //about the bill dues
    private double finalAmount;    // Final amount after deductions

    private LocalDateTime saleDate; // Date of sale
    
}
