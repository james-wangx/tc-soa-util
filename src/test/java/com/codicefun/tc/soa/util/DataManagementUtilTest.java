package com.codicefun.tc.soa.util;

import com.codicefun.tc.soa.exception.TestException;
import com.teamcenter.soa.client.model.strong.Folder;
import com.teamcenter.soa.client.model.strong.Item;
import com.teamcenter.soa.client.model.strong.ItemRevision;
import com.teamcenter.soa.exceptions.NotLoadedException;
import org.junit.jupiter.api.AfterAll;
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
class DataManagementUtilTest extends UtilTest {

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

    @AfterAll
    static void destroy() {
        // SessionUtil.logout();
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

    @Test
    void findMoByUid() {
        for (int i = 0; i < 5; i++) {
            ItemRevision itemRevision = (ItemRevision) DataManagementUtil.findMoByUid("QLqdt490o0c12D")
                                                                         .orElseThrow(() -> new TestException(
                                                                                 "Not found item revision"));
            DataManagementUtil.refreshMo(itemRevision);
            String desc = ModelObjectUtil.getPropStringValue(itemRevision, "object_desc")
                                         .orElseThrow(() -> new TestException("Not found property"));

            System.out.println("desc = " + desc);
            connection.getClientDataModel().removeAllObjects();
        }
    }

    @Test
    void createFolder() {
        Folder homeFolder = SessionUtil.getHomeFolder()
                                       .orElseThrow(() -> new TestException("Not found home folder"));
        Optional<Folder> folderOptional = DataManagementUtil.createFolder(homeFolder, "createFolderTest");

        assertTrue(folderOptional.isPresent());
    }

    @Test
    void createObject() {
    }

    @Test
    void copy() {
    }

    @Test
    void deleteObject() {
        Item item = (Item) DataManagementUtil.findMoByUid("gKghAytvo0c12D")
                                             .orElseThrow(() -> new TestException("Not found item"));
        boolean result = DataManagementUtil.deleteObject(item);

        assertTrue(result);
    }

    @Test
    void getNextRevId() {
        ItemRevision rev = (ItemRevision) DataManagementUtil.findMoByUid("w6mhmRp0o0c12D")
                                                            .orElseThrow(() -> new TestException("Not found revision"));
        String newRevId = DataManagementUtil.getNextRevId(rev)
                                            .orElseThrow(() -> new TestException("Not found revision"));
        System.out.println("newRevId = " + newRevId);
    }

    @Test
    void revise() {
        ItemRevision rev = (ItemRevision) DataManagementUtil.findMoByUid("w6mhmRp0o0c12D")
                                                            .orElseThrow(() -> new TestException("Not found revision"));
        ItemRevision newRev = DataManagementUtil.revise(rev)
                                                .orElseThrow(() -> new TestException("Revise rev failed"));
        System.out.println("newRev = " + newRev);
    }

}
