package com.codicefun.tc.soa.util;

import com.codicefun.tc.soa.clientx.AppXSession;
import com.codicefun.tc.soa.exception.SoaUtilException;
import com.teamcenter.schemas.soa._2006_03.exceptions.ServiceException;
import com.teamcenter.services.strong.administration.PreferenceManagementService;
import com.teamcenter.services.strong.administration._2012_09.PreferenceManagement.CompletePreference;
import com.teamcenter.services.strong.administration._2012_09.PreferenceManagement.GetPreferencesResponse;
import com.teamcenter.soa.client.Connection;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

/**
 * Preference management util
 */
@Slf4j
public class PreferenceManagementUtil {

    private static final Connection connection = AppXSession.getConnection();
    private static final PreferenceManagementService pmService = PreferenceManagementService.getService(connection);

    /**
     * Get preference values by preference name
     *
     * @param prefName preference name
     * @return preference values
     */
    public static Optional<String[]> getValuesByName(String prefName) {
        try {
            pmService.refreshPreferences();
        } catch (ServiceException e) {
            log.error("Catch ServiceException: {}", e.getMessage(), e);
            return Optional.empty();
        }
        GetPreferencesResponse response = pmService.getPreferences(new String[]{prefName}, false);
        if (ServiceUtil.catchPartialErrors(response.data)) {
            return Optional.empty();
        }

        CompletePreference[] preferences = response.response;
        if (preferences == null || preferences.length == 0) {
            return Optional.empty();
        }

        return Optional.ofNullable(preferences[0].values.values);
    }

    /**
     * Get preference value(first value) by preference name
     *
     * @param prefName preference name
     * @return preference value(first value)
     */
    public static Optional<String> getValueByName(String prefName) {
        String[] preferences = getValuesByName(prefName).orElseThrow(
                () -> new SoaUtilException("Not found preferences by name: " + prefName));

        if (preferences == null || preferences.length == 0) {
            return Optional.empty();
        }

        return Optional.ofNullable(preferences[0]);
    }

    /**
     * Get preference value(first value) by preference name and prefix
     *
     * @param prefName preference name
     * @param prefix   prefix(lower case)
     * @return preference value(first value)
     */
    public static Optional<String> getValueByNameAndPrefix(String prefName, String prefix) {
        String[] preferences = getValuesByName(prefName).orElseThrow(
                () -> new SoaUtilException("Not found preferences by name: " + prefName));

        if (preferences == null) {
            return Optional.empty();
        }

        for (String preference : preferences) {
            if (preference.toLowerCase().startsWith(prefix)) {
                return Optional.of(preference);
            }
        }

        return Optional.empty();
    }

}
