package com.xcplm.tc.soa.util;

import com.teamcenter.schemas.soa._2006_03.exceptions.ServiceException;
import com.teamcenter.services.internal.strong.structuremanagement._2011_06.Structure.CreateOrSaveAsPSBOMViewRevisionInput;
import com.teamcenter.services.internal.strong.structuremanagement._2011_06.Structure.CreateOrSaveAsPSBOMViewRevisionResponse;
import com.teamcenter.services.internal.strong.structuremanagement._2011_06.Structure.GetAllAvailableViewTypesInput;
import com.teamcenter.services.internal.strong.structuremanagement._2011_06.Structure.GetAvailableViewTypesResponse;
import com.teamcenter.services.strong.administration.PreferenceManagementService;
import com.teamcenter.services.strong.administration._2012_09.PreferenceManagement.CompletePreference;
import com.teamcenter.services.strong.administration._2012_09.PreferenceManagement.GetPreferencesResponse;
import com.teamcenter.services.strong.cad.StructureManagementService;
import com.teamcenter.services.strong.cad._2007_01.StructureManagement;
import com.teamcenter.services.strong.cad._2007_01.StructureManagement.*;
import com.teamcenter.services.strong.cad._2008_06.StructureManagement.SaveBOMWindowsResponse;
import com.teamcenter.services.strong.cad._2019_06.StructureManagement.CreateWindowsInfo3;
import com.teamcenter.services.strong.core.DataManagementService;
import com.teamcenter.services.strong.core._2006_03.DataManagement.*;
import com.teamcenter.services.strong.core._2007_01.DataManagement.GetItemFromIdPref;
import com.teamcenter.services.strong.core._2008_06.DataManagement.*;
import com.teamcenter.services.strong.core._2009_10.DataManagement.GetItemFromAttributeInfo;
import com.teamcenter.services.strong.core._2009_10.DataManagement.GetItemFromAttributeResponse;
import com.teamcenter.services.strong.core._2010_09.DataManagement;
import com.teamcenter.services.strong.core._2010_09.DataManagement.NameValueStruct1;
import com.teamcenter.services.strong.core._2010_09.DataManagement.PropInfo;
import com.teamcenter.services.strong.core._2010_09.DataManagement.SetPropertyResponse;
import com.teamcenter.services.strong.core._2013_05.DataManagement.GenerateNextValuesIn;
import com.teamcenter.services.strong.core._2013_05.DataManagement.GenerateNextValuesResponse;
import com.teamcenter.services.strong.core._2015_07.DataManagement.CreateIn2;
import com.teamcenter.services.strong.importexport.FileImportExportService;
import com.teamcenter.services.strong.importexport._2011_06.FileImportExport.ExportToApplicationInputData2;
import com.teamcenter.services.strong.importexport._2011_06.FileImportExport.ExportToApplicationResponse1;
import com.teamcenter.services.strong.query.SavedQueryService;
import com.teamcenter.services.strong.query._2006_03.SavedQuery.GetSavedQueriesResponse;
import com.teamcenter.services.strong.query._2006_03.SavedQuery.SavedQueryObject;
import com.teamcenter.services.strong.query._2007_09.SavedQuery.SavedQueriesResponse;
import com.teamcenter.services.strong.query._2008_06.SavedQuery.QueryInput;
import com.teamcenter.services.strong.structuremanagement.StructureService;
import com.teamcenter.services.strong.structuremanagement._2012_09.Structure.AddInformation;
import com.teamcenter.services.strong.structuremanagement._2012_09.Structure.AddParam;
import com.teamcenter.services.strong.structuremanagement._2012_09.Structure.AddResponse;
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
import com.teamcenter.soa.client.model.Property;
import com.teamcenter.soa.client.model.ServiceData;
import com.teamcenter.soa.client.model.strong.*;
import com.teamcenter.soa.exceptions.NotLoadedException;
import com.xcplm.tc.soa.exception.SoaUtilException;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.*;
import java.util.Map.Entry;

@Slf4j
@SuppressWarnings({"unused", "OptionalGetWithoutIsPresent", "UnusedReturnValue", "SameParameterValue",
                   "FieldCanBeLocal"})
public class TcUtil {

    // private final FileManagementUtility fmUtil;
    private final DataManagementService dmService;
    private final WorkflowService wfService;
    private final StructureManagementService smService;
    private final SavedQueryService sqService;
    private final PreferenceManagementService pmService;
    private final FileImportExportService fieService;
    private final StructureService structureService;

    /**
     * Internal Structure Service
     */
    private final com.teamcenter.services.internal.strong.structuremanagement.StructureService isService;
    /**
     * Transient file directory, read from TC Preference: "Transient_Volume_RootDir"
     */
    // private final String TRANSIENT_DIRECTORY = getValueByNameAndPrefix("Transient_Volume_RootDir",
    //                                                                    SystemUtil.isWindows() ? "c:" : "/")
    //         .orElseThrow(() -> new SoaUtilException("Not found Transient_Volume_RootDir"));

    private final String TRANSIENT_DIRECTORY = ";";
    /**
     * Transient file separator: ";\"
     * <p>
     * TODO: separator in linux?
     */
    private final String TRANSIENT_SEPARATOR = "%3b%5c";

    public TcUtil(Connection connection) {
        // this.fmUtil = new FileManagementUtility(connection);
        this.dmService = DataManagementService.getService(connection);
        this.wfService = WorkflowService.getService(connection);
        this.smService = StructureManagementService.getService(connection);
        this.sqService = SavedQueryService.getService(connection);
        this.pmService = PreferenceManagementService.getService(connection);
        this.fieService = FileImportExportService.getService(connection);
        this.structureService = StructureService.getService(connection);
        this.isService = com.teamcenter.services.internal.strong.structuremanagement.StructureService.getService(
                connection);
    }

