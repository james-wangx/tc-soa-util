package com.codicefun.tc.soa.util;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class PreferenceManagementUtilTest {

    @Test
    void testGetValuesByName() {
        // TODO: implement testGetValuesByName
        Optional<String[]> result = PreferenceManagementUtil.getValuesByName(null);
        assertTrue(result.isPresent());
    }

    @Test
    void testGetValueByName() {
        // TODO: implement testGetValueByName
        Optional<String> result = PreferenceManagementUtil.getValueByName(null);
        assertTrue(result.isPresent());
    }

    @Test
    void testGetValueByNameAndPrefix() {
        // TODO: implement testGetValueByNameAndPrefix
        Optional<String> result = PreferenceManagementUtil.getValueByNameAndPrefix(null, null);
        assertTrue(result.isPresent());
    }

}
