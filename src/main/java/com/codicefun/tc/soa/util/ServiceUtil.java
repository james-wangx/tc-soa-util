package com.codicefun.tc.soa.util;

import com.teamcenter.soa.client.model.ServiceData;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

/**
 * Utility class for service operations
 */
@Slf4j
public class ServiceUtil {

    /**
     * Catch partial errors
     *
     * @param serviceData service data
     * @return true if there are partial errors
     */
    public static boolean catchPartialErrors(ServiceData serviceData) {
        if (serviceData == null) {
            log.error("Catch partial error: serviceData is null");
            return true;
        }

        if (serviceData.sizeOfPartialErrors() > 0) {
            for (int i = 0; i < serviceData.sizeOfPartialErrors(); i++) {
                log.error("Catch partial error: {}", Arrays.toString(serviceData.getPartialError(i).getMessages()));
            }
            return true;
        }

        return false;
    }

}
