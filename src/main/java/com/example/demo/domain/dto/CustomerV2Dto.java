package com.example.demo.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerV2Dto {

  private long id;

  @NotNull(message = "The name field is required")
  private String name;

  @Email(message = "The email must be a valid email address")
  @NotNull(message = "The email field is required")
  private String email;

  @NotNull
  private String phone;
}