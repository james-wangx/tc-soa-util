package com.codicefun.tc.soa.util;

import com.codicefun.tc.soa.clientx.AppXSession;
import com.teamcenter.services.strong.cad.StructureManagementService;
import com.teamcenter.services.strong.cad._2007_01.StructureManagement.*;
import com.teamcenter.services.strong.cad._2008_06.StructureManagement.SaveBOMWindowsResponse;
import com.teamcenter.services.strong.cad._2019_06.StructureManagement.CreateWindowsInfo3;
import com.teamcenter.services.strong.core.DataManagementService;
import com.teamcenter.soa.client.Connection;
import com.teamcenter.soa.client.model.strong.*;
import com.teamcenter.soa.exceptions.NotLoadedException;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Bom util
 */
@Slf4j
public class StructureManagementUtil {

    private static final Connection connection = AppXSession.getConnection();
    private static final DataManagementService dmService = DataManagementService.getService(connection);
    private static final StructureManagementService smService = StructureManagementService.getService(connection);

    /**
     * Get ps bom view
     */
    public static Optional<PSBOMView> getPSBOMView(ItemRevision itemRevision) {
        try {
            DataManagementUtil.getProperty(itemRevision, "structure_revisions");
            PSBOMViewRevision[] psBOMViewRevisions = itemRevision.get_structure_revisions();

            for (PSBOMViewRevision psBOMViewRevision : psBOMViewRevisions) {
                DataManagementUtil.getProperty(psBOMViewRevision, "bom_view");
                return Optional.ofNullable(psBOMViewRevision.get_bom_view());
            }
        } catch (NotLoadedException e) {
            log.error(e.getMessage(), e);
        }

        return Optional.empty();
    }

    public static Optional<CreateBOMWindowsOutput> getBOM(ItemRevision itemRevision) {
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
    public static Optional<BOMLine> getBOMLine(ItemRevision itemRevision) {
        return getBOM(itemRevision).map(bom -> bom.bomLine);
    }

    public static Optional<BOMWindow> getBOMWindow(ItemRevision itemRevision) {
        return getBOM(itemRevision).map(bom -> bom.bomWindow);
    }

    public static boolean saveBOMWindow(BOMWindow bomWindow) {
        SaveBOMWindowsResponse response = smService.saveBOMWindows(new BOMWindow[]{bomWindow});

        return !ServiceUtil.catchPartialErrors(response.serviceData);
    }

    /**
     * Get absolute occurrence context property list
     */
    public static Optional<List<String>> getAbsOccCxtPropList(BOMLine bomLine) {
        Optional<String> result = ModelObjectUtil.getPropStringValue(bomLine, "bl_properties_in_context");

        return result.map(s -> Arrays.asList(s.split(",")));
    }

    /**
     * Get children bom line
     */
    private static ExpandPSData[] getChildren(BOMLine bomLine) {
        ExpandPSOneLevelInfo expendInfo = new ExpandPSOneLevelInfo();
        expendInfo.parentBomLines = new BOMLine[]{bomLine};
        expendInfo.excludeFilter = "None";
        ExpandPSOneLevelPref expandPref = new ExpandPSOneLevelPref();
        expandPref.expItemRev = false;
        ExpandPSOneLevelOutput[] expandOutputs = smService.expandPSOneLevel(expendInfo,
                expandPref).output;

        if (expandOutputs.length == 0) {
            return null;
        }

        ExpandPSOneLevelOutput expandOutput = expandOutputs[0];

        return expandOutput.children;
    }

}
