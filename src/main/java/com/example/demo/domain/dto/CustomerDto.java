package com.example.demo.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDto {

  private long id;

  @NotNull(message = "The name field is required")
  private String name;
  @NotNull
  private String dni;
  private String surname;

  @Email(message = "The email must be a valid email address")
  @NotNull(message = "The email field is required")
  private String email;

  private boolean isEmailConfirmed;


  private LocalDate registerDate;
}
