package com.codicefun.tc.soa.util;

import com.codicefun.tc.soa.clientx.AppXSession;
import com.teamcenter.schemas.soa._2006_03.exceptions.ServiceException;
import com.teamcenter.services.strong.cad.StructureManagementService;
import com.teamcenter.services.strong.cad._2007_01.StructureManagement;
import com.teamcenter.services.strong.core.DataManagementService;
import com.teamcenter.services.strong.importexport.FileImportExportService;
import com.teamcenter.services.strong.importexport._2011_06.FileImportExport;
import com.teamcenter.soa.client.Connection;
import com.teamcenter.soa.client.model.ErrorStack;
import com.teamcenter.soa.client.model.ModelObject;
import com.teamcenter.soa.client.model.ServiceData;
import com.teamcenter.soa.client.model.strong.BOMLine;
import com.teamcenter.soa.client.model.strong.ItemRevision;
import com.teamcenter.soa.client.model.strong.PSBOMView;
import com.teamcenter.soa.client.model.strong.PSBOMViewRevision;
import com.teamcenter.soa.exceptions.NotLoadedException;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * Bom util
 */
public class BomUtil {

    /**
     * Transient file directory
     * <p>
     * TODO: Get from TC Preference
     */
    private static final String TRANSIENT_DIRECTORY = "C:\\temp\\transientVolume_infodba";

    /**
     * Transient file separator: ";\"
     */
    private static final String TRANSIENT_SEPARATOR = "%3b%5c";

    private static final Connection connection = AppXSession.getConnection();
    private static final DataManagementService dmService = DataManagementService.getService(connection);
    private static final StructureManagementService smService = StructureManagementService.getService(connection);
    private static final FileImportExportService fieService = FileImportExportService.getService(connection);

    /**
     * Get ps bom view
     */
    public static PSBOMView getPSBOMView(ItemRevision itemRev) throws NotLoadedException {
        MoUtil.getProperty(itemRev, "structure_revisions");
        PSBOMViewRevision[] psBOMViewRevisions = itemRev.get_structure_revisions();
        PSBOMView psBOMView = null;

        for (PSBOMViewRevision psBOMViewRevision : psBOMViewRevisions) {
            MoUtil.getProperty(psBOMViewRevision, "bom_view");
            psBOMView = psBOMViewRevision.get_bom_view();
        }

        return psBOMView;
    }

    /**
     * Send rev to structure manager and return the top line
     */
    public static BOMLine getBOMLine(ItemRevision itemRev) throws NotLoadedException {
        MoUtil.getProperty(itemRev, "structure_revisions");
        PSBOMViewRevision[] psBOMViewRevisions = itemRev.get_structure_revisions();
        if (psBOMViewRevisions != null && psBOMViewRevisions.length > 0) {
            dmService.refreshObjects(psBOMViewRevisions);
        }

        PSBOMView bomView = getPSBOMView(itemRev);
        if (bomView != null) {
            dmService.refreshObjects(new PSBOMView[]{bomView});
        }

        com.teamcenter.services.strong.cad._2019_06.StructureManagement.CreateWindowsInfo3 info = new com.teamcenter.services.strong.cad._2019_06.StructureManagement.CreateWindowsInfo3();
        info.bomView = bomView;
        info.itemRev = itemRev;
        StructureManagement.CreateBOMWindowsResponse response = smService.createOrReConfigureBOMWindows(
                new com.teamcenter.services.strong.cad._2019_06.StructureManagement.CreateWindowsInfo3[]{info});

        return response.output[0].bomLine;
    }

    /**
     * Export bom to excel
     */
    private static String exportBom2Excel(FileImportExport.ExportToApplicationInputData2 inputData) throws
            ServiceException {
        FileImportExport.ExportToApplicationResponse1 response = fieService.exportToApplication(
                new FileImportExport.ExportToApplicationInputData2[]{inputData});
        ServiceData serviceData = response.serviceData;
        int errorSize = serviceData.sizeOfPartialErrors();
        if (errorSize == 0) {
            String ticket = response.transientFileReadTickets[0];
            return TRANSIENT_DIRECTORY + File.separator + ticket.split(TRANSIENT_SEPARATOR)[1];
        }
        for (int i = 0; i < errorSize; i++) {
            ErrorStack error = serviceData.getPartialError(i);
            System.out.println("error = " + error);
        }

        return null;
    }

    /**
     * Export bom to excel by attributes
     */
    public static String exportBom2ExcelByAttrs(BOMLine bomLine, String[] attrs) throws ServiceException {
        FileImportExport.ExportToApplicationInputData2 inputData = new FileImportExport.ExportToApplicationInputData2();
        inputData.objectsToExport = new ModelObject[]{bomLine};
        inputData.attributesToExport = attrs;
        inputData.applicationFormat = "MSExcel";

        return exportBom2Excel(inputData);
    }

    /**
     * Export bom to excel by template id
     */
    public static String exportBom2ExcelByTemplateId(BOMLine bomLine, String templateId) throws ServiceException {
        FileImportExport.ExportToApplicationInputData2 inputData = new FileImportExport.ExportToApplicationInputData2();
        inputData.objectsToExport = new ModelObject[]{bomLine};
        inputData.applicationFormat = "MSExcel";
        inputData.templateId = templateId;
        inputData.templateType = "ExcelTemplate";

        return exportBom2Excel(inputData);
    }

    /**
     * Get absolute occurrence context property list
     */
    public static List<String> getAbsOccCxtPropList(BOMLine bomLine) {
        String propsInContext = MoUtil.getPropStringValue(bomLine, "bl_properties_in_context");

        return Arrays.asList(propsInContext.split(","));
    }

    /**
     * Get children bom line
     */
    private static StructureManagement.ExpandPSData[] getChildren(BOMLine bomLine) {
        StructureManagement.ExpandPSOneLevelInfo expendInfo = new StructureManagement.ExpandPSOneLevelInfo();
        expendInfo.parentBomLines = new BOMLine[]{bomLine};
        expendInfo.excludeFilter = "None";
        StructureManagement.ExpandPSOneLevelPref expandPref = new StructureManagement.ExpandPSOneLevelPref();
        expandPref.expItemRev = false;
        StructureManagement.ExpandPSOneLevelOutput[] expandOutputs = smService.expandPSOneLevel(expendInfo,
                expandPref).output;

        if (expandOutputs.length == 0) {
            return null;
        }

        StructureManagement.ExpandPSOneLevelOutput expandOutput = expandOutputs[0];

        return expandOutput.children;
    }

    /**
     * Print all absolute occurrence context property list
     */
    public static void printAbsOccCxtProp(BOMLine bomLine) {
        StructureManagement.ExpandPSData[] children = getChildren(bomLine);
        if (children == null) {
            return;
        }

        for (StructureManagement.ExpandPSData child : children) {
            BOMLine childBOMLine = child.bomLine;
            String title = MoUtil.getPropStringValue(childBOMLine, "bl_indented_title");
            System.out.println("title = " + title);
            List<String> absOccCxtPropList = getAbsOccCxtPropList(childBOMLine);
            System.out.println("absOccCxtPropList = " + absOccCxtPropList);
            System.out.println("-------------------------------------------------------------------------------------");
            printAbsOccCxtProp(childBOMLine);
        }
    }

}
