package com.example.demo.domain.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {

  private long id;

  @NotNull(message = "Customer ID is required")
  private Long customerId;

  private String customerNotes;

  @NotNull(message = "The order price field is required")
  @DecimalMin(value = "0.0", message = "Order price must be positive")
  private Float orderPrice;

  private boolean isDelivered;
  private LocalDate orderDate;

  private List<Long> itemIds;
}
