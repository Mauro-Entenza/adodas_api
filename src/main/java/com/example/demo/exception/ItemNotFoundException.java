package com.example.demo.exception;

public class ItemNotFoundException extends Exception {
    public ItemNotFoundException() {
        super("This item does not exist");
    }

    public ItemNotFoundException(String message) {
        super(message);
    }
}
