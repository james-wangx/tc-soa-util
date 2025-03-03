package com.codicefun.tc.soa.util;

import com.teamcenter.soa.client.model.ModelObject;
import com.teamcenter.soa.client.model.strong.Item;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Data management util test
 * <p>
 * TODO: Finish DataManagementUtilTest
 */
class DataManagementUtilTest {

    @Test
    void refreshMo() {
        boolean result = DataManagementUtil.refreshMo(null);
        assertTrue(result);
    }

    @Test
    void getProperty() {
        boolean result = DataManagementUtil.getProperty(null, null);
        assertTrue(result);
    }

    @Test
    void findMoByUid() {
        Optional<ModelObject> result = DataManagementUtil.findMoByUid(null);
        assertTrue(result.isPresent());
    }

    @Test
    void getNamingRules() {
        Optional<String[]> result = DataManagementUtil.getNamingRules(null, null);
        assertTrue(result.isPresent());
    }

    @Test
    void getNextId() {
        Optional<String> result = DataManagementUtil.getNextId(null, null, null);
        assertTrue(result.isPresent());
    }

    @Test
    void createItem() {
        Optional<Item> result = DataManagementUtil.createItem(null, null, null, null, null);
        assertTrue(result.isPresent());
    }

}