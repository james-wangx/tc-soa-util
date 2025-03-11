package com.codicefun.tc.soa.util;

import com.codicefun.tc.soa.exception.TestException;
import com.teamcenter.soa.client.model.strong.ItemRevision;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Workflow util test
 */
class WorkflowUtilTest {

    static ItemRevision obj;

    @BeforeAll
    static void init() {
        SessionUtil.login("http://192.168.80.101:8888/tc", "00001", "00001", "tc-soa-util-test");
        obj = (ItemRevision) DataManagementUtil.findMoByUid("QTodNMcao0c12D")
                                               .orElseThrow(
                                                       () -> new TestException("Not found model object"));
    }

    @Test
    void appendReleaseStatus_WithReleaseStatus_ShouldReturnTrue() {
        boolean result = WorkflowUtil.appendReleaseStatus(obj, "TCM Released");
        Assertions.assertTrue(result);
    }

    @Test
    void appendReleaseStatus_WithObsoleteStatus_ShouldReturnTrue() {
        boolean result = WorkflowUtil.appendReleaseStatus(obj, "Obsolete");
        Assertions.assertTrue(result);
    }

    @Test
    void deleteReleaseStatus_WithReleaseStatus_ShouldReturnTrue() {
        boolean result = WorkflowUtil.deleteReleaseStatus(obj, "TCM Released");
        Assertions.assertTrue(result);
    }

    @Test
    void deleteReleaseStatus_WithNullStatus_ShouldReturnTrue() {
        boolean result = WorkflowUtil.deleteReleaseStatus(obj, null);
        Assertions.assertTrue(result);
    }

    @Test
    void deleteAllReleaseStatus_WithExistObj_ShouldReturnTrue() {
        boolean result = WorkflowUtil.deleteAllReleaseStatus(obj);
        Assertions.assertTrue(result);
    }

}
