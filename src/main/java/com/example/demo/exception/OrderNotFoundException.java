package com.example.demo.exception;

public class OrderNotFoundException extends Exception {

  public OrderNotFoundException() {
    super("This order does not exist");
  }

  public OrderNotFoundException(String message) {
    super(message);
  }
}
