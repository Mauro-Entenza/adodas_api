package com.example.demo.controller;

import com.example.demo.domain.dto.ErrorResponseDto;
import com.example.demo.domain.dto.ItemDto;
import com.example.demo.enumerate.CategoryEnum;
import com.example.demo.exception.ItemNotFoundException;
import com.example.demo.service.ItemService;
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
public class ItemController {

  private final Logger logger = LoggerFactory.getLogger(ItemController.class);
  @Autowired
  private ItemService itemService;

  @GetMapping("/items")
  public ResponseEntity<List<ItemDto>> getAll() {
    logger.info("BEGIN getAll");
    List<ItemDto> items = itemService.findAll();
    return new ResponseEntity<>(items, HttpStatus.OK);
  }

  @PostMapping("/items")
  public ResponseEntity<ItemDto> addItem(@Valid @RequestBody ItemDto itemDto) {
    logger.info("BEGIN addItem");
    ItemDto newItem = itemService.addItem(itemDto);
    return new ResponseEntity<>(newItem, HttpStatus.CREATED);
  }

  @PutMapping("/items/{itemId}")
  public ResponseEntity<ItemDto> modifyItem(@PathVariable long itemId,
      @Valid @RequestBody ItemDto itemDto)
      throws ItemNotFoundException {
    logger.info("BEGIN modifyItem");
    ItemDto modifiedItem = itemService.modify(itemId, itemDto);
    return new ResponseEntity<>(modifiedItem, HttpStatus.OK);
  }

  @DeleteMapping("/items/{itemId}")
  public ResponseEntity<Void> removeItem(@PathVariable long itemId) throws ItemNotFoundException {
    this.itemService.deleteItem(itemId);
    return ResponseEntity.noContent().build();
  }

  @ExceptionHandler(ItemNotFoundException.class)
  public ResponseEntity<ErrorResponseDto> handleItemNotFoundException(
      ItemNotFoundException exception) {
    ErrorResponseDto error = ErrorResponseDto.generalError(404, exception.getMessage());
    logger.error(exception.getMessage(), exception);
    return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponseDto> handleValidationException(
      MethodArgumentNotValidException exception) {
    List<String> errorMessages = exception.getBindingResult()
        .getFieldErrors()
        .stream()
        .map(error -> error.getField() + ": " + error.getDefaultMessage())
        .toList();

    ErrorResponseDto error = new ErrorResponseDto(400, "Validation Failed", errorMessages);
    logger.error("Validation error: {}", errorMessages);
    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
  }

  @GetMapping("/items/search")
  public ResponseEntity<List<ItemDto>> getItemsByFilter(
      @RequestParam(value = "brand", required = false) String brand,
      @RequestParam(value = "category", required = false) CategoryEnum category,
      @RequestParam(value = "price", required = false) Float price) {

    List<ItemDto> items = itemService.getItemsByFilter(brand, category, price);
    return new ResponseEntity<>(items, HttpStatus.OK);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponseDto> handleUnexpectedException(Exception exception) {
    ErrorResponseDto error = new ErrorResponseDto(500, "An unexpected error occurred",
        List.of(exception.getMessage()));
    logger.error("Unexpected error: {}", exception.getMessage(), exception);

    return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
  }

}
