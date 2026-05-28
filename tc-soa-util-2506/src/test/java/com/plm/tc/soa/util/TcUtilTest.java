package com.plm.tc.soa.util;

import com.plm.tc.soa.exception.TestException;
import com.teamcenter.services.strong.core._2006_03.DataManagement.ItemProperties;
import com.teamcenter.soa.client.model.strong.BOMLine;
import com.teamcenter.soa.client.model.strong.ItemRevision;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

class TcUtilTest extends UtilTest {

    @Test
    void downloadFile() {
    }

    @Test
    void createItems() {
        ItemProperties itemProperties = new ItemProperties();
        itemProperties.type = "U2_Part";
        itemProperties.name = "tc-soa-util-test";
        // ExtendedAttributes[] extendedAttributes = new ExtendedAttributes[1];
        // extendedAttributes[0] = new ExtendedAttributes();
        // extendedAttributes[0].objectType = "U2_PartRevision";
        Map<String, Object> attrMap = new HashMap<>();
        attrMap.put("u2_remarks", "test");
        // extendedAttributes[0].attributes = attrMap;
        // itemProperties.extendedAttributes = extendedAttributes;
        // ItemRevision itemRevision = tcUtil.createItem(itemProperties, null, null).get();
        // tcUtil.setProperties(itemRevision, attrMap);
    }

    @Test
    void test() {
        ItemRevision object = (ItemRevision) tcUtil.findMoByUid("AYFAQAHdZv6QdD").get();
        BOMLine bomLine = tcUtil.getBOMLine(object).get();
        tcUtil.getProperties(bomLine, new String[]{"bl_rev_item_id", "bl_rev_item_revision_id", "A2precursorCt"});
        String itemId = tcUtil.getPropStringValue(bomLine, "bl_rev_item_id")
                              .orElseThrow(() -> new TestException("未获取到itemId"));
        String test = tcUtil.getPropStringValue(bomLine, "A2precursorCt")
                            .orElseThrow(() -> new TestException("未获取到A2precursorCt"));
        System.out.println("itemId = " + itemId);
        System.out.println("test = " + test);
        System.out.println("bomLine = " + bomLine.getUid());
    }

    @Test
    void loginTest() {
        boolean success = tcUtil.login("james", "james", "zh_CN", "tc-soa-util");
        Assertions.assertTrue(success);
    }

}
