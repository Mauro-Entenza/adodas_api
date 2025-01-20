package com.example.demo.controller;

import com.example.demo.domain.dto.ErrorResponseDto;
import com.example.demo.domain.dto.OrderDto;
import com.example.demo.exception.OrderNotFoundException;
import com.example.demo.service.OrderService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
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
public class OrderController {

  private final Logger logger = LoggerFactory.getLogger(OrderController.class);

  @Autowired
  private OrderService orderService;

  @GetMapping("/orders")
  public ResponseEntity<List<OrderDto>> getAll() {
    logger.info("BEGIN getAllOrders");
    List<OrderDto> orders = orderService.findAll();
    return new ResponseEntity<>(orders, HttpStatus.OK);
  }

  @PostMapping("/orders")
  public ResponseEntity<OrderDto> addOrder(@Valid @RequestBody OrderDto orderDto) {
    logger.info("BEGIN addOrder");
    OrderDto newOrder = orderService.addOrder(orderDto);
    return new ResponseEntity<>(newOrder, HttpStatus.CREATED);
  }

  @PutMapping("/orders/{orderId}")
  public ResponseEntity<OrderDto> modifyOrder(@PathVariable long orderId,
      @Valid @RequestBody OrderDto orderDto) throws OrderNotFoundException {

    orderDto.setId(orderId);
    OrderDto modifiedOrder = orderService.modify(orderId, orderDto);
    return new ResponseEntity<>(modifiedOrder, HttpStatus.OK);
  }


  @DeleteMapping("/orders/{orderId}")
  public ResponseEntity<Void> removeOrder(@PathVariable long orderId)
      throws OrderNotFoundException {
    orderService.deleteOrder(orderId);
    return ResponseEntity.noContent().build();
  }

  @ExceptionHandler(OrderNotFoundException.class)
  public ResponseEntity<ErrorResponseDto> handleOrderNotFoundException(
      OrderNotFoundException exception) {
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

  @GetMapping("/orders/search")
  public ResponseEntity<List<OrderDto>> searchOrders(
      @RequestParam(value = "isDelivered", required = false) Boolean isDelivered,
      @RequestParam(value = "minPrice", required = false) Float minPrice,
      @RequestParam(value = "orderDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate orderDate) {

    logger.info("BEGIN searchOrders - isDelivered: {}, minPrice: {}, orderDate: {}", isDelivered,
        minPrice, orderDate);
    List<OrderDto> orders = orderService.searchOrders(isDelivered, minPrice, orderDate);
    return new ResponseEntity<>(orders, HttpStatus.OK);
  }
}
