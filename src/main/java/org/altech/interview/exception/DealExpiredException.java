package org.altech.interview.exception;

public class DealExpiredException extends RuntimeException {
    
    public DealExpiredException(String message) {
        super(message);
    }
    
    public DealExpiredException(String dealName, String reason) {
        super(String.format("Deal '%s' has expired: %s", dealName, reason));
    }
} 