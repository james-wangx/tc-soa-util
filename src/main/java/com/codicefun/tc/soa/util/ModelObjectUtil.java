package com.codicefun.tc.soa.util;

import com.teamcenter.soa.client.model.ModelObject;
import com.teamcenter.soa.exceptions.NotLoadedException;
import lombok.extern.slf4j.Slf4j;

/**
 * Model object util
 */
@Slf4j
public class ModelObjectUtil {

    /**
     * Get property string value
     *
     * @param obj      Model object
     * @param propName Property name
     * @return Property value
     */
    public static String getPropStringValue(ModelObject obj, String propName) {
        String propValue = "";

        if (obj == null) {
            return propValue;
        }

        DataManagementUtil.getProperty(obj, propName);
        try {
            propValue = obj.getPropertyObject(propName).getStringValue();
        } catch (NotLoadedException e) {
            log.error("Get property failed: {}", e.getMessage(), e);
        }

        return propValue;
    }

    /**
     * Get property displayable value
     *
     * @param obj      Model object
     * @param propName Property name
     * @return Property value
     */
    public static String getPropDisplayableValue(ModelObject obj, String propName) {
        String propValue = "";

        if (obj == null) {
            return propValue;
        }

        DataManagementUtil.getProperty(obj, propName);
        try {
            propValue = obj.getPropertyObject(propName).getDisplayableValue();
        } catch (NotLoadedException e) {
            log.error("Get property failed: {}", e.getMessage(), e);
        }

        return propValue;
    }

    /**
     * Get property array values
     *
     * @param obj      Model object
     * @param propName Property name
     * @return Property value
     */
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
