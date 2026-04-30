package com.example.demo.controller;

import com.example.demo.domain.dto.ErrorResponseDto;
import com.example.demo.domain.dto.RefundDto;
import com.example.demo.exception.RefundNotFoundException;
import com.example.demo.service.RefundService;
import jakarta.validation.Valid;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/refunds")
public class RefundController {

  private final Logger logger = LoggerFactory.getLogger(RefundController.class);

  @Autowired
  private RefundService refundService;

  // ------------------ GET CON FILTROS ------------------
  @GetMapping
  public ResponseEntity<List<RefundDto>> getRefunds(
      @RequestParam(value = "reason", required = false) String reason,
      @RequestParam(value = "minAmount", required = false) Float minAmount,
      @RequestParam(value = "isApproved", required = false) Boolean approved) { //  {

    logger.info("BEGIN getRefunds with filters: reason={}, minAmount={}, approved={}",
        reason, minAmount, approved);

    List<RefundDto> refunds = refundService.findByFilters(reason, minAmount, approved);
    return new ResponseEntity<>(refunds, HttpStatus.OK);
  }

  // ------------------ CRUD ------------------
  @PostMapping
  public ResponseEntity<RefundDto> addRefund(@Valid @RequestBody RefundDto refundDto) {
    logger.info("BEGIN addRefund");
    RefundDto newRefund = refundService.addRefund(refundDto);
    return new ResponseEntity<>(newRefund, HttpStatus.CREATED);
  }

  @PatchMapping("/{refundId}")
  public ResponseEntity<RefundDto> patchRefund(@PathVariable long refundId,
      @RequestBody Map<String, Object> updates) {

    logger.info("BEGIN patchRefund");
    try {
      RefundDto updatedRefund = refundService.patchRefund(refundId, updates);
      return new ResponseEntity<>(updatedRefund, HttpStatus.OK);
    } catch (RefundNotFoundException e) {
      return ResponseEntity.notFound().build();
    }
  }

  @PutMapping("/{refundId}")
  public ResponseEntity<RefundDto> modifyRefund(@PathVariable long refundId,
      @Valid @RequestBody RefundDto refundDto) throws RefundNotFoundException {

    logger.info("BEGIN modifyRefund");
    RefundDto modifiedRefund = refundService.modify(refundId, refundDto);
    return new ResponseEntity<>(modifiedRefund, HttpStatus.OK);
  }

  @DeleteMapping("/{refundId}")
  public ResponseEntity<Void> removeRefund(@PathVariable long refundId)
      throws RefundNotFoundException {

    refundService.deleteRefund(refundId);
    return ResponseEntity.noContent().build();
  }

  // ------------------ HANDLERS ------------------
  @ExceptionHandler(RefundNotFoundException.class)
  public ResponseEntity<ErrorResponseDto> handleRefundNotFoundException(
      RefundNotFoundException exception) {

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

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponseDto> handleIllegalArgument(IllegalArgumentException ex) {
    ErrorResponseDto error = ErrorResponseDto.generalError(404, ex.getMessage());
    logger.error(ex.getMessage(), ex);
    return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponseDto> handleUnexpectedException(Exception exception) {
    ErrorResponseDto error = new ErrorResponseDto(500, "An unexpected error occurred",
        List.of(exception.getMessage()));
    logger.error("Unexpected error: {}", exception.getMessage(), exception);

    return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}