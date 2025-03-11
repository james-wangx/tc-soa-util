package com.codicefun.tc.soa.util;

import com.codicefun.tc.soa.clientx.AppXSession;
import com.teamcenter.services.strong.cad.StructureManagementService;
import com.teamcenter.services.strong.cad._2007_01.StructureManagement.*;
import com.teamcenter.services.strong.cad._2019_06.StructureManagement.CreateWindowsInfo3;
import com.teamcenter.services.strong.core.DataManagementService;
import com.teamcenter.soa.client.Connection;
import com.teamcenter.soa.client.model.strong.BOMLine;
import com.teamcenter.soa.client.model.strong.ItemRevision;
import com.teamcenter.soa.client.model.strong.PSBOMView;
import com.teamcenter.soa.client.model.strong.PSBOMViewRevision;
import com.teamcenter.soa.exceptions.NotLoadedException;

import java.util.Arrays;
import java.util.List;

/**
 * Bom util
 */
public class BomUtil {

    private static final Connection connection = AppXSession.getConnection();
    private static final DataManagementService dmService = DataManagementService.getService(connection);
    private static final StructureManagementService smService = StructureManagementService.getService(connection);

    /**
     * Get ps bom view
     */
    public static PSBOMView getPSBOMView(ItemRevision itemRev) throws NotLoadedException {
        DataManagementUtil.getProperty(itemRev, "structure_revisions");
        PSBOMViewRevision[] psBOMViewRevisions = itemRev.get_structure_revisions();
        PSBOMView psBOMView = null;

        for (PSBOMViewRevision psBOMViewRevision : psBOMViewRevisions) {
            DataManagementUtil.getProperty(psBOMViewRevision, "bom_view");
            psBOMView = psBOMViewRevision.get_bom_view();
        }

        return psBOMView;
    }

    /**
     * Send rev to structure manager and return the top line
     */
    public static BOMLine getBOMLine(ItemRevision itemRev) throws NotLoadedException {
        DataManagementUtil.getProperty(itemRev, "structure_revisions");
        PSBOMViewRevision[] psBOMViewRevisions = itemRev.get_structure_revisions();
        if (psBOMViewRevisions != null && psBOMViewRevisions.length > 0) {
            dmService.refreshObjects(psBOMViewRevisions);
        }

        PSBOMView bomView = getPSBOMView(itemRev);
        if (bomView != null) {
            dmService.refreshObjects(new PSBOMView[]{bomView});
        }

        CreateWindowsInfo3 info = new CreateWindowsInfo3();
        info.bomView = bomView;
        info.itemRev = itemRev;
        CreateBOMWindowsResponse response = smService.createOrReConfigureBOMWindows(
                new CreateWindowsInfo3[]{info});

        return response.output[0].bomLine;
    }

    /**
     * Get absolute occurrence context property list
     */
    public static List<String> getAbsOccCxtPropList(BOMLine bomLine) {
        String propsInContext = ModelObjectUtil.getPropStringValue(bomLine, "bl_properties_in_context");

        return Arrays.asList(propsInContext.split(","));
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

    /**
     * Print all absolute occurrence context property list
     */
    public static void printAbsOccCxtProp(BOMLine bomLine) {
        ExpandPSData[] children = getChildren(bomLine);
        if (children == null) {
            return;
        }

        for (ExpandPSData child : children) {
            BOMLine childBOMLine = child.bomLine;
            String title = ModelObjectUtil.getPropStringValue(childBOMLine, "bl_indented_title");
            System.out.println("title = " + title);
            List<String> absOccCxtPropList = getAbsOccCxtPropList(childBOMLine);
            System.out.println("absOccCxtPropList = " + absOccCxtPropList);
            System.out.println("-------------------------------------------------------------------------------------");
            printAbsOccCxtProp(childBOMLine);
        }
    }

}
