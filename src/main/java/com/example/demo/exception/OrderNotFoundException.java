package com.example.demo.exception;

public class OrderNotFoundException extends RuntimeException {

  public OrderNotFoundException() {
    super("This order does not exist");
  }

  public OrderNotFoundException(String message) {
    super(message);
  }

  public OrderNotFoundException(long id) {
    super("Order not found with id: " + id);
  }
}
