package com.plm.tc.soa.util;

@FunctionalInterface
public interface Retryable<T> {

    T execute() throws Exception;

}
