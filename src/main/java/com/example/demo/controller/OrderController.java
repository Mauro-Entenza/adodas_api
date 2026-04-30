package com.example.demo.controller;

import com.example.demo.domain.dto.ErrorResponseDto;
import com.example.demo.domain.dto.OrderDto;
import com.example.demo.exception.OrderNotFoundException;
import com.example.demo.service.OrderService;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController {

  private final Logger logger = LoggerFactory.getLogger(OrderController.class);

  @Autowired
  private OrderService orderService;

  @GetMapping("/orders")
  public ResponseEntity<List<OrderDto>> getOrders(
      @RequestParam(value = "delivered", required = false) Boolean delivered,
      @RequestParam(value = "minPrice", required = false) Float minPrice,
      @RequestParam(value = "maxPrice", required = false) Float maxPrice) {

    logger.info("BEGIN getOrders - delivered={}, minPrice={}, maxPrice={}", delivered, minPrice,
        maxPrice);
    List<OrderDto> orders = orderService.findByFilters(delivered, minPrice, maxPrice);
    return new ResponseEntity<>(orders, HttpStatus.OK);
  }

  @PostMapping("/orders")
  public ResponseEntity<OrderDto> addOrder(@RequestBody OrderDto orderDto) {
    logger.info("BEGIN addOrder");
    OrderDto newOrder = orderService.addOrder(orderDto);
    return new ResponseEntity<>(newOrder, HttpStatus.CREATED);
  }

  @PutMapping("/orders/{orderId}")
  public ResponseEntity<OrderDto> modifyOrder(@PathVariable long orderId,
      @RequestBody OrderDto orderDto)
      throws OrderNotFoundException {
    logger.info("BEGIN modifyOrder");
    orderDto.setId(orderId);
    OrderDto modifiedOrder = orderService.modify(orderId, orderDto);
    return new ResponseEntity<>(modifiedOrder, HttpStatus.OK);
  }

  @PatchMapping("/orders/{orderId}")
  public ResponseEntity<OrderDto> patchOrder(@PathVariable long orderId,
      @RequestBody Map<String, Object> updates)
      throws OrderNotFoundException {
    logger.info("BEGIN patchOrder - id={}, updates={}", orderId, updates);
    OrderDto updatedOrder = orderService.patchOrder(orderId, updates);
    return new ResponseEntity<>(updatedOrder, HttpStatus.OK);
  }

  @DeleteMapping("/orders/{orderId}")
  public ResponseEntity<Void> deleteOrder(@PathVariable long orderId)
      throws OrderNotFoundException {
    orderService.deleteOrder(orderId);
    return ResponseEntity.noContent().build();
  }

  // ===== Exception Handlers =====
  @ExceptionHandler(OrderNotFoundException.class)
  public ResponseEntity<ErrorResponseDto> handleOrderNotFound(OrderNotFoundException ex) {
    ErrorResponseDto error = ErrorResponseDto.generalError(404, ex.getMessage());
    logger.error(ex.getMessage(), ex);
    return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponseDto> handleValidationErrors(
      MethodArgumentNotValidException ex) {
    List<String> errors = ex.getBindingResult().getFieldErrors().stream()
        .map(err -> err.getField() + ": " + err.getDefaultMessage())
        .toList();
    ErrorResponseDto error = new ErrorResponseDto(400, "Validation failed", errors);
    logger.error("Validation errors: {}", errors);
    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponseDto> handleUnexpected(Exception ex) {
    ErrorResponseDto error = new ErrorResponseDto(500, "An unexpected error occurred",
        List.of(ex.getMessage()));
    logger.error("Unexpected error: {}", ex.getMessage(), ex);
    return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}