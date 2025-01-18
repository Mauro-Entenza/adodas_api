package com.example.demo.domain.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceDto {

  private long id;

  @NotNull(message = "The name field is required")
  private String name;

  private String description;

  @NotNull
  @Min(value = 0, message = "The service price must be 0 or higher")
  private float price;

  private LocalDate availableFrom;

  private LocalDate availableTo;

  private boolean isActive;

  private Long customerId;

  private String notes;
}
