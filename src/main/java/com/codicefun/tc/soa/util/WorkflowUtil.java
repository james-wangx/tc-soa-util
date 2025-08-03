package com.codicefun.tc.soa.util;

import com.codicefun.tc.soa.clientx.AppXSession;
import com.teamcenter.schemas.soa._2006_03.exceptions.ServiceException;
import com.teamcenter.services.strong.workflow.WorkflowService;
import com.teamcenter.services.strong.workflow._2007_06.Workflow.ReleaseStatusInput;
import com.teamcenter.services.strong.workflow._2007_06.Workflow.ReleaseStatusOption;
import com.teamcenter.services.strong.workflow._2007_06.Workflow.SetReleaseStatusResponse;
import com.teamcenter.services.strong.workflow._2014_10.Workflow.CreateWkfInput;
import com.teamcenter.services.strong.workflow._2014_10.Workflow.CreateWkfOutput;
import com.teamcenter.services.strong.workflow._2015_07.Workflow.CreateSignoffInfo;
import com.teamcenter.services.strong.workflow._2015_07.Workflow.CreateSignoffs;
import com.teamcenter.soa.client.Connection;
import com.teamcenter.soa.client.model.ModelObject;
import com.teamcenter.soa.client.model.strong.EPMTask;
import com.teamcenter.soa.client.model.strong.User;
import com.teamcenter.soa.client.model.strong.WorkspaceObject;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

/**
 * Workflow utility class
 * <p>
 * TODO: Add more operations: Replace and Rename
 */
@Slf4j
public class WorkflowUtil {

    private static final Connection connection = AppXSession.getConnection();
    private static final WorkflowService wfService = WorkflowService.getService(connection);

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
            SetReleaseStatusResponse response = wfService.setReleaseStatus(inputs);
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

    public static Optional<ModelObject> createWorkflow(String name, String desc, String template, User owner,
                                                       ModelObject[] attachments) {
        CreateWkfInput input = new CreateWkfInput();
        input.processName = name;
        input.processDescription = desc;
        input.processTemplate = template;
        input.workflowOwner = owner;
        input.responsibleParty = owner;
        input.attachments = attachments;
        input.attachmentRelationTypes = new String[]{"Fnd0EPMTarget"};
        try {
            CreateWkfOutput output = wfService.createWorkflow(input);
            if (ServiceUtil.catchPartialErrors(output.serviceData)) {
                return Optional.empty();
            }
            return Optional.ofNullable(output.workflowTask);
        } catch (ServiceException e) {
            log.error("Catch Exception: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    public static void addSignoff(EPMTask task) {
        CreateSignoffs[] signoffs = new CreateSignoffs[1];
        signoffs[0] = new CreateSignoffs();
        signoffs[0].signoffInfo = new CreateSignoffInfo[1];
        signoffs[0].task = task;
        CreateSignoffInfo[] infos = new CreateSignoffInfo[1];
        infos[0] = new CreateSignoffInfo();
    }

}
