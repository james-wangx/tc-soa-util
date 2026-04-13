package com.xcplm.tc.soa.clientx;

import com.teamcenter.soa.SoaConstants;
import com.teamcenter.soa.client.Connection;
import lombok.Getter;

/**
 * This class is used to create and hold a single instance of the Connection object that is shared throughout the
 * application.
 */
@SuppressWarnings("unused")
public class AppXSharedSession {

    /**
     * Single instance of the Connection object that is shared throughout the application.
     * This Connection object is needed whenever a Service stub is instantiated.
     */
    @Getter
    private static Connection connection;

    /**
     * Create an instance of the Session with a connection to the specified server.
     * <p>
     * Add implementations of the ExceptionHandler, PartialErrorListener, ChangeListener, and DeleteListeners.
     *
     * @param host Address of the host to connect to, http://serverName:port/tc
     */
    @SuppressWarnings("JavadocLinkAsPlainText")
    public AppXSharedSession(String host) {
        // Create an instance of the CredentialManager, this is used by the SOA Framework to get the user's credentials
        // when challenged by the server (session timeout on the web tier).
        AppXCredentialManager credentialManager = new AppXCredentialManager();

        String protocol;
        String envNameTccs = null;
        if (host.startsWith("http")) {
            protocol = SoaConstants.HTTP;
        } else if (host.startsWith("tccs")) {
            protocol = SoaConstants.TCCS;
            host = host.trim();
            int envNameStart = host.indexOf('/') + 2;
            envNameTccs = host.substring(envNameStart);
            host = "";
        } else {
            protocol = SoaConstants.IIOP;
        }

        // Create the Connection object, no contact is made with the server until a service request is made
        connection = new Connection(host, credentialManager, SoaConstants.REST, protocol);

        if (protocol.equals(SoaConstants.TCCS)) {
            connection.setOption(Connection.TCCS_ENV_NAME, envNameTccs);
        }

        // Add an ExceptionHandler to the Connection, this will handle any InternalServerException,
        // communication errors, XML marshaling errors .etc
        connection.setExceptionHandler(new AppXExceptionHandler());

        // While the above ExceptionHandler is required, all the following Listeners are optional.
        // Client application can add as many or as few Listeners of each type that they want.

        // Add a Partial Error Listener, this will be notified when ever a service returns partial errors.
        // connection.getModelManager().addPartialErrorListener(new AppXPartialErrorListener());

        // Add a Change and Delete Listener, this will be notified when ever a service returns model objects that have
        // been updated or deleted.
        // connection.getModelManager().addModelEventListener(new AppXModelEventListener());

        // Add a Request Listener, this will be notified before and after each service request is sent to the server.
        // Connection.addRequestListener(new AppXRequestListener());
    }

}
