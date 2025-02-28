package com.codicefun.tc.soa.util;

import com.codicefun.tc.soa.clientx.AppXSession;
import com.codicefun.tc.soa.exception.UtilException;
import com.teamcenter.schemas.soa._2006_03.exceptions.ServiceException;
import com.teamcenter.services.strong.importexport.FileImportExportService;
import com.teamcenter.services.strong.importexport._2011_06.FileImportExport.ExportToApplicationInputData2;
import com.teamcenter.services.strong.importexport._2011_06.FileImportExport.ExportToApplicationResponse1;
import com.teamcenter.soa.client.Connection;
import com.teamcenter.soa.client.model.ModelObject;
import com.teamcenter.soa.client.model.strong.BOMLine;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.Optional;

/**
 * File import export util
 */
@Slf4j
public class FileImportExportUtil {

    /**
     * Transient file directory, read from TC Preference: "Transient_Volume_RootDir"
     */
    private static final String TRANSIENT_DIRECTORY = PreferenceManagementUtil
            .getValueByNameAndPrefix("Transient_Volume_RootDir", SystemUtil.isWindows() ? "c:" : "/")
            .orElseThrow(() -> new UtilException("Not found Transient_Volume_RootDir"));

    /**
     * Transient file separator: ";\"
     * <p>
     * TODO: separator in linux?
     */
    private static final String TRANSIENT_SEPARATOR = "%3b%5c";

    private static final Connection connection = AppXSession.getConnection();
    private static final FileImportExportService fieService = FileImportExportService.getService(connection);

    /**
     * Export bom to excel
     *
     * @param inputData The input data to export
     * @return Excel file path
     */
    private static Optional<String> exportBom2Excel(ExportToApplicationInputData2 inputData) {
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
     * Export bom to excel by attributes
     *
     * @param bomLine The bom line to be exported
     * @param attrs   The bom line attributes to be exported
     * @return Excel file path
     */
    public static Optional<String> exportBom2ExcelByAttrs(BOMLine bomLine, String[] attrs) {
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
    public static Optional<String> exportBom2ExcelByTemplateId(BOMLine bomLine, String templateId) {
        ExportToApplicationInputData2 inputData = new ExportToApplicationInputData2();
        inputData.objectsToExport = new ModelObject[]{bomLine};
        inputData.applicationFormat = "MSExcel";
        inputData.templateId = templateId;
        inputData.templateType = "ExcelTemplate";

        return exportBom2Excel(inputData);
    }

}
