package com.example.demo.domain.dto;

import java.util.Map;
import lombok.Data;

@Data
public class ErrorResponseDto {

  private int code;
  private String message;
  private Map<String, String> errorMessages;

  private ErrorResponseDto(int code, String message) {
    this.code = code;
    this.message = message;
  }

  private ErrorResponseDto(Map<String, String> errorMessages) {
    this.code = 400;
    this.message = "Bad Request";
    this.errorMessages = errorMessages;
  }

  public static ErrorResponseDto generalError(int code, String message) {
    return new ErrorResponseDto(code, message);
  }

  public static ErrorResponseDto validationError(Map<String, String> errors) {
    return new ErrorResponseDto(errors);
  }
}