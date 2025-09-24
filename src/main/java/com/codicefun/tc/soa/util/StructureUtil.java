package com.codicefun.tc.soa.util;

import com.codicefun.tc.soa.clientx.AppXSession;
import com.codicefun.tc.soa.exception.SoaUtilException;
import com.teamcenter.services.internal.strong.structuremanagement._2011_06.Structure.CreateOrSaveAsPSBOMViewRevisionInput;
import com.teamcenter.services.internal.strong.structuremanagement._2011_06.Structure.CreateOrSaveAsPSBOMViewRevisionResponse;
import com.teamcenter.services.internal.strong.structuremanagement._2011_06.Structure.GetAllAvailableViewTypesInput;
import com.teamcenter.services.internal.strong.structuremanagement._2011_06.Structure.GetAvailableViewTypesResponse;
import com.teamcenter.services.strong.structuremanagement.StructureService;
import com.teamcenter.services.strong.structuremanagement._2012_09.Structure.AddInformation;
import com.teamcenter.services.strong.structuremanagement._2012_09.Structure.AddParam;
import com.teamcenter.services.strong.structuremanagement._2012_09.Structure.AddResponse;
import com.teamcenter.soa.client.Connection;
import com.teamcenter.soa.client.model.strong.*;

import java.util.Optional;

@SuppressWarnings("OptionalGetWithoutIsPresent")
public class StructureUtil {

    private static final Connection connection = AppXSession.getConnection();
    private static final StructureService structureService = StructureService.getService(connection);

    /**
     * Internal Structure Service
     */
    private static final com.teamcenter.services.internal.strong.structuremanagement.StructureService isService =
            com.teamcenter.services.internal.strong.structuremanagement.StructureService.getService(connection);

    public static Optional<BOMLine> addChild(BOMLine bomLine, ItemRevision itemRevision) {
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
     * Create a PSBOMViewRevision for the given ItemRevision with specified view type and precision.
     *
     * @param itemRevision The ItemRevision to create the PSBOMViewRevision for.
     * @param viewTypeName The name of the PSViewType to use. If null or empty, the default view type will be used.
     * @param isPrevise    Whether to create a precise PSBOMViewRevision.
     * @return An Optional containing the created PSBOMViewRevision, or an empty Optional if creation failed.
     */
    public static Optional<PSBOMViewRevision> createPSBVR(ItemRevision itemRevision, String viewTypeName,
                                                          boolean isPrevise) {
        CreateOrSaveAsPSBOMViewRevisionInput[] inputs = new CreateOrSaveAsPSBOMViewRevisionInput[1];
        inputs[0] = new CreateOrSaveAsPSBOMViewRevisionInput();
        inputs[0].itemObject = (Item) ModelObjectUtil.getPropObjValue(itemRevision, "items_tag").get();
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
     * Create a PSBOMViewRevision for the given ItemRevision.
     *
     * @param itemRevision The ItemRevision to create the PSBOMViewRevision for.
     * @return An Optional containing the created PSBOMViewRevision, or an empty Optional if creation failed.
     */
    public static Optional<PSBOMViewRevision> createPSBVR(ItemRevision itemRevision) {
        return createPSBVR(itemRevision, null, false);
    }

    /**
     * Get the PSViewType by its name for the given ItemRevision.
     *
     * @param itemRevision The ItemRevision to get the PSViewType for.
     * @param viewTypeName The name of the PSViewType to retrieve.
     * @return An Optional containing the PSViewType if found, or an empty Optional if not found.
     */
    public static Optional<PSViewType> getPSVT(ItemRevision itemRevision, String viewTypeName) {
        GetAllAvailableViewTypesInput[] inputs = new GetAllAvailableViewTypesInput[1];
        inputs[0] = new GetAllAvailableViewTypesInput();
        inputs[0].itemObject = (Item) ModelObjectUtil.getPropObjValue(itemRevision, "items_tag").get();
        inputs[0].itemRevisionObj = itemRevision;

        GetAvailableViewTypesResponse response = isService.getAvailableViewTypes(inputs);
        if (ServiceUtil.catchPartialErrors(response.serviceData) ||
            response.viewTypesOutputs == null ||
            response.viewTypesOutputs.length == 0) {
            return Optional.empty();
        }

        PSViewType[] psViewTypeTags = response.viewTypesOutputs[0].viewTags;
        for (PSViewType psViewTypeTag : psViewTypeTags) {
            String tempName = ModelObjectUtil.getPropStringValue(psViewTypeTag, "name").get();
            if (tempName.equals(viewTypeName)) {
                return Optional.of(psViewTypeTag);
            }
        }

        return Optional.empty();
    }

}
