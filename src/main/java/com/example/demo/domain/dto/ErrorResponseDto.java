package com.example.demo.domain.dto;

import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponseDto {

  private int code;
  private String message;
  private List<String> errors;


  public static ErrorResponseDto generalError(int code, String message) {
    return new ErrorResponseDto(code, message, Collections.emptyList());
  }

  
  public static ErrorResponseDto validationError(List<String> errors) {
    return new ErrorResponseDto(400, "Bad Request", errors);
  }
}