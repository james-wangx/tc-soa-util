package com.plm.tc.soa.clientx;

import org.apache.logging.log4j.core.impl.Log4jProvider;

/**
 * Selects the Log4j Core context factory required by the Teamcenter SOA
 * client even when a Spring Boot application also contains log4j-to-slf4j.
 */
public final class A6TcLog4jProvider extends Log4jProvider {

    private static final Integer PRIORITY = 1000;

    @Override
    public Integer getPriority() {
        return PRIORITY;
    }
}
