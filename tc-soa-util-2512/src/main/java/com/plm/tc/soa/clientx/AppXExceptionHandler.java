package com.plm.tc.soa.clientx;

import com.plm.tc.soa.exception.SoaConnException;
import com.teamcenter.schemas.soa._2006_03.exceptions.ConnectionException;
import com.teamcenter.schemas.soa._2006_03.exceptions.InternalServerException;
import com.teamcenter.schemas.soa._2006_03.exceptions.ProtocolException;
import com.teamcenter.soa.client.ExceptionHandler;
import com.teamcenter.soa.exceptions.CanceledOperationException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AppXExceptionHandler implements ExceptionHandler {

    public void handleException(InternalServerException ise) {
        String message = ise.getMessage() == null ? "" : ise.getMessage();

        if (ise instanceof ConnectionException ||
            ise instanceof ProtocolException ||
            message.contains("未能获得服务器指派") ||
            message.contains("No response received") ||
            message.contains("Exception in ServerAccess for user")) {
            // ConnectionException are typically due to a network error (server down .etc.)
            throw new SoaConnException(message, ise);
        } else {
            log.debug("Propagating Teamcenter SOA internal server exception.", ise);
            throw new RuntimeException(message, ise);
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
        log.debug("Propagating canceled Teamcenter SOA operation.", coe);

        // Expecting this from the login tests with bad credentials,
        // and the AnyUserCredentials class not prompting for different credentials
        throw new RuntimeException(coe);
    }

}
