package com.xcplm.tc.soa.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RetryUtil {

    /**
     * Executes the given action with retry logic.
     *
     * @param action                The action to execute.
     * @param maxRetries            Maximum number of retries (-1 for infinite retries).
     * @param retryIntervalMinutes  Interval between retries in minutes.
     * @param retryOnExceptionClass The class of exceptions that should trigger a retry.
     * @param <T>                   The return type of the action.
     * @param <E>                   The type of exception to retry on.
     * @return The result of the action if successful.
     * @throws Exception If the action fails after all retries or if an unexpected exception occurs.
     */
    @SuppressWarnings({"BusyWait", "UnusedReturnValue"})
    public static <T, E extends Exception> T executeWithRetry(
            Retryable<T> action,
            int maxRetries,
            long retryIntervalMinutes,
            Class<E> retryOnExceptionClass) throws Exception {
        int attempt = 0;

        while (true) {
            try {
                T result = action.execute();
                attempt = 0;
                return result;
            } catch (Exception e) {
                if (retryOnExceptionClass.isInstance(e)) {
                    log.error("Connection failed, attempt {}: {}", ++attempt, e.getMessage());

                    if (maxRetries != -1 && attempt >= maxRetries) {
                        throw new RuntimeException("Maximum retry attempts reached, operation still failed", e);
                    }

                    log.error("Waiting {} minute(s) before retry...", retryIntervalMinutes);
                    Thread.sleep(retryIntervalMinutes * 60 * 1000);
                } else {
                    throw e;
                }
            }
        }
    }

}
