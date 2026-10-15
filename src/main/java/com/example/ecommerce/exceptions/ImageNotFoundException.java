package com.example.ecommerce.exceptions;

public class ImageNotFoundException extends RuntimeException {
    public ImageNotFoundException() {
        super("Image not found");
    }
}
