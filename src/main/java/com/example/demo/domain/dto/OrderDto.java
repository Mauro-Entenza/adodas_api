package com.example.demo.domain.dto;

import jakarta.validation.constraints.Min;
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

  private String customerNotes;
  @NotNull
  @Min(value = 0, message = "The order price must be 0 or higher")
  private float orderPrice;

  private boolean isDelivered;

  @NotNull(message = "The order date is required")
  private LocalDate orderDate;

  private long customerId;

  private List<Long> itemIds;
}
