package com.codicefun.tc.soa.util;

import com.codicefun.tc.soa.clientx.AppXSession;
import com.teamcenter.schemas.soa._2006_03.exceptions.ServiceException;
import com.teamcenter.services.strong.workflow.WorkflowService;
import com.teamcenter.services.strong.workflow._2007_06.Workflow.ReleaseStatusInput;
import com.teamcenter.services.strong.workflow._2007_06.Workflow.ReleaseStatusOption;
import com.teamcenter.services.strong.workflow._2007_06.Workflow.SetReleaseStatusResponse;
import com.teamcenter.soa.client.Connection;
import com.teamcenter.soa.client.model.strong.WorkspaceObject;
import lombok.extern.slf4j.Slf4j;

/**
 * Workflow utility class
 * <p>
 * TODO: Add more operations: Replace and Rename
 */
@Slf4j
public class WorkflowUtil {

    private static final Connection connection = AppXSession.getConnection();
    private static final WorkflowService workflowService = WorkflowService.getService(connection);

    /**
     * Set release status
     *
     * @param obj       the workspace object
     * @param operation the operation
     * @param newStatus the new status
     * @param oldStatus the old status
     * @return true if successful, otherwise false
     */
    private static boolean setReleaseStatus(WorkspaceObject obj, String operation, String newStatus, String oldStatus) {
        try {
            ReleaseStatusInput[] inputs = new ReleaseStatusInput[1];
            inputs[0] = new ReleaseStatusInput();
            inputs[0].objects = new WorkspaceObject[]{obj};
            inputs[0].operations = new ReleaseStatusOption[1];
            inputs[0].operations[0] = new ReleaseStatusOption();
            inputs[0].operations[0].operation = operation;
            inputs[0].operations[0].newReleaseStatusTypeName = newStatus;
            inputs[0].operations[0].existingreleaseStatusTypeName = oldStatus;
            SetReleaseStatusResponse response = workflowService.setReleaseStatus(inputs);
            return !ServiceUtil.catchPartialErrors(response.serviceData);
        } catch (ServiceException e) {
            log.error("Catch Exception: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Append release status
     *
     * @param obj    the workspace object
     * @param status the status
     * @return true if successful, otherwise false
     */
    public static boolean appendReleaseStatus(WorkspaceObject obj, String status) {
        return setReleaseStatus(obj, "Append", status, null);
    }

    /**
     * Delete release status
     *
     * @param obj    the workspace object
     * @param status the status
     * @return true if successful, otherwise false
     */
    public static boolean deleteReleaseStatus(WorkspaceObject obj, String status) {
        return setReleaseStatus(obj, "Delete", status, null);
    }

    /**
     * Delete all release status
     *
     * @param obj the workspace object
     * @return true if successful, otherwise false
     */
    public static boolean deleteAllReleaseStatus(WorkspaceObject obj) {
        return deleteReleaseStatus(obj, null);
    }

}
