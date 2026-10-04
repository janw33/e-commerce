package com.example.ecommerce.exceptions.brand;

public class BrandNotFoundException extends RuntimeException{
    public BrandNotFoundException() {super("Brand not found");}
}
