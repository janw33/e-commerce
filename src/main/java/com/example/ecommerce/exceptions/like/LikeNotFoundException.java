package com.example.ecommerce.exceptions.like;

public class LikeNotFoundException extends RuntimeException {
    public LikeNotFoundException() {
        super("Like not found");
    }
}
