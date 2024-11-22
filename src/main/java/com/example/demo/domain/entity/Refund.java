package com.example.demo.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "Refund")
@Table(name = "Refunds")
public class Refund {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;
  @Column
  private String reason;
  @Column
  private LocalDate refundDate;
  @Column
  private float amount;
  @Column
  private boolean isApproved;
  @ManyToOne
  @JoinColumn(name = "order_id", nullable = false)
  private Order order;
  @ManyToOne
  @JoinColumn(name = "customer_id", nullable = false)
  private Customer customer;
}
