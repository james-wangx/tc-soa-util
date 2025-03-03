package com.codicefun.tc.soa.util;

import com.teamcenter.soa.client.model.ModelObject;
import com.teamcenter.soa.exceptions.NotLoadedException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MoUtil {

    public static String getPropStringValue(ModelObject mo, String propName) {
        String propValue = "";

        if (mo == null) {
            return propValue;
        }

        DataManagementUtil.getProperty(mo, propName);
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

        DataManagementUtil.getProperty(mo, propName);
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

        DataManagementUtil.getProperty(obj, propName);
        try {
            propValues = obj.getPropertyObject(propName).getModelObjectArrayValue();
        } catch (NotLoadedException e) {
            log.error("Get property failed: {}", e.getMessage(), e);
        }

        return propValues;
    }

}
