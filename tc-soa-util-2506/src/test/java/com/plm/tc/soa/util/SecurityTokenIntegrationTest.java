package com.plm.tc.soa.util;

import com.plm.tc.soa.clientx.AppXSession;
import com.teamcenter.schemas.soa._2006_03.exceptions.ServiceException;
import com.teamcenter.services.strong.core.SessionService;
import com.teamcenter.services.strong.core._2006_03.Session.LoginResponse;
import com.teamcenter.soa.client.model.ServiceData;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Verifies that a Teamcenter security token can create a new session after
 * the session that issued the token has logged out.
 *
 * Set TC_USERNAME and TC_PASSWORD to enable this integration test.
 */
class SecurityTokenIntegrationTest {

    private static final String DEFAULT_TC_URL = "http://192.168.80.107:7001/tc";
    private static final String DEFAULT_LOCALE = "zh_CN";
    private static final int TOKEN_DURATION_SECONDS = 60;

    @Test
    void securityTokenCanLoginAfterIssuerSessionLogout() throws Exception {
        String tcUrl = environmentValue("TC_URL", DEFAULT_TC_URL);
        String username = environmentValue("TC_USERNAME", null);
        String password = environmentValue("TC_PASSWORD", null);
        String locale = environmentValue("TC_LOCALE", DEFAULT_LOCALE);

        Assumptions.assumeTrue(
                username != null && !username.isBlank() && password != null && !password.isBlank(),
                "Set TC_USERNAME and TC_PASSWORD to run the security token integration test.");

        String securityToken = loginAndGetSecurityToken(tcUrl, username, password, locale);
        assertNotNull(securityToken, "Teamcenter returned a null security token.");
        assertFalse(securityToken.isBlank(), "Teamcenter returned an empty security token.");

        AppXSession tokenSession = new AppXSession(tcUrl);
        SessionService tokenSessionService = SessionService.getService(tokenSession.getConnection());
        boolean tokenLoginCompleted = false;
        try {
            LoginResponse response = tokenSessionService.login(
                    username, securityToken, "", "", locale, "tc-security-token-verification");
            tokenLoginCompleted = true;
            assertNotNull(response.user, "Security token login did not return a user.");
            assertNoPartialErrors(response.serviceData);
        } finally {
            if (tokenLoginCompleted) {
                logout(tokenSessionService);
            }
        }
    }

    private static String loginAndGetSecurityToken(
            String tcUrl, String username, String password, String locale) throws Exception {
        AppXSession sourceSession = new AppXSession(tcUrl);
        SessionService sourceSessionService = SessionService.getService(sourceSession.getConnection());
        boolean sourceLoginCompleted = false;
        try {
            LoginResponse response = sourceSessionService.login(
                    username, password, "", "", locale, "tc-security-token-source");
            sourceLoginCompleted = true;
            assertNotNull(response.user, "Password login did not return a user.");
            assertNoPartialErrors(response.serviceData);

            com.teamcenter.services.internal.strong.core.SessionService internalSessionService =
                    com.teamcenter.services.internal.strong.core.SessionService.getService(
                            sourceSession.getConnection());
            return internalSessionService.getSecurityToken(TOKEN_DURATION_SECONDS);
        } finally {
            if (sourceLoginCompleted) {
                logout(sourceSessionService);
            }
        }
    }

    private static void logout(SessionService sessionService) throws ServiceException {
        ServiceData serviceData = sessionService.logout();
        assertNoPartialErrors(serviceData);
    }

    private static void assertNoPartialErrors(ServiceData serviceData) {
        assertNotNull(serviceData, "Teamcenter returned no service data.");
        assertEquals(0, serviceData.sizeOfPartialErrors(), "Teamcenter returned partial errors.");
    }

    private static String environmentValue(String name, String defaultValue) {
        String value = System.getenv(name);
        return value == null || value.isEmpty() ? defaultValue : value;
    }
}
