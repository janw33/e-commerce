package com.example.ecommerce.exceptions.user;

public class UserInactiveException extends RuntimeException {
    public UserInactiveException() {
        super("User account is inactive");
    }
}