    /**
     * Add child item revision to specific bom line
     *
     * @param bomLine      target bom line
     * @param itemRevision the child revision to be added
     * @return an Optional containing the added bom line if successful, otherwise an empty Optional
     */
    public Optional<BOMLine> addChild(BOMLine bomLine, ItemRevision itemRevision) {
        AddParam[] addParams = new AddParam[1];
        addParams[0] = new AddParam();
        addParams[0].parent = bomLine;
        addParams[0].toBeAdded = new AddInformation[1];
        addParams[0].toBeAdded[0] = new AddInformation();
        addParams[0].toBeAdded[0].itemRev = itemRevision;
        AddResponse response = structureService.add(addParams);
        if (ServiceUtil.catchPartialErrors(response.serviceData) ||
            response.addedLines == null ||
            response.addedLines.length == 0) {
            return Optional.empty();
        }

        return Optional.ofNullable(response.addedLines[0]);
    }

    /**
     * Not impl
     */
    @SuppressWarnings("MismatchedReadAndWriteOfArray")
    public void addSignoff(EPMTask task) {
        CreateSignoffs[] signoffs = new CreateSignoffs[1];
        signoffs[0] = new CreateSignoffs();
        signoffs[0].signoffInfo = new CreateSignoffInfo[1];
        signoffs[0].task = task;
        CreateSignoffInfo[] infos = new CreateSignoffInfo[1];
        infos[0] = new CreateSignoffInfo();

        throw new SoaUtilException("Not Implemented");
    }

    /**
     * Append release status
     *
     * @param obj    the workspace object
     * @param status the status
     * @return true if successful, otherwise false
     */
    public boolean appendReleaseStatus(WorkspaceObject obj, String status) {
        return setReleaseStatus(obj, "Append", status, null);
    }

    /**
     * Change object's ownership to specific user and group
     *
     * @param obj   the object to be change
     * @param owner target user
     * @param group target group
     * @return true if success, otherwise false
     */
    public boolean changeOwnership(ModelObject obj, User owner, Group group) {
        ObjectOwner[] owners = new ObjectOwner[1];
        owners[0] = new ObjectOwner();
        owners[0].object = obj;
        owners[0].owner = owner;
        owners[0].group = group;
        ServiceData serviceData = dmService.changeOwnership(owners);

        return !ServiceUtil.catchPartialErrors(serviceData);
    }

    /**
     * Copy a model object to a folder
     *
     * @param from the model object
     * @param to   the folder
     * @return success or failed
     */
    public boolean copy(ModelObject from, Folder to) {
        Relationship[] inputs = new Relationship[1];
        inputs[0] = new Relationship();
        inputs[0].primaryObject = to;
        inputs[0].secondaryObject = from;
        inputs[0].relationType = "contents";
        CreateRelationsResponse response = dmService.createRelations(inputs);

        return !ServiceUtil.catchPartialErrors(response.serviceData);
    }

    /**
     * Create folder by parent folder object and new folder name
     *
     * @param parent parent folder object
     * @param name   new folder name
     * @return new folder
     */
    public Optional<Folder> createFolder(Folder parent, String name) {
        CreateFolderInput[] inputs = new CreateFolderInput[1];
        inputs[0] = new CreateFolderInput();
        inputs[0].name = name;
        CreateFoldersResponse response = dmService.createFolders(inputs, parent, "");

        if (ServiceUtil.catchPartialErrors(response.serviceData) || response.output == null ||
            response.output.length == 0) {
            return Optional.empty();
        }

        return Optional.ofNullable(response.output[0].folder);
    }

    /**
     * Creates an item with the specified properties and adds it to the given container.
     *
     * @param itemProperties A list of properties to create new Item objects
     * @param container      The container object to which all the items will be related to via the input relation type, optional
     * @param relationType   The relation type that will be used to relate the newly created Items to the container, optional.
     * @return an Optional containing the created item if successful, otherwise an empty Optional
     */
    public Optional<ItemRevision> createItem(ItemProperties itemProperties, ModelObject container,
                                             String relationType) {
        CreateItemsResponse response = dmService.createItems(new ItemProperties[]{itemProperties}, container,
                                                             relationType);
        if (ServiceUtil.catchPartialErrors(response.serviceData) || response.output == null ||
            response.output.length == 0) {
            return Optional.empty();
        }

        return Optional.ofNullable(response.output[0].itemRev);
    }

    /**
     * Create object and relate to parent object
     *
     * @param parent  the parent object to be related
     * @param type    the new object type
     * @param propMap the property map
     * @return the new object
     */
    public Optional<ModelObject> createObject(ModelObject parent, String type, Map<String, String[]> propMap) {
        CreateIn2[] inputs = new CreateIn2[1];
        inputs[0] = new CreateIn2();
        inputs[0].targetObject = parent;
        inputs[0].createData.boName = type;
        inputs[0].createData.propertyNameValues = propMap;
        CreateResponse response = dmService.createRelateAndSubmitObjects2(inputs);
        if (ServiceUtil.catchPartialErrors(response.serviceData) || response.output == null ||
            response.output.length == 0 || response.output[0].objects == null ||
            response.output[0].objects.length == 0) {
            return Optional.empty();
        }

        return Optional.ofNullable(response.output[0].objects[0]);
    }

    /**
     * Create a PSBOMViewRevision for the given ItemRevision.
     *
     * @param itemRevision The ItemRevision to create the PSBOMViewRevision for.
     * @return An Optional containing the created PSBOMViewRevision, or an empty Optional if creation failed.
     */
    public Optional<PSBOMViewRevision> createPSBVR(ItemRevision itemRevision) {
        return createPSBVR(itemRevision, null, false);
    }

    /**
     * Create a PSBOMViewRevision for the given ItemRevision with specified view type and precision.
     *
     * @param itemRevision The ItemRevision to create the PSBOMViewRevision for.
     * @param viewTypeName The name of the PSViewType to use. If null or empty, the default view type will be used.
     * @param isPrevise    Whether to create a precise PSBOMViewRevision.
     * @return An Optional containing the created PSBOMViewRevision, or an empty Optional if creation failed.
     */
    public Optional<PSBOMViewRevision> createPSBVR(ItemRevision itemRevision, String viewTypeName,
                                                   boolean isPrevise) {
        CreateOrSaveAsPSBOMViewRevisionInput[] inputs = new CreateOrSaveAsPSBOMViewRevisionInput[1];
        inputs[0] = new CreateOrSaveAsPSBOMViewRevisionInput();
        inputs[0].itemObject = (Item) getPropObjValue(itemRevision, "items_tag").get();
        inputs[0].itemRevObj = itemRevision;
        if (!StringUtil.isEmpty(viewTypeName)) {
            inputs[0].viewTypeTag = getPSVT(itemRevision, viewTypeName).orElseThrow(
                    () -> new SoaUtilException("Not found PSViewType by name: " + viewTypeName));
        }
        inputs[0].isPrecise = isPrevise;

        CreateOrSaveAsPSBOMViewRevisionResponse response = isService.createOrSavePSBOMViewRevision(inputs);
        if (ServiceUtil.catchPartialErrors(response.serviceData) ||
            response.psBVROutputs == null ||
            response.psBVROutputs.length == 0) {
            return Optional.empty();
        }

        return Optional.ofNullable(response.psBVROutputs[0].bvrTag);
    }

