package com.codicefun.tc.soa.util;

import com.codicefun.tc.soa.exception.TestException;
import com.teamcenter.services.strong.cad._2007_01.StructureManagement.CreateBOMWindowsOutput;
import com.teamcenter.soa.client.model.strong.BOMLine;
import com.teamcenter.soa.client.model.strong.ItemRevision;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StructureUtilTest extends UtilTest {

    @Test
    void addChild() {
        String uid1 = "wygdutszo0c12D"; // 顶层
        String uid2 = "g3oduauKo0c12D"; // 子 bom
        ItemRevision itemRevision = (ItemRevision) DataManagementUtil.findMoByUid(uid1)
                                                                     .orElseThrow(() -> new TestException(
                                                                             "Not found item revision by uid: " +
                                                                             uid1));
        ItemRevision childRevision = (ItemRevision) DataManagementUtil.findMoByUid(uid2)
                                                                      .orElseThrow(() -> new TestException(
                                                                              "Not found item revision by uid: " +
                                                                              uid2));
        CreateBOMWindowsOutput bom = StructureManagementUtil.getBOM(itemRevision)
                                                            .orElseThrow(
                                                                    () -> new TestException("Get bom failed"));
        BOMLine childLine = StructureUtil.addChild(bom.bomLine, childRevision)
                                         .orElseThrow(() -> new TestException("Add child failed"));
        boolean isSaved = StructureManagementUtil.saveBOMWindow(bom.bomWindow);
        assertNotNull(childLine);
        assertTrue(isSaved);
    }

}
