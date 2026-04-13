package com.xcplm.tc.soa.util;

import com.teamcenter.services.strong.core._2006_03.DataManagement.ItemProperties;
import com.teamcenter.soa.client.model.ModelObject;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
        String queryName = "Item...";
        String[] queryEntries = new String[]{"名称", "类型"};
        String[] queryValues = new String[]{"Test", "CPGC-产品零部件"};
        // log.info("Execute query: {}, entries: {}, values: {}", queryName, queryEntries, queryValues);
        Optional<ModelObject> resOpt = tcUtil.queryOneObject(queryName, queryEntries, queryValues);
        if (resOpt.isPresent()) {
            System.out.println("Found object for object name Test: " + resOpt.get().getUid());
        } else {
            System.out.println("No object found for object name Test");
        }
    }

}
