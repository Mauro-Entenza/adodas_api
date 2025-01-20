package com.example.demo.controller;

import com.example.demo.domain.dto.CustomerDto;
import com.example.demo.domain.dto.ErrorResponseDto;
import com.example.demo.exception.CustomerNotFoundException;
import com.example.demo.service.CustomerService;
import jakarta.validation.Valid;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomerController {

  private final Logger logger = LoggerFactory.getLogger(CustomerController.class);

  @Autowired
  private CustomerService customerService;

  @GetMapping("/customers")
  public ResponseEntity<List<CustomerDto>> getAll() {
    logger.info("BEGIN getAll");
    List<CustomerDto> customers = customerService.findAll();
    return new ResponseEntity<>(customers, HttpStatus.OK);
  }

  @PostMapping("/customers")
  public ResponseEntity<CustomerDto> addCustomer(@Valid @RequestBody CustomerDto customerDto) {
    logger.info("BEGIN addCustomer");
    CustomerDto newCustomer = customerService.addCustomer(customerDto);
    return new ResponseEntity<>(newCustomer, HttpStatus.CREATED);
  }

  @PutMapping("/customers/{customerId}")
  public ResponseEntity<CustomerDto> modifyCustomer(@PathVariable long customerId,
      @Valid @RequestBody CustomerDto customerDto)
      throws CustomerNotFoundException {
    logger.info("BEGIN modifyCustomer");
    CustomerDto modifiedCustomer = customerService.modify(customerId, customerDto);
    return new ResponseEntity<>(modifiedCustomer, HttpStatus.OK);
  }

  @DeleteMapping("/customers/{customerId}")
  public ResponseEntity<Void> removeCustomer(@PathVariable long customerId)
      throws CustomerNotFoundException {
    customerService.deleteCustomer(customerId);
    return ResponseEntity.noContent().build();
  }

  @ExceptionHandler(CustomerNotFoundException.class)
  public ResponseEntity<ErrorResponseDto> handleCustomerNotFoundException(
      CustomerNotFoundException exception) {
    ErrorResponseDto error = ErrorResponseDto.generalError(404, exception.getMessage());
    logger.error(exception.getMessage(), exception);
    return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponseDto> handleValidationErrors(
      MethodArgumentNotValidException exception) {

    List<String> errors = exception.getBindingResult().getFieldErrors().stream()
        .map(err -> err.getField() + ": " + err.getDefaultMessage())
        .toList();

    ErrorResponseDto error = new ErrorResponseDto(400, "Validation failed", errors);
    logger.error("Validation errors: {}", errors);

    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponseDto> handleUnexpectedException(Exception exception) {
    ErrorResponseDto error = new ErrorResponseDto(500, "An unexpected error occurred",
        List.of(exception.getMessage()));
    logger.error("Unexpected error: {}", exception.getMessage(), exception);

    return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @GetMapping("/customers/search")
  public ResponseEntity<List<CustomerDto>> searchCustomers(
      @RequestParam(value = "name", required = false) String name,
      @RequestParam(value = "email", required = false) String email,
      @RequestParam(value = "surname", required = false) String surname) {

    logger.info("BEGIN searchCustomers - name: {}, email: {}, surname: {}", name, email, surname);
    List<CustomerDto> customers = customerService.searchCustomers(name, email, surname);
    return new ResponseEntity<>(customers, HttpStatus.OK);
  }
}
