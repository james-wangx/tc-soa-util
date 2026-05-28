package com.plm.tc.soa.exception;

/**
 * SoaUtilException
 */
public class SoaUtilException extends RuntimeException {

    public SoaUtilException(String message) {
        super(message);
    }

    public SoaUtilException(String message, Throwable cause) {
        super(message, cause);
    }

}
