package com.example.ecommerce.exceptions.image;

public class InvalidImageCountException extends RuntimeException {
    public InvalidImageCountException() {
        super("Product must have between 1 and 20 images");
    }
}
