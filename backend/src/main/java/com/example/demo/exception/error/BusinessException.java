package com.example.demo.exception.error;

/**
 * Thrown when a business rule is broken - for example trying to cancel a
 * booking that is already completed, or handing over a car that is not
 * available.
 *
 * The global exception handler turns this into a 400 Bad Request so the
 * frontend can show the message to the user as-is.
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}
