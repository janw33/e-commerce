package com.example.ecommerce.exceptions.like;

public class LikeAlreadyExistsException extends RuntimeException {
    public LikeAlreadyExistsException() {
        super("Like already exists");
    }
}
