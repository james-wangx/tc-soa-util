package com.xcplm.tc.soa.clientx;

import com.teamcenter.schemas.soa._2006_03.exceptions.ConnectionException;
import com.teamcenter.schemas.soa._2006_03.exceptions.InternalServerException;
import com.teamcenter.schemas.soa._2006_03.exceptions.ProtocolException;
import com.teamcenter.soa.client.ExceptionHandler;
import com.teamcenter.soa.exceptions.CanceledOperationException;
import com.xcplm.tc.soa.exception.SoaConnException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

@Slf4j
public class AppXExceptionHandler implements ExceptionHandler {

    public void handleException(InternalServerException ise) {
        log.error("Exception caught in {}.handleException(InternalServerException).", this.getClass().getName());
        LineNumberReader reader = new LineNumberReader(new InputStreamReader(System.in));

        if (ise instanceof ConnectionException || ise.getMessage().startsWith("未能获得服务器指派")) {
            // ConnectionException are typically due to a network error (server down .etc.)
            throw new SoaConnException(ise.getMessage());
        } else if (ise instanceof ProtocolException) {
            // ProtocolException are typically due to programming errors (content of HTTP request is incorrect).
            // These are generally can not be recovered from.
            log.warn("The server returned an protocol error.\n" +
                     "{}\n" +
                     "This is most likely the result of a programming error.\n" +
                     "Do you wish to retry the last service request?[y/n]",
                     ise.getMessage());
        } else {
            log.warn("The server returned an internal server error.\n" +
                     "{}\n" +
                     "This is most likely the result of a programming error.\n" +
                     "A RuntimeException will be thrown.",
                     ise.getMessage());
            throw new RuntimeException(ise.getMessage());
        }

        try {
            String retry = reader.readLine();
            // If yes, return to the calling SOA client framework, where the
            // last service request will be resent.
            if (retry.equalsIgnoreCase("y") || retry.equalsIgnoreCase("yes")) return;

            throw new RuntimeException("The user has opted not to retry the last request");
        } catch (IOException e) {
            log.error("Failed to read user response.\nA RuntimeException will be thrown.");
            throw new RuntimeException(e.getMessage());
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
