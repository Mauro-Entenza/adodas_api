package com.example.demo.exception;

public class ServiceNotFoundException extends Exception {

  public ServiceNotFoundException() {
    super("This service does not exist");
  }

  public ServiceNotFoundException(String message) {
    super(message);
  }
}
