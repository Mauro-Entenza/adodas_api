package com.example.demo.controller;

import com.example.demo.domain.dto.ErrorResponseDto;
import com.example.demo.domain.dto.ServiceDto;
import com.example.demo.exception.ServiceNotFoundException;
import com.example.demo.service.ServiceService;
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
public class ServiceController {

  private final Logger logger = LoggerFactory.getLogger(ServiceController.class);

  @Autowired
  private ServiceService serviceService;

  @GetMapping("/services")
  public ResponseEntity<List<ServiceDto>> getAll() {
    logger.info("BEGIN getAllServices");
    List<ServiceDto> services = serviceService.findAll();
    return new ResponseEntity<>(services, HttpStatus.OK);
  }

  @PostMapping("/services")
  public ResponseEntity<ServiceDto> addService(@Valid @RequestBody ServiceDto serviceDto) {
    logger.info("BEGIN addService");
    ServiceDto newService = serviceService.addService(serviceDto);
    return new ResponseEntity<>(newService, HttpStatus.CREATED);
  }

  @PutMapping("/services/{serviceId}")
  public ResponseEntity<ServiceDto> modifyService(@PathVariable long serviceId,
      @Valid @RequestBody ServiceDto serviceDto)
      throws ServiceNotFoundException {
    logger.info("BEGIN modifyService");
    ServiceDto modifiedService = serviceService.modify(serviceId, serviceDto);
    return new ResponseEntity<>(modifiedService, HttpStatus.OK);
  }

  @DeleteMapping("/services/{serviceId}")
  public ResponseEntity<Void> removeService(@PathVariable long serviceId)
      throws ServiceNotFoundException {
    serviceService.deleteService(serviceId);
    return ResponseEntity.noContent().build();
  }

  @ExceptionHandler(ServiceNotFoundException.class)
  public ResponseEntity<ErrorResponseDto> handleServiceNotFoundException(
      ServiceNotFoundException exception) {
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

  @GetMapping("/services/search")
  public ResponseEntity<List<ServiceDto>> searchServices(
      @RequestParam(value = "name", required = false) String name,
      @RequestParam(value = "minPrice", required = false) Float minPrice,
      @RequestParam(value = "maxPrice", required = false) Float maxPrice,
      @RequestParam(value = "isActive", required = false) Boolean isActive) {

    if (name == null && minPrice == null && maxPrice == null && isActive == null) {
      throw new IllegalArgumentException("At least one filter parameter must be provided");
    }

    logger.info("BEGIN searchServices - name: {}, minPrice: {}, maxPrice: {}, isActive: {}", name,
        minPrice, maxPrice, isActive);
    List<ServiceDto> services = serviceService.searchServices(name, minPrice, maxPrice, isActive);
    return new ResponseEntity<>(services, HttpStatus.OK);
  }

}
