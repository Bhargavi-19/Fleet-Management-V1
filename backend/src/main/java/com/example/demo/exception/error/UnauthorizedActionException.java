package com.example.demo.exception.error;

/**
 * Thrown when a logged-in user tries to touch a record that does not
 * belong to them - for example a customer opening someone else's booking,
 * or staff acting on a booking from a different hub.
 *
 * The global exception handler turns this into a 403 Forbidden.
 */
public class UnauthorizedActionException extends RuntimeException {

    public UnauthorizedActionException(String message) {
        super(message);
    }
}
