package org.altech.interview.exception;

public class InsufficientStockException extends RuntimeException {
    
    public InsufficientStockException(String message) {
        super(message);
    }
    
    public InsufficientStockException(String productName, int requestedQuantity, int availableQuantity) {
        super(String.format("Insufficient stock for product '%s'. Requested: %d, Available: %d", 
                productName, requestedQuantity, availableQuantity));
    }
} 