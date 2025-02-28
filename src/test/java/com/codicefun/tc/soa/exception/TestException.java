package com.codicefun.tc.soa.exception;

/**
 * Test exception.
 */
public class TestException extends RuntimeException {

    public TestException(String message) {
        super(message);
    }

    public TestException(String message, Throwable cause) {
        super(message, cause);
    }

}
