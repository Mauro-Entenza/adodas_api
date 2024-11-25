package com.example.demo.domain.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "Order")
@Table(name = "Orders")
public class Order {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
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
  @JoinColumn(name = "customer_id", nullable = false)
  private Customer customer;
  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
  private List<Item> items;
}
