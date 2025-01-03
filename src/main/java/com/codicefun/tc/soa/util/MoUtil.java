package com.codicefun.tc.soa.util;

import com.codicefun.tc.soa.clientx.AppXSession;
import com.teamcenter.services.strong.core.DataManagementService;
import com.teamcenter.soa.client.Connection;
import com.teamcenter.soa.client.model.ModelObject;
import com.teamcenter.soa.exceptions.NotLoadedException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MoUtil {

    private static final Connection connection = AppXSession.getConnection();
    private static final DataManagementService dmService = DataManagementService.getService(connection);

    public static void getProperty(ModelObject mo, String propName) {
        dmService.getProperties(new ModelObject[]{mo}, new String[]{propName});
    }

    public static String getPropStringValue(ModelObject mo, String propName) {
        String propValue = "";

        if (mo == null) {
            return propValue;
        }

        getProperty(mo, propName);
        try {
            propValue = mo.getPropertyObject(propName).getStringValue();
        } catch (NotLoadedException e) {
            log.error("Get property failed: {}", e.getMessage(), e);
        }

        return propValue;
    }

    public static String getPropDisplayableValue(ModelObject mo, String propName) {
        String propValue = "";

        if (mo == null) {
            return propValue;
        }

        getProperty(mo, propName);
        try {
            propValue = mo.getPropertyObject(propName).getDisplayableValue();
        } catch (NotLoadedException e) {
            log.error("Get property failed: {}", e.getMessage(), e);
        }

        return propValue;
    }

    public static ModelObject[] getPropArrayValues(ModelObject obj, String propName) {
        ModelObject[] propValues = new ModelObject[]{};

        if (obj == null) {
            return propValues;
        }

        getProperty(obj, propName);
        try {
            propValues = obj.getPropertyObject(propName).getModelObjectArrayValue();
        } catch (NotLoadedException e) {
            log.error("Get property failed: {}", e.getMessage(), e);
        }

        return propValues;
    }

}
