package com.plm.tc.soa.util;

import com.plm.tc.soa.clientx.AppXSharedSession;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifies password changes through the shared service-account session.
 *
 * Set TC_SERVICE_USERNAME, TC_SERVICE_PASSWORD, TC_TARGET_USERNAME, and
 * TC_TARGET_PASSWORD to enable this integration test.
 */
class SharedSessionPasswordChangeIntegrationTest {

    private static final String DEFAULT_TC_URL = "https://192.168.80.110:7001/tc";

    @Test
    void changesAndRestoresPasswordWithoutRelogging() {
        String tcUrl = environmentValue("TC_URL", DEFAULT_TC_URL);
        String serviceUsername = environmentValue("TC_SERVICE_USERNAME", null);
        String servicePassword = environmentValue("TC_SERVICE_PASSWORD", null);
        String targetUsername = environmentValue("TC_TARGET_USERNAME", null);
        String targetPassword = environmentValue("TC_TARGET_PASSWORD", null);

        Assumptions.assumeTrue(
                serviceUsername != null
                && servicePassword != null
                && targetUsername != null
                && targetPassword != null,
                "Set service-account and target-user credentials to run this test.");

        String temporaryPassword = "A6Test" + UUID.randomUUID()
                                                     .toString()
                                                     .replace("-", "")
                                                     .substring(0, 8);
        boolean temporaryPasswordActive = false;
        try {
            AppXSharedSession.initialize(
                    tcUrl,
                    serviceUsername,
                    servicePassword,
                    "",
                    "",
                    "en_US",
                    "A6SharedSessionIntegration");

            boolean changed = AppXSharedSession.execute(tcUtil ->
                    resetPassword(
                            tcUtil,
                            targetUsername,
                            targetPassword,
                            temporaryPassword));
            assertTrue(changed);
            temporaryPasswordActive = true;

            boolean restored = AppXSharedSession.execute(tcUtil ->
                    resetPassword(
                            tcUtil,
                            targetUsername,
                            temporaryPassword,
                            targetPassword));
            assertTrue(restored);
            temporaryPasswordActive = false;
        } finally {
            if (temporaryPasswordActive) {
                try {
                    AppXSharedSession.execute(tcUtil ->
                            resetPassword(
                                    tcUtil,
                                    targetUsername,
                                    temporaryPassword,
                                    targetPassword));
                } catch (RuntimeException ignored) {
                    // The primary test failure remains the reported failure.
                }
            }
            AppXSharedSession.close();
        }
    }

    private static String environmentValue(String name, String defaultValue) {
        String value = System.getenv(name);
        return value == null || value.isEmpty() ? defaultValue : value;
    }

    private static boolean resetPassword(
            TcUtil tcUtil,
            String username,
            String currentPassword,
            String newPassword) {
        try {
            return tcUtil.resetUserPassword(username, currentPassword, newPassword);
        } catch (Exception exception) {
            throw new IllegalStateException("Password change failed in shared session.", exception);
        }
    }
}
