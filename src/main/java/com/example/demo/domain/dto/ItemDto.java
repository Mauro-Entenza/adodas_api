package com.example.demo.domain.dto;

import com.example.demo.enumerate.CategoryEnum;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {


  private String brand;

  private String color;

  @NotNull(message = "The category field is required")
  private CategoryEnum category;
  @Min(value = 0)
  @NotNull(message = "The price field is required")
  private float price;

  private boolean isWaterproof;

  private LocalDate releaseDate;
}
