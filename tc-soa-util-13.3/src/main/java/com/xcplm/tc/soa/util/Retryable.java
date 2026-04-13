package com.xcplm.tc.soa.util;

@FunctionalInterface
public interface Retryable<T> {

    T execute() throws Exception;

}
