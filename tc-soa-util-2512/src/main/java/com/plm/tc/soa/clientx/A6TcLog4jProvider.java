package com.plm.tc.soa.clientx;

import org.apache.logging.log4j.core.impl.Log4jContextFactory;
import org.apache.logging.log4j.spi.DefaultThreadContextMap;
import org.apache.logging.log4j.spi.Provider;

/**
 * Selects the Log4j Core context factory required by the Teamcenter SOA
 * client even when a Spring Boot application also contains log4j-to-slf4j.
 */
public final class A6TcLog4jProvider extends Provider {

    private static final Integer PRIORITY = 1000;

    public A6TcLog4jProvider() {
        super(
                PRIORITY,
                "2.6.0",
                Log4jContextFactory.class,
                DefaultThreadContextMap.class);
    }
}
