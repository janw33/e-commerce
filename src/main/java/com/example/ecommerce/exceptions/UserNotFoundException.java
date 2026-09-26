package com.example.ecommerce.exceptions;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException() {super("User not found");}
}
