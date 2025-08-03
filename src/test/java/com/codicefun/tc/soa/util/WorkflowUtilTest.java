package com.codicefun.tc.soa.util;

import com.codicefun.tc.soa.exception.TestException;
import com.teamcenter.soa.client.model.ModelObject;
import com.teamcenter.soa.client.model.strong.ItemRevision;
import com.teamcenter.soa.client.model.strong.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Workflow util test
 */
class WorkflowUtilTest extends UtilTest {

    static ItemRevision obj;

    @Test
    void appendReleaseStatus_WithReleaseStatus_ShouldReturnTrue() {
        obj = (ItemRevision) DataManagementUtil.findMoByUid("QTodNMcao0c12D")
                                               .orElseThrow(() -> new TestException("Not found model object"));
        boolean result = WorkflowUtil.appendReleaseStatus(obj, "TCM Released");
        Assertions.assertTrue(result);
    }

    @Test
    void appendReleaseStatus_WithObsoleteStatus_ShouldReturnTrue() {
        obj = (ItemRevision) DataManagementUtil.findMoByUid("QTodNMcao0c12D")
                                               .orElseThrow(() -> new TestException("Not found model object"));
        boolean result = WorkflowUtil.appendReleaseStatus(obj, "Obsolete");
        Assertions.assertTrue(result);
    }

    @Test
    void deleteReleaseStatus_WithReleaseStatus_ShouldReturnTrue() {
        obj = (ItemRevision) DataManagementUtil.findMoByUid("QTodNMcao0c12D")
                                               .orElseThrow(() -> new TestException("Not found model object"));
        boolean result = WorkflowUtil.deleteReleaseStatus(obj, "TCM Released");
        Assertions.assertTrue(result);
    }

    @Test
    void deleteReleaseStatus_WithNullStatus_ShouldReturnTrue() {
        obj = (ItemRevision) DataManagementUtil.findMoByUid("QTodNMcao0c12D")
                                               .orElseThrow(() -> new TestException("Not found model object"));
        boolean result = WorkflowUtil.deleteReleaseStatus(obj, null);
        Assertions.assertTrue(result);
    }

    @Test
    void deleteAllReleaseStatus_WithExistObj_ShouldReturnTrue() {
        boolean result = WorkflowUtil.deleteAllReleaseStatus(obj);
        Assertions.assertTrue(result);
    }

    @Test
    void createWorkflow() {
        ModelObject rev = DataManagementUtil.findMoByUid("Arlh2twGo0c12D")
                                            .orElseThrow(() -> new TestException("Not found revision"));
        ModelObject owner = ModelObjectUtil.getPropObjValue(rev, "owning_user")
                                           .orElseThrow(() -> new TestException("Not found owning user"));
        ModelObject workflow = WorkflowUtil.createWorkflow("001-测试流程", "", "001-测试流程", (User) owner,
                                                           new ModelObject[]{rev})
                                           .orElseThrow(() -> new TestException("Failed in createWorkflow"));
        System.out.println("workflow = " + workflow);
    }

}
