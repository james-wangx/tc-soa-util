package com.codicefun.tc.soa.util;

import com.codicefun.tc.soa.exception.SoaUtilException;
import com.teamcenter.soa.client.model.ModelObject;
import com.teamcenter.soa.client.model.strong.BOMLine;
import com.teamcenter.soa.client.model.strong.ItemRevision;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * File import export util test
 * <p>
 * TODO: Finish FileImportExportUtilTest
 */
class FileImportExportUtilTest extends UtilTest {

    @Test
    void exportBom2ExcelByAttrs() {
        ModelObject rev = DataManagementUtil.findMoByUid("BAfhTnkno0c12D")
                                            .orElseThrow(() -> new SoaUtilException("Not found item revision"));
        BOMLine bomLine = StructureManagementUtil.getBOMLine((ItemRevision) rev)
                                                 .orElseThrow(() -> new SoaUtilException("Not found BOMLine"));

        String[] attrs = new String[]{"BOMLine.bl_real_occurrence", "BOMLine.bl_occ_fnd0objectId"};
        Optional<String> result = FileImportExportUtil.exportBom2ExcelByAttrs(bomLine, attrs);
        assertTrue(result.isPresent());
    }

    @Test
    void exportBom2ExcelByTemplateId() {
        Optional<String> result = FileImportExportUtil.exportBom2ExcelByTemplateId(null, null);
        assertTrue(result.isPresent());
    }

}
