package com.codicefun.tc.soa.util;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * File import export util test
 * <p>
 * TODO: Finish FileImportExportUtilTest
 */
class FileImportExportUtilTest {

    @Test
    void exportBom2ExcelByAttrs() {
        Optional<String> result = FileImportExportUtil.exportBom2ExcelByAttrs(null, null);
        assertTrue(result.isPresent());
    }

    @Test
    void exportBom2ExcelByTemplateId() {
        Optional<String> result = FileImportExportUtil.exportBom2ExcelByTemplateId(null, null);
        assertTrue(result.isPresent());
    }

}
