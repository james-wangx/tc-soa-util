package com.xcplm.tc.soa.clientx;

import com.teamcenter.schemas.soa._2006_03.exceptions.ConnectionException;
import com.teamcenter.schemas.soa._2006_03.exceptions.InternalServerException;
import com.teamcenter.schemas.soa._2006_03.exceptions.ProtocolException;
import com.teamcenter.soa.client.ExceptionHandler;
import com.teamcenter.soa.exceptions.CanceledOperationException;
import com.xcplm.tc.soa.exception.SoaConnException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AppXExceptionHandler implements ExceptionHandler {

    public void handleException(InternalServerException ise) {
        log.error("Exception caught in {}.handleException(InternalServerException).", this.getClass().getName());

        if (ise instanceof ConnectionException ||
            ise instanceof ProtocolException ||
            ise.getMessage().contains("未能获得服务器指派") ||
            ise.getMessage().contains("No response received") ||
            ise.getMessage().contains("Exception in ServerAccess for user")) {
            // ConnectionException are typically due to a network error (server down .etc.)
            throw new SoaConnException(ise.getMessage());
        } else {
            log.warn("The server returned an internal server error.\n" +
                     "{}\n" +
                     "This is most likely the result of a programming error.\n" +
                     "A RuntimeException will be thrown.",
                     ise.getMessage());
            throw new RuntimeException(ise.getMessage());
        }
    }

    /**
     * Handle CanceledOperationException.
     * This is expected from the login tests with bad credentials,
     * and the AnyUserCredentials class not prompting for different credentials.
     *
     * @param coe the CanceledOperationException to handle
     */
    public void handleException(CanceledOperationException coe) {
        log.error("Exception caught in {}.handleException(CanceledOperationException).", this.getClass().getName());

        // Expecting this from the login tests with bad credentials,
        // and the AnyUserCredentials class not prompting for different credentials
        throw new RuntimeException(coe);
    }

}
