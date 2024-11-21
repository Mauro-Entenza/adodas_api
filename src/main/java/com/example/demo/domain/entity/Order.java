package com.example.demo.domain.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name="Order")
@Table(name="Orders")
public class Order {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long id;
    @Column
    private String customerNotes;
    @Column
    private float orderPrice;
    @Column
    private boolean isDelivered;
    @Column
    private LocalDate orderDate;
    @ManyToOne
    @JoinColumn(name="customer_id", nullable=false)
    private Customer customer;
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Item> items;
}
