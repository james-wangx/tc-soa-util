package com.codicefun.tc.soa.util;

import com.codicefun.tc.soa.exception.TestException;
import com.teamcenter.soa.client.model.ModelObject;
import com.teamcenter.soa.client.model.strong.Item;
import com.teamcenter.soa.client.model.strong.ItemRevision;
import com.teamcenter.soa.exceptions.NotLoadedException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Data management util test
 * <p>
 * TODO: Finish DataManagementUtilTest
 */
class DataManagementUtilTest {

    @BeforeAll
    static void init() {
        SessionUtil.login("http://192.168.80.101:8888/tc", "00001", "00001", "tc-soa-util-test");
    }

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

    @Test
    void getItemFromId_WithExistId_ShouldSameId() throws NotLoadedException {
        String itemId = "013921";
        Item item = DataManagementUtil.getItemFromId(itemId).orElseThrow(() -> new TestException("Not found item"));
        DataManagementUtil.getProperty(item, "item_id");
        assertEquals(itemId, item.get_item_id());
    }

    @Test
    void getItemFromId_WithNotExistId_ShouldNotFound() {
        Optional<Item> result = DataManagementUtil.getItemFromId("");
        assertFalse(result.isPresent());
    }

    @Test
    void setProperties_WithMultiProps_ShouldReturnTrue() {
        ItemRevision itemRevision = (ItemRevision) DataManagementUtil.findMoByUid("RYjdNMcao0c12D")
                                                                     .orElseThrow(() -> new TestException(
                                                                             "Not found item revision"));
        Map<String, String> propMap = new HashMap<>();
        propMap.put("object_name", "setPropertiesTestSuccess");
        propMap.put("object_desc", "test description");
        boolean result = DataManagementUtil.setProperties(itemRevision, propMap);
        assertTrue(result);
    }

    @Test
    void getLatestItemRevision() {

    }

}