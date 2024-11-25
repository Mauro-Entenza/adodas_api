package com.example.demo.domain.entity;

import com.example.demo.enumerate.CategoryEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Entity(name = "Item")
@Table(name = "Items")
public class Item {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;
  @Column
  private String brand;
  @Column
  private String color;
  @Enumerated(EnumType.STRING)
  @Column
  private CategoryEnum category;
  @Column
  private float price;
  @Column
  private boolean isWaterproof;
  @Column
  private LocalDate releaseDate;
  @ManyToOne
  @JoinColumn(name = "order_id")
  private Order order;
}
