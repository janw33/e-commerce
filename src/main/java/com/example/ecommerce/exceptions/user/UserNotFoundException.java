package com.example.ecommerce.exceptions.user;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException() {super("User not found");}
}