    /**
     * Create workflow with specific name, description, template, owner and attachments
     *
     * @param name        workflow name
     * @param desc        workflow description
     * @param template    workflow template
     * @param owner       workflow owner
     * @param attachments workflow attachments
     * @return an Optional containing the created workflow task if successful, otherwise an empty Optional
     */
    public Optional<ModelObject> createWorkflow(String name, String desc, String template, User owner,
                                                ModelObject[] attachments) {
        CreateWkfInput input = new CreateWkfInput();
        input.processName = name;
        input.processDescription = desc;
        input.processTemplate = template;
        input.workflowOwner = owner;
        input.responsibleParty = owner;
        input.attachments = attachments;
        String[] attachmentRelationTypes = new String[attachments.length];
        Arrays.fill(attachmentRelationTypes, "Fnd0EPMTarget");
        input.attachmentRelationTypes = attachmentRelationTypes;
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

    /**
     * Delete all release status
     *
     * @param obj the workspace object
     * @return true if successful, otherwise false
     */
    public boolean deleteAllReleaseStatus(WorkspaceObject obj) {
        return deleteReleaseStatus(obj, null);
    }

    /**
     * Delete a model object
     *
     * @param obj the model object
     * @return success or failed
     */
    public boolean deleteObject(ModelObject obj) {
        ServiceData serviceData = dmService.deleteObjects(new ModelObject[]{obj});

        return !ServiceUtil.catchPartialErrors(serviceData);
    }

    /**
     * Delete release status
     *
     * @param obj    the workspace object
     * @param status the status
     * @return true if successful, otherwise false
     */
    public boolean deleteReleaseStatus(WorkspaceObject obj, String status) {
        return setReleaseStatus(obj, "Delete", status, null);
    }

    /**
     * Export bom to excel by attributes
     *
     * @param bomLine The bom line to be exported
     * @param attrs   The bom line attributes to be exported
     * @return Excel file path
     */
    public Optional<String> exportBom2ExcelByAttrs(BOMLine bomLine, String[] attrs) {
        ExportToApplicationInputData2 inputData = new ExportToApplicationInputData2();
        inputData.objectsToExport = new ModelObject[]{bomLine};
        inputData.attributesToExport = attrs;
        inputData.applicationFormat = "MSExcel";

        return exportBom2Excel(inputData);
    }

    /**
     * Export bom to excel by template id
     *
     * @param bomLine    The bom line to be exported
     * @param templateId The template id used to export
     * @return Excel file path
     */
    public Optional<String> exportBom2ExcelByTemplateId(BOMLine bomLine, String templateId) {
        ExportToApplicationInputData2 inputData = new ExportToApplicationInputData2();
        inputData.objectsToExport = new ModelObject[]{bomLine};
        inputData.applicationFormat = "MSExcel";
        inputData.templateId = templateId;
        inputData.templateType = "ExcelTemplate";

        return exportBom2Excel(inputData);
    }

    /**
     * Find model object by uid
     *
     * @param uid target model object's uid
     * @return the result model object
     */
    public Optional<ModelObject> findMoByUid(String uid) {
        ServiceData serviceData = dmService.loadObjects(new String[]{uid});

        if (ServiceUtil.catchPartialErrors(serviceData) || serviceData.sizeOfPlainObjects() == 0) {
            return Optional.empty();
        }

        return Optional.ofNullable(serviceData.getPlainObject(serviceData.sizeOfPlainObjects() - 1));
    }

    /**
     * Find model objects by uids
     *
     * @param uids target model objects' uids
     * @return the result model objects
     */
    public Optional<ModelObject[]> findMosByUids(String[] uids) {
        ServiceData serviceData = dmService.loadObjects(uids);

        if (ServiceUtil.catchPartialErrors(serviceData) || serviceData.sizeOfPlainObjects() == 0) {
            return Optional.empty();
        }

        ModelObject[] mos = new ModelObject[serviceData.sizeOfPlainObjects()];
        for (int i = 0; i < serviceData.sizeOfPlainObjects(); i++) {
            mos[i] = serviceData.getPlainObject(i);
        }

        return Optional.of(mos);
    }

    /**
     * Get absolute occurrence context property list
     *
     * @param bomLine the bom line
     * @return the absolute occurrence context property list, if the property is not present, return an empty Optional
     */
    public Optional<List<String>> getAbsOccCxtPropList(BOMLine bomLine) {
        Optional<String> result = getPropStringValue(bomLine, "bl_properties_in_context");

        return result.map(s -> Arrays.asList(s.split(",")));
    }

    /**
     * Get bom by specific item revision
     *
     * @param itemRevision target item revision
     * @return an Optional containing the BOM if successful, otherwise an empty Optional
     */
    public Optional<CreateBOMWindowsOutput> getBOM(ItemRevision itemRevision) {
        CreateWindowsInfo3[] inputs = new CreateWindowsInfo3[1];
        inputs[0] = new CreateWindowsInfo3();
        getPSBOMView(itemRevision).ifPresent(psbomView -> inputs[0].bomView = psbomView);
        inputs[0].itemRev = itemRevision;
        CreateBOMWindowsResponse response = smService.createOrReConfigureBOMWindows(inputs);
        if (ServiceUtil.catchPartialErrors(response.serviceData) ||
            response.output == null ||
            response.output.length == 0) {
            return Optional.empty();
        }

        return Optional.ofNullable(response.output[0]);
    }

    /**
     * Send rev to structure manager and return the top line
     */
    public Optional<BOMLine> getBOMLine(ItemRevision itemRevision) {
        return getBOM(itemRevision).map(bom -> bom.bomLine);
    }

    /**
     * Get bom line by item revision and rev rule
     *
     * @param itemRevision the item revision
     * @param revRule      the rev rule
     * @return an Optional containing the BOM line if successful, otherwise an empty Optional
     */
    public Optional<BOMLine> getBOMLine(ItemRevision itemRevision, String revRule) {
        return getBOM(itemRevision, revRule).map(bom -> bom.bomLine);
    }

    /**
     * Get bom window by item revision
     *
     * @param itemRevision the item revision
     * @return an Optional containing the BOM window if successful, otherwise an empty Optional
     */
    public Optional<BOMWindow> getBOMWindow(ItemRevision itemRevision) {
        return getBOM(itemRevision).map(bom -> bom.bomWindow);
    }

    /**
     * Get bom window by item revision and rev rule
     *
     * @param itemRevision the item revision
     * @param revRule      the rev rule
     * @return an Optional containing the BOM window if successful, otherwise an empty Optional
     */
    public Optional<BOMWindow> getBOMWindow(ItemRevision itemRevision, String revRule) {
        return getBOM(itemRevision, revRule).map(bom -> bom.bomWindow);
    }

    /**
     * Get children bom line
     *
     * @param bomLine the parent bom line
     * @return an Optional containing the children bom lines if successful, otherwise an empty Optional
     */
    public Optional<ExpandPSData[]> getChildren(BOMLine bomLine) {
        ExpandPSOneLevelInfo expendInfo = new ExpandPSOneLevelInfo();
        expendInfo.parentBomLines = new BOMLine[]{bomLine};
        expendInfo.excludeFilter = "None";
        ExpandPSOneLevelPref expandPref = new ExpandPSOneLevelPref();
        expandPref.expItemRev = false;
        ExpandPSOneLevelOutput[] expandOutputs = smService.expandPSOneLevel(expendInfo, expandPref).output;

        if (expandOutputs.length == 0 ||
            expandOutputs[0].children == null ||
            expandOutputs[0].children.length == 0) {
            return Optional.empty();
        }

        return Optional.of(expandOutputs[0].children);
    }

    /**
     * Get item from id
     *
     * @param itemId the item id
     * @return the item
     */
    public Optional<Item> getItemFromId(String itemId) {
        GetItemFromAttributeInfo[] infos = new GetItemFromAttributeInfo[1];
        infos[0] = new GetItemFromAttributeInfo();
        infos[0].itemAttributes.put("item_id", itemId);
        GetItemFromIdPref pref = new GetItemFromIdPref();
        GetItemFromAttributeResponse response = dmService.getItemFromAttribute(infos, -1, pref);
        if (ServiceUtil.catchPartialErrors(response.serviceData) || response.output == null ||
            response.output.length == 0) {
            return Optional.empty();
        }

        return Optional.ofNullable(response.output[0].item);
    }

    /**
     * Get latest item revision
     *
     * @param item the item
     * @return the latest item revision
     */
    public Optional<ItemRevision> getLatestItemRevision(Item item) {
        ModelObject[] revisions = getPropArrayValue(item, "revision_list").orElse(new ModelObject[0]);
        if (revisions.length == 0) {
            return Optional.empty();
        }

        return Optional.ofNullable((ItemRevision) revisions[revisions.length - 1]);
    }

    /**
     * Get latest item revision
     *
     * @param rev the item revision
     * @return the latest item revision
     */
    public Optional<ItemRevision> getLatestItemRevision(ItemRevision rev) {
        ModelObject item = getPropObjValue(rev, "items_tag").orElseThrow(
                () -> new SoaUtilException("items_tag is not present"));

        return getLatestItemRevision((Item) item);
    }

    /**
     * Get naming rules
     *
     * @param typeName the type name
     * @param propName the property name
     * @return the naming rules
     */
    public Optional<String[]> getNamingRules(String typeName, String propName) {
        NRAttachInfo nrAttachInfo = new NRAttachInfo();
        nrAttachInfo.typeName = typeName;
        nrAttachInfo.propName = propName;
        GetNRPatternsWithCountersResponse response = dmService.getNRPatternsWithCounters(
                new NRAttachInfo[]{nrAttachInfo});
        if (ServiceUtil.catchPartialErrors(response.serviceData) || response.patterns == null ||
            response.patterns.length == 0) {
            return Optional.empty();
        }

        return Optional.ofNullable(response.patterns[0].patternStrings);
    }

    /**
     * Get next id
     *
     * @param typeName the type name
     * @param propName the property name
     * @param pattern  the pattern
     * @return the next id
     */
    public Optional<String> getNextId(String typeName, String propName, String pattern) {
        InfoForNextId infoForNextId = new InfoForNextId();
        infoForNextId.pattern = pattern;
        infoForNextId.propName = propName;
        infoForNextId.typeName = typeName;
        GetNextIdsResponse response = dmService.getNextIds(new InfoForNextId[]{infoForNextId});
        if (ServiceUtil.catchPartialErrors(response.serviceData) || response.nextIds == null ||
            response.nextIds.length == 0) {
            return Optional.empty();
        }

        return Optional.ofNullable(response.nextIds[0]);
    }

    /**
     * Get next revision id for specific item revision
     *
     * @param rev the item revision
     * @return the next revision id, if failed to get, return an empty Optional
     */
    public Optional<String> getNextRevId(ItemRevision rev) {
        GenerateNextValuesIn[] ins = new GenerateNextValuesIn[1];
        ins[0] = new GenerateNextValuesIn();
        Map<String, String> additionalInputParams = new HashMap<>();
        additionalInputParams.put("sourceObject", rev.getUid());
        ins[0].additionalInputParams = additionalInputParams;
        ins[0].businessObjectName = getPropStringValue(rev, "object_type").orElseThrow(
                () -> new SoaUtilException("object_type is not present"));
        ins[0].operationType = 2; // revise
        Map<String, String> propertyNameWithSelectedPattern = new HashMap<>();
        propertyNameWithSelectedPattern.put("item_revision_id", "");
        ins[0].propertyNameWithSelectedPattern = propertyNameWithSelectedPattern;
        GenerateNextValuesResponse response = dmService.generateNextValues(ins);
        if (ServiceUtil.catchPartialErrors(response.data) || response.generatedValues == null ||
            response.generatedValues.length == 0 || response.generatedValues[0] == null ||
            response.generatedValues[0].generatedValues.isEmpty() ||
            !response.generatedValues[0].generatedValues.containsKey("item_revision_id")) {
            return Optional.empty();
        }

        return Optional.ofNullable(response.generatedValues[0].generatedValues.get("item_revision_id").nextValue);
    }

    /**
     * Get not baseline latest released revision for specific item and release status.
     * If status name is null or empty, return the latest released revision no matter what the release status is.
     *
     * @param item       the item
     * @param statusName the release status name
     * @return the not baseline latest released revision, if failed to get, return an empty Optional
     */
    public Optional<ItemRevision> getNotBaselineLatestReleasedRev(Item item, String statusName) {
        Optional<List<ModelObject>> revisionListOpt = getPropListValue(item, "revision_list");
        if (revisionListOpt.isEmpty()) {
            return Optional.empty();
        }

        List<ModelObject> revisionList = revisionListOpt.get();
        Calendar latestDate = null;
        ItemRevision latestRev = null;
        for (int i = revisionList.size() - 1; i >= 0; i--) {
            ItemRevision rev = (ItemRevision) revisionList.get(i);
            if (isBaselineRev(rev)) {
                continue;
            }

            Optional<Calendar> dateReleasedOpt = getPropCalendarValue(rev, "date_released");
            if (dateReleasedOpt.isEmpty()) {
                continue;
            }
            ReleaseStatus lastReleaseStatus = (ReleaseStatus) getPropObjValue(rev, "last_release_status").get();

            if (latestDate == null) {
                latestDate = dateReleasedOpt.get();
                latestRev = rev;
                continue;
            }

            if (StringUtil.isEmpty(statusName)) {
                if (dateReleasedOpt.get().after(latestDate)) {
                    latestDate = dateReleasedOpt.get();
                    latestRev = rev;
                    continue;
                }
            }

            String lastStatusName = getPropStringValue(lastReleaseStatus, "name").get();
            if (lastStatusName.equals(statusName) && dateReleasedOpt.get().after(latestDate)) {
                latestDate = dateReleasedOpt.get();
                latestRev = rev;
            }
        }

        return Optional.ofNullable(latestRev);
    }

    /**
     * Get not baseline latest released revision for specific item revision and release status.
     * If status name is null or empty, return the latest released revision no matter what the release status is.
     *
     * @param rev        the item revision
     * @param statusName the release status name
     * @return the not baseline latest released revision, if failed to get, return an empty Optional
     */
    public Optional<ItemRevision> getNotBaselineLatestReleasedRev(ItemRevision rev, String statusName) {
        ModelObject item = getPropObjValue(rev, "items_tag").orElseThrow(
                () -> new SoaUtilException("items_tag is not present"));

        return getNotBaselineLatestReleasedRev((Item) item, statusName);
    }

    /**
     * Get property array values
     *
     * @param obj      Model object
     * @param propName Property name
     * @return Property value
     */
    public Optional<ModelObject[]> getPropArrayValue(ModelObject obj, String propName) {
        if (obj == null) {
            return Optional.empty();
        }

        try {
            getProperty(obj, propName);
            ModelObject[] objs = obj.getPropertyObject(propName).getModelObjectArrayValue();
            if (objs == null || objs.length == 0) {
                return Optional.empty();
            }
            return Optional.of(objs);
        } catch (NotLoadedException e) {
            log.error("Get property failed: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    /**
     * Get property calendar value
     *
     * @param obj      the model object
     * @param propName the property name
     * @return the calendar value, if the property is not present or failed to get, return an empty Optional
     */
    public Optional<Calendar> getPropCalendarValue(ModelObject obj, String propName) {
        if (obj == null) {
            return Optional.empty();
        }

        try {
            getProperty(obj, propName);
            return Optional.ofNullable(obj.getPropertyObject(propName).getCalendarValue());
        } catch (NotLoadedException e) {
            log.error("Get property failed: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    /**
     * Get property displayable value
     *
     * @param obj      Model object
     * @param propName Property name
     * @return Property value
     */
    public Optional<String> getPropDisplayableValue(ModelObject obj, String propName) {
        if (obj == null) {
            return Optional.empty();
        }

        try {
            getProperty(obj, propName);
            return Optional.ofNullable(obj.getPropertyObject(propName).getDisplayableValue());
        } catch (NotLoadedException e) {
            log.error("Get property failed: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    /**
     * Get properties from tc
     *
     * @param mo        the model object
     * @param propNames the property names
     * @return true if successful, otherwise false
     */
    public boolean getProperties(ModelObject mo, String[] propNames) {
        ServiceData serviceData = dmService.getProperties(new ModelObject[]{mo}, propNames);

        return !ServiceUtil.catchPartialErrors(serviceData);
    }

    /**
     * Get property from tc
     *
     * @param mo       the model object
     * @param propName the property name
     * @return true if successful, otherwise false
     */
    public boolean getProperty(ModelObject mo, String propName) {
        ServiceData serviceData = dmService.getProperties(new ModelObject[]{mo}, new String[]{propName});

        return !ServiceUtil.catchPartialErrors(serviceData);
    }

    /**
     * Get property list value
     *
     * @param obj      the model object
     * @param propName the property name
     * @return the property list value, if the property is not present or failed to get, return an empty Optional
     */
    public Optional<List<ModelObject>> getPropListValue(ModelObject obj, String propName) {
        if (obj == null) {
            return Optional.empty();
        }

        try {
            getProperty(obj, propName);
            List<ModelObject> objs = obj.getPropertyObject(propName).getModelObjectListValue();
            if (objs == null || objs.isEmpty()) {
                return Optional.empty();
            }
            return Optional.of(objs);
        } catch (NotLoadedException e) {
            log.error("Get property failed: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    /**
     * Get property object value
     *
     * @param obj      the model object
     * @param propName the property name
     * @return the property object value, if the property is not present or failed to get, return an empty Optional
     */
    public Optional<ModelObject> getPropObjValue(ModelObject obj, String propName) {
        if (obj == null) {
            return Optional.empty();
        }

        try {
            getProperty(obj, propName);
            return Optional.ofNullable(obj.getPropertyObject(propName).getModelObjectValue());
        } catch (NotLoadedException e) {
            log.error("Get property failed: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    /**
     * Get property string value
     *
     * @param obj      Model object
     * @param propName Property name
     * @return Property value
     */
    public Optional<String> getPropStringValue(ModelObject obj, String propName) {
        if (obj == null) {
            return Optional.empty();
        }

        try {
            getProperty(obj, propName);
            return Optional.ofNullable(obj.getPropertyObject(propName).getStringValue());
        } catch (NotLoadedException e) {
            log.error("Get property failed: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    /**
     * Get ps bom view
     *
     * @param itemRevision the item revision
     * @return the ps bom view, if failed to get, return an empty Optional
     */
    public Optional<PSBOMView> getPSBOMView(ItemRevision itemRevision) {
        try {
            getProperty(itemRevision, "structure_revisions");
            PSBOMViewRevision[] psBOMViewRevisions = itemRevision.get_structure_revisions();

            for (PSBOMViewRevision psBOMViewRevision : psBOMViewRevisions) {
                getProperty(psBOMViewRevision, "bom_view");
                return Optional.ofNullable(psBOMViewRevision.get_bom_view());
            }
        } catch (NotLoadedException e) {
            log.error(e.getMessage(), e);
        }

        return Optional.empty();
    }

    /**
     * Get the PSViewType by its name for the given ItemRevision.
     *
     * @param itemRevision The ItemRevision to get the PSViewType for.
     * @param viewTypeName The name of the PSViewType to retrieve.
     * @return An Optional containing the PSViewType if found, or an empty Optional if not found.
     */
    public Optional<PSViewType> getPSVT(ItemRevision itemRevision, String viewTypeName) {
        GetAllAvailableViewTypesInput[] inputs = new GetAllAvailableViewTypesInput[1];
        inputs[0] = new GetAllAvailableViewTypesInput();
        inputs[0].itemObject = (Item) getPropObjValue(itemRevision, "items_tag").get();
        inputs[0].itemRevisionObj = itemRevision;

        GetAvailableViewTypesResponse response = isService.getAvailableViewTypes(inputs);
        if (ServiceUtil.catchPartialErrors(response.serviceData) ||
            response.viewTypesOutputs == null ||
            response.viewTypesOutputs.length == 0) {
            return Optional.empty();
        }

        PSViewType[] psViewTypeTags = response.viewTypesOutputs[0].viewTags;
        for (PSViewType psViewTypeTag : psViewTypeTags) {
            String tempName = getPropStringValue(psViewTypeTag, "name").get();
            if (tempName.equals(viewTypeName)) {
                return Optional.of(psViewTypeTag);
            }
        }

        return Optional.empty();
    }

    /**
     * Get revision rule by name
     *
     * @param revRuleName the revision rule name
     * @return the revision rule, if failed to get, return an empty Optional
     */
    public Optional<RevisionRule> getRevisionRule(String revRuleName) {
        try {
            StructureManagement.GetRevisionRulesResponse response = smService.getRevisionRules();
            ServiceUtil.catchPartialErrors(response.serviceData);
            for (StructureManagement.RevisionRuleInfo revisionRuleInfo : response.output) {
                RevisionRule revRule = revisionRuleInfo.revRule;
                String objectName = getPropStringValue(revRule, "object_name")
                        .orElseThrow(() -> new SoaUtilException(
                                "Not found object_name property in revision rule"));
                if (objectName.equals((revRuleName))) {
                    return Optional.of(revRule);
                }
            }
        } catch (ServiceException e) {
            log.error(e.getMessage(), e);
        }

        return Optional.empty();
    }

    /**
     * Get saved query by name
     *
     * @param name the saved query name
     * @return the saved query, if failed to get, return an empty Optional
     */
    public Optional<ImanQuery> getSavedQueryByName(String name) {
        try {
            GetSavedQueriesResponse response = sqService.getSavedQueries();
            if (ServiceUtil.catchPartialErrors(response.serviceData) || response.serviceData == null) {
                return Optional.empty();
            }

            for (SavedQueryObject query : response.queries) {
                if (query.name.equals(name)) {
                    return Optional.ofNullable(query.query);
                }
            }

            return Optional.empty();
        } catch (ServiceException e) {
            return Optional.empty();
        }
    }

    /**
     * Get preference value(first value) by preference name
     *
     * @param prefName preference name
     * @return preference value(first value)
     */
    public Optional<String> getValueByName(String prefName) {
        String[] preferences = getValuesByName(prefName).orElseThrow(
                () -> new SoaUtilException("Not found preferences by name: " + prefName));

        if (preferences.length == 0) {
            return Optional.empty();
        }

        return Optional.ofNullable(preferences[0]);
    }

    /**
     * Get preference value(first value) by preference name and prefix
     *
     * @param prefName preference name
     * @param prefix   prefix(lower case)
     * @return preference value(first value)
     */
    public Optional<String> getValueByNameAndPrefix(String prefName, String prefix) {
        String[] preferences = getValuesByName(prefName).orElseThrow(
                () -> new SoaUtilException("Not found preferences by name: " + prefName));

        for (String preference : preferences) {
            if (preference.toLowerCase().startsWith(prefix)) {
                return Optional.of(preference);
            }
        }

        return Optional.empty();
    }

    /**
     * Get preference values by preference name
     *
     * @param prefName preference name
     * @return preference values
     */
    public Optional<String[]> getValuesByName(String prefName) {
        try {
            pmService.refreshPreferences();
        } catch (ServiceException e) {
            log.error("Catch ServiceException: {}", e.getMessage(), e);
            return Optional.empty();
        }
        GetPreferencesResponse response = pmService.getPreferences(new String[]{prefName}, false);
        if (ServiceUtil.catchPartialErrors(response.data)) {
            return Optional.empty();
        }

        CompletePreference[] preferences = response.response;
        if (preferences == null || preferences.length == 0) {
            return Optional.empty();
        }

        return Optional.ofNullable(preferences[0].values.values);
    }

    /**
     * Get working revision for specific item, if there are multiple working revisions, return the first one;
     * if there is no working revision, return an empty Optional
     *
     * @param item the item
     * @return the working revision, if failed to get, return an empty Optional
     */
    public Optional<ItemRevision> getWorkingRev(Item item) {
        ModelObject[] revisions = getPropArrayValue(item, "revision_list").orElse(new ModelObject[0]);

        for (ModelObject revision : revisions) {
            Optional<ModelObject[]> resOpt = getPropArrayValue(revision, "release_status_list");
            if (resOpt.isEmpty()) {
                return Optional.of((ItemRevision) revision);
            }
        }

        return Optional.empty();
    }

    /**
     * Get working revision for specific item revision, if there are multiple working revisions, return the first one;
     * if there is no working revision, return an empty Optional
     *
     * @param rev the item revision
     * @return the working revision, if failed to get, return an empty Optional
     */
    public Optional<ItemRevision> getWorkingRev(ItemRevision rev) {
        ModelObject item = getPropObjValue(rev, "items_tag").orElseThrow(
                () -> new SoaUtilException("items_tag is not present"));

        return getWorkingRev((Item) item);
    }

    /**
     * Get working revision list for specific item, if there is no working revision, return an empty Optional
     *
     * @param item the item
     * @return the working revision list, if failed to get, return an empty Optional
     */
    public Optional<List<ItemRevision>> getWorkingRevList(Item item) {
        List<ItemRevision> revList = new ArrayList<>();
        ModelObject[] revisions = getPropArrayValue(item, "revision_list").orElse(new ModelObject[0]);

        for (ModelObject revision : revisions) {
            Optional<ModelObject[]> resOpt = getPropArrayValue(revision, "release_status_list");
            if (resOpt.isEmpty()) {
                revList.add((ItemRevision) revision);
            }
        }

        if (revList.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(revList);
    }

    /**
     * Get working revision list for specific item revision, if there is no working revision, return an empty Optional
     *
     * @param rev the item revision
     * @return the working revision list, if failed to get, return an empty Optional
     */
    public Optional<List<ItemRevision>> getWorkingRevList(ItemRevision rev) {
        ModelObject item = getPropObjValue(rev, "items_tag").orElseThrow(
                () -> new SoaUtilException("items_tag is not present"));

        return getWorkingRevList((Item) item);
    }

    /**
     * Check if the item revision is baseline revision.
     * If the item revision id contains ".", it is considered as baseline revision.
     *
     * @param rev the item revision
     * @return true if it is baseline revision, otherwise false
     */
    public boolean isBaselineRev(ItemRevision rev) {
        String revId = getPropStringValue(rev, "item_revision_id").get();
        return revId.contains(".");
    }

    /**
     * Query model objects by saved query name and query conditions
     *
     * @param name    the query name
     * @param entries the query condition entries
     * @param values  the query condition values
     * @return the result model objects, if failed to get, return an empty Optional
     */
    public Optional<ModelObject[]> queryAll(String name, String[] entries, String[] values) {
        try {
            ImanQuery query = getSavedQueryByName(name).orElseThrow(
                    () -> new SoaUtilException("Not found saved query by name: " + name));

            QueryInput[] inputs = new QueryInput[1];
            inputs[0] = new QueryInput();
            inputs[0].query = query;
            inputs[0].entries = entries;
            inputs[0].values = values;
            SavedQueriesResponse response = sqService.executeSavedQueries(inputs);
            if (ServiceUtil.catchPartialErrors(response.serviceData) ||
                response.arrayOfResults == null ||
                response.arrayOfResults.length == 0) {
                return Optional.empty();
            }

            String[] objectUIDS = response.arrayOfResults[0].objectUIDS;
            if (objectUIDS == null || objectUIDS.length == 0) {
                return Optional.empty();
            }

            return findMosByUids(objectUIDS);
        } catch (SoaUtilException e) {
            log.error(e.getMessage(), e);
            return Optional.empty();
        }
    }

    /**
     * Query one model object by saved query name and query conditions, if there are multiple results, return the first one;
     * if there is no result, return an empty Optional
     *
     * @param name    the query name
     * @param entries the query condition entries
     * @param values  the query condition values
     * @return the result model objects, if failed to get, return an empty Optional
     */
    public Optional<ModelObject> queryOneObject(String name, String[] entries, String[] values) {
        try {
            ModelObject[] results = queryAll(name, entries, values).orElseThrow(
                    () -> new SoaUtilException("Not found any result"));
            return Optional.ofNullable(results[0]);
        } catch (SoaUtilException e) {
            log.error(e.getMessage(), e);
            return Optional.empty();
        }
    }

    /**
     * Refresh model object
     *
     * @param mo the model object
     * @return true if successful, otherwise false
     */
    public boolean refreshMo(ModelObject mo) {
        ServiceData serviceData = dmService.refreshObjects(new ModelObject[]{mo});

        return !ServiceUtil.catchPartialErrors(serviceData);
    }

    /**
     * Revise item revision
     *
     * @param rev the item revision to be revised
     * @return the new item revision after revise, if failed to revise, return an empty Optional
     */
    public Optional<ItemRevision> revise(ItemRevision rev) {
        ReviseInfo[] infos = new ReviseInfo[1];
        infos[0] = new ReviseInfo();
        infos[0].clientId = "soa";
        infos[0].baseItemRevision = rev;
        infos[0].newRevId = getNextRevId(rev).orElseThrow(() -> new SoaUtilException("Failed in getNextId"));

        ReviseResponse2 response = dmService.revise2(infos);
        if (ServiceUtil.catchPartialErrors(response.serviceData) || response.reviseOutputMap.isEmpty() ||
            !response.reviseOutputMap.containsKey("soa")) {
            return Optional.empty();
        }

        return Optional.ofNullable(response.reviseOutputMap.get("soa").newItemRev);
    }

    /**
     * Save bom window
     *
     * @param bomWindow the bom window to be saved
     * @return true if successful, otherwise false
     */
    public boolean saveBOMWindow(BOMWindow bomWindow) {
        SaveBOMWindowsResponse response = smService.saveBOMWindows(new BOMWindow[]{bomWindow});

        return !ServiceUtil.catchPartialErrors(response.serviceData);
    }

    /**
     * Set properties
     *
     * @param obj     the model object
     * @param propMap the property map, key is property name, value is property value.
     *                <p>
     *                value type can be String or String[].
     * @return true if successful, otherwise false
     */
    public boolean setProperties(ModelObject obj, Map<String, Object> propMap) {
        PropInfo[] infos = new PropInfo[1];
        infos[0] = new PropInfo();
        infos[0].object = obj;
        propMap.entrySet().removeIf(entry -> entry.getValue() == null);
        NameValueStruct1[] structs = new NameValueStruct1[propMap.size()];
        int i = 0;
        for (Entry<String, Object> entry : propMap.entrySet()) {
            NameValueStruct1 struct = new NameValueStruct1();
            struct.name = entry.getKey();
            if (entry.getValue() instanceof String) {
                struct.values = new String[]{(String) entry.getValue()};
            } else if (entry.getValue() instanceof String[]) {
                struct.values = (String[]) entry.getValue();
            } else {
                throw new SoaUtilException(
                        "Unsupported property value type: " + entry.getValue().getClass().getName() + ", key: " +
                        entry.getKey() + ", only support String and String[]");
            }
            structs[i++] = struct;
        }
        infos[0].vecNameVal = structs;

        SetPropertyResponse response = dmService.setProperties(infos, new String[]{});

        return !ServiceUtil.catchPartialErrors(response.data);
    }

    /**
     * Export bom to excel
     *
     * @param inputData The input data to export
     * @return Excel file path
     */
    private Optional<String> exportBom2Excel(ExportToApplicationInputData2 inputData) {
        try {
            ExportToApplicationResponse1 response = fieService.exportToApplication(
                    new ExportToApplicationInputData2[]{inputData});
            // Catch errors but continue
            ServiceUtil.catchPartialErrors(response.serviceData);
            if (response.transientFileReadTickets == null || response.transientFileReadTickets.length == 0) {
                return Optional.empty();
            }

            String ticket = response.transientFileReadTickets[0];
            return Optional.of(TRANSIENT_DIRECTORY + File.separator + ticket.split(TRANSIENT_SEPARATOR)[1]);
        } catch (ServiceException e) {
            log.error("Catch ServiceException: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    /**
     * Get bom by item revision and rev rule
     *
     * @param itemRevision the item revision
     * @param revRule      the rev rule
     * @return the bom, if failed to get, return an empty Optional
     */
    private Optional<CreateBOMWindowsOutput> getBOM(ItemRevision itemRevision, String revRule) {
        CreateWindowsInfo3[] inputs = new CreateWindowsInfo3[1];
        inputs[0] = new CreateWindowsInfo3();
        getPSBOMView(itemRevision).ifPresent(psbomView -> inputs[0].bomView = psbomView);
        inputs[0].itemRev = itemRevision;
        RevisionRuleConfigInfo revRuleConfigInfo = new RevisionRuleConfigInfo();
        revRuleConfigInfo.revRule = getRevisionRule(revRule).orElseThrow(
                () -> new SoaUtilException("Not found revision rule by name: " + revRule));
        inputs[0].revRuleConfigInfo = revRuleConfigInfo;
        CreateBOMWindowsResponse response = smService.createOrReConfigureBOMWindows(inputs);
        if (ServiceUtil.catchPartialErrors(response.serviceData) ||
            response.output == null ||
            response.output.length == 0) {
            return Optional.empty();
        }

        return Optional.ofNullable(response.output[0]);
    }

    /**
     * Set release status
     *
     * @param obj       the workspace object
     * @param operation the operation
     * @param newStatus the new status
     * @param oldStatus the old status
     * @return true if successful, otherwise false
     */
    private boolean setReleaseStatus(WorkspaceObject obj, String operation, String newStatus, String oldStatus) {
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

    public void setBOMWindowPack(BOMWindow window) throws NotLoadedException {
        log.info("setBOMWindowPack start");
        String propName = "is_packed_by_default";
        dmService.getProperties(new ModelObject[]{window}, new String[]{propName});
        boolean bolPack = window.get_is_packed_by_default();
        System.out.println(propName + "=" + bolPack);
        if (!bolPack) {
            String[] as = {"ENABLE_PSE_BULLETIN_BOARD"};
            DataManagement.PropInfo[] apropinfo = new DataManagement.PropInfo[1];
            apropinfo[0] = new DataManagement.PropInfo();
            apropinfo[0].object = window;
            DataManagement.NameValueStruct1[] vecNameVal = new DataManagement.NameValueStruct1[1];
            vecNameVal[0] = new DataManagement.NameValueStruct1();
            vecNameVal[0].name = propName;
            vecNameVal[0].values = new String[]{Property.toBooleanString(true)};
            apropinfo[0].vecNameVal = vecNameVal;
            dmService.setProperties(apropinfo, as);
            DataManagement.SetPropertyResponse setpropertyresponse = dmService.setProperties(apropinfo, as);
            dmService.refreshObjects(new ModelObject[]{window});
        }
        bolPack = window.get_is_packed_by_default();
        log.info("{}.now={}", propName, bolPack);
        log.info("setBOMWindowPack end");
    }

    // public Optional<File> downloadFile(Dataset dataset) {
    //     ModelObject[] refs = getPropArrayValue(dataset, "ref_list").orElseThrow(
    //             () -> new SoaUtilException("ref_list is empty"));
    //     GetFileResponse response = fmUtil.getFiles(refs);
    //     if (response.sizeOfPartialErrors() > 0) {
    //         for (int i = 0; i < response.sizeOfPartialErrors(); i++) {
    //             log.error("Catch partial error: {}", Arrays.toString(response.getPartialError(i).getMessages()));
    //         }
    //         return Optional.empty();
    //     }
    //
    //     File[] files = response.getFiles();
    //     if (files == null || files.length == 0) {
    //         return Optional.empty();
    //     }
    //
    //     return Optional.ofNullable(files[0]);
    // }

}
