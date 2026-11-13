package com.plm.tc.soa.clientx;

import com.plm.tc.soa.util.TcUtil;
import com.teamcenter.soa.SoaConstants;
import com.teamcenter.soa.client.Connection;
import lombok.Getter;

import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

/**
 * This class is used to create and hold a single instance of the Connection object that is shared throughout the
 * application.
 */
public class AppXSharedSession {

    private static final ReentrantLock CALL_LOCK = new ReentrantLock(true);

    /**
     * Single instance of the Connection object that is shared throughout the application.
     * This Connection object is needed whenever a Service stub is instantiated.
     */
    @Getter
    private static volatile Connection connection;

    private static volatile AppXCredentialManager sharedCredentialManager;
    private static volatile TcUtil sharedTcUtil;

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
        sharedCredentialManager = credentialManager;

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

    /**
     * Initialize the application-wide service-account session.
     */
    public static synchronized void initialize(
            String host,
            String username,
            String password,
            String group,
            String role,
            String locale,
            String sessionDiscriminator) {
        if (sharedTcUtil != null) {
            return;
        }
        Objects.requireNonNull(username, "Service-account username is required.");
        Objects.requireNonNull(password, "Service-account password is required.");

        new AppXSharedSession(host);
        sharedCredentialManager.setUserPassword(username, password, sessionDiscriminator);
        sharedCredentialManager.setGroupRole(
                group == null ? "" : group,
                role == null ? "" : role);
        TcUtil candidate = new TcUtil(connection);
        if (!candidate.login(username, password, group, role, locale, sessionDiscriminator)) {
            connection = null;
            throw new IllegalStateException(
                    "Unable to log in the Teamcenter shared service account '" + username + "'.");
        }
        sharedTcUtil = candidate;
    }

    /**
     * Execute an operation on the shared connection. Calls are serialized
     * because the Teamcenter connection holds mutable client-side model state.
     */
    public static <T> T execute(Function<TcUtil, T> operation) {
        CALL_LOCK.lock();
        try {
            TcUtil tcUtil = sharedTcUtil;
            if (tcUtil == null) {
                throw new IllegalStateException("Teamcenter shared session is not initialized.");
            }
            return operation.apply(tcUtil);
        } finally {
            CALL_LOCK.unlock();
        }
    }

    /**
     * Log out and clear the shared session.
     */
    public static void close() {
        CALL_LOCK.lock();
        try {
            if (sharedTcUtil != null) {
                sharedTcUtil.logout();
            }
        } finally {
            sharedTcUtil = null;
            sharedCredentialManager = null;
            connection = null;
            CALL_LOCK.unlock();
        }
    }
}
