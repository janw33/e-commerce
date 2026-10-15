package com.example.ecommerce.exceptions.product;

public class InvalidStatusException extends RuntimeException {
    public InvalidStatusException() {
        super("Invalid status for this operation");
    }
}
