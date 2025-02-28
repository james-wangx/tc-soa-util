package com.codicefun.tc.soa.util;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class FileImportExportUtilTest {

    @Test
    void testExportBom2ExcelByAttrs() {
        // TODO: implement testExportBom2ExcelByAttrs
        Optional<String> result = FileImportExportUtil.exportBom2ExcelByAttrs(null, null);
        assertTrue(result.isPresent());
    }

    @Test
    void testExportBom2ExcelByTemplateId() {
        // TODO: implement testExportBom2ExcelByTemplateId
        Optional<String> result = FileImportExportUtil.exportBom2ExcelByTemplateId(null, null);
        assertTrue(result.isPresent());
    }

}
