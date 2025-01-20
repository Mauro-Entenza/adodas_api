package com.example.demo.exception;

public class RefundNotFoundException extends Exception {

  public RefundNotFoundException() {
    super("This refund does not exist");
  }

  public RefundNotFoundException(String message) {
    super(message);
  }
}
