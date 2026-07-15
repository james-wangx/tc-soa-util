package com.plm.tc.soa.util;

import com.teamcenter.services.strong.core._2006_03.DataManagement.ItemProperties;
import com.teamcenter.soa.client.model.ModelObject;
import com.teamcenter.soa.client.model.strong.Item;
import com.teamcenter.soa.client.model.strong.ItemRevision;
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
        ItemProperties itemProperties = new ItemProperties();
        itemProperties.type = "U2_Item";
        itemProperties.name = "tc-soa-util-test";
        Optional<Item> item = tcUtil.createItem(itemProperties, null, null);
    }

    @Test
    void deleteRelation() {

    }

    @Test
    void whereUsed() {
        ItemRevision revision = (ItemRevision) tcUtil.findMoByUid("uniFAAi1o0c12D").get();
        Optional<ModelObject[]> usedOpt = tcUtil.whereUsed(revision, 1);
        if (usedOpt.isPresent()) {
            ModelObject[] revs = usedOpt.get();
            for (ModelObject rev : revs) {
                String itemId = tcUtil.getPropStringValue(rev, "item_id").get();
                System.out.println("itemId = " + itemId);
            }
        }
    }

}
