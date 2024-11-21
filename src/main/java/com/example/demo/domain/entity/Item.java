package com.example.demo.domain.entity;

import com.example.demo.enumerate.CategoryEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name="Item")
@Table(name="Items")
public class Item {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
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
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
}
