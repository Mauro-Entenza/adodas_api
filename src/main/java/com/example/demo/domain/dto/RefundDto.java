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
public class RefundDto {

  private long id;

  @NotNull(message = "The reason field is required")
  private String reason;

  @NotNull(message = "The refund date is required")
  private LocalDate refundDate;

  @Min(value = 0, message = "The refund amount must be 0 or higher")
  private float amount;

  private long orderId;

  private long customerId;

  private boolean isApproved;
}
