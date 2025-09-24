package com.codicefun.tc.soa.util;

import com.codicefun.tc.soa.exception.TestException;
import com.teamcenter.soa.client.model.ModelObject;
import com.teamcenter.soa.client.model.strong.ItemRevision;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class StructureManagementUtilTest extends UtilTest {

    @Test
    void getPSBOMView() {
        ModelObject mo = DataManagementUtil.findMoByUid("Q0HAAASFZXeAPB")
                                           .orElseThrow(() -> new TestException("ItemRevision not found"));
        assertTrue(StructureManagementUtil.getPSBOMView((ItemRevision) mo).isPresent());
    }

    @Test
    void getBOMLine() {
        ModelObject mo = DataManagementUtil.findMoByUid("w0NAAASFZXeAPB")
                                           .orElseThrow(() -> new TestException("ItemRevision not found"));
        assertTrue(StructureManagementUtil.getBOM((ItemRevision) mo).isPresent());
    }

}
