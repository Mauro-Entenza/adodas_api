package com.example.demo.domain.dto;

import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
public class ErrorResponseDto {

  private static Map<String, String> errorMessages;
  private int code;
  private String message;

  public ErrorResponseDto(int code, String message, List<String> errorMessages) {
    this.code = code;
    this.message = message;
  }

  private ErrorResponseDto(Map<String, String> errorMessages) {
    this.code = 400;
    this.message = "Bad Request";
    ErrorResponseDto.errorMessages = errorMessages;
  }

  public static ErrorResponseDto generalError(int code, String message) {
    return new ErrorResponseDto(code, message, (List<String>) errorMessages);
  }

  public static ErrorResponseDto validationError(Map<String, String> errors) {
    return new ErrorResponseDto(errors);
  }
}