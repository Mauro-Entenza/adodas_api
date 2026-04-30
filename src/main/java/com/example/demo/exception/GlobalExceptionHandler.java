package com.example.demo.exception;


import com.example.demo.domain.dto.ErrorResponseDto;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
public class GlobalExceptionHandler {


  @ExceptionHandler({
      OrderNotFoundException.class,
      CustomerNotFoundException.class,
      ItemNotFoundException.class,
      RefundNotFoundException.class,
      ServiceNotFoundException.class
  })
  public ResponseEntity<ErrorResponseDto> handleNotFound(RuntimeException ex) {
    ErrorResponseDto error =
        ErrorResponseDto.generalError(
            HttpStatus.NOT_FOUND.value(),
            ex.getMessage()
        );

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
  }


  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponseDto> handleBadRequest(IllegalArgumentException ex) {
    ErrorResponseDto error =
        ErrorResponseDto.generalError(
            HttpStatus.BAD_REQUEST.value(),
            ex.getMessage()
        );

    return ResponseEntity.badRequest().body(error);
  }


  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponseDto> handleValidationErrors(
      MethodArgumentNotValidException ex) {

    List<String> errors = ex.getBindingResult()
        .getFieldErrors()
        .stream()
        .map(error -> error.getField() + ": " + error.getDefaultMessage())
        .toList();

    ErrorResponseDto response = ErrorResponseDto.validationError(errors);

    return ResponseEntity.badRequest().body(response);
  }


  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponseDto> handleGeneric(Exception ex) {
    ErrorResponseDto error =
        ErrorResponseDto.generalError(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Error interno inesperado"
        );

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorResponseDto> handleJsonError(HttpMessageNotReadableException ex) {

    ErrorResponseDto error = new ErrorResponseDto(
        400,
        "Invalid request body",
        List.of(ex.getMostSpecificCause().getMessage())
    );

    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
  }
}