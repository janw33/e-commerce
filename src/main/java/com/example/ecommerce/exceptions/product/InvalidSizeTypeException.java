package com.example.ecommerce.exceptions.product;

public class InvalidSizeTypeException extends RuntimeException {
    public InvalidSizeTypeException() {
        super("Invalid size type for the selected category");
    }
}
