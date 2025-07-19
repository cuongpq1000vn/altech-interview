package org.altech.interview.exception;

public class ProductNameAlreadyExistsException extends RuntimeException {
    
    public ProductNameAlreadyExistsException(String productName) {
        super("Product with name '" + productName + "' already exists");
    }
    
    public ProductNameAlreadyExistsException(String productName, Throwable cause) {
        super("Product with name '" + productName + "' already exists", cause);
    }
} 