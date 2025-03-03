package com.codicefun.tc.soa.util;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Preference management util test
 * <p>
 * TODO: Finish PreferenceManagementUtilTest
 */
class PreferenceManagementUtilTest {

    @Test
    void getValuesByName() {
        Optional<String[]> result = PreferenceManagementUtil.getValuesByName(null);
        assertTrue(result.isPresent());
    }

    @Test
    void getValueByName() {
        Optional<String> result = PreferenceManagementUtil.getValueByName(null);
        assertTrue(result.isPresent());
    }

    @Test
    void getValueByNameAndPrefix() {
        Optional<String> result = PreferenceManagementUtil.getValueByNameAndPrefix(null, null);
        assertTrue(result.isPresent());
    }

}
