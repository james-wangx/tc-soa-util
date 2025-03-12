package com.codicefun.tc.soa.util;

import com.teamcenter.soa.client.model.ModelObject;
import com.teamcenter.soa.exceptions.NotLoadedException;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

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
    public static Optional<String> getPropStringValue(ModelObject obj, String propName) {
        if (obj == null) {
            return Optional.empty();
        }

        try {
            DataManagementUtil.getProperty(obj, propName);
            return Optional.ofNullable(obj.getPropertyObject(propName).getStringValue());
        } catch (NotLoadedException e) {
            log.error("Get property failed: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    /**
     * Get property displayable value
     *
     * @param obj      Model object
     * @param propName Property name
     * @return Property value
     */
    public static Optional<String> getPropDisplayableValue(ModelObject obj, String propName) {
        if (obj == null) {
            return Optional.empty();
        }

        try {
            DataManagementUtil.getProperty(obj, propName);
            return Optional.ofNullable(obj.getPropertyObject(propName).getDisplayableValue());
        } catch (NotLoadedException e) {
            log.error("Get property failed: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    /**
     * Get property array values
     *
     * @param obj      Model object
     * @param propName Property name
     * @return Property value
     */
    public static Optional<ModelObject[]> getPropArrayValues(ModelObject obj, String propName) {
        if (obj == null) {
            return Optional.empty();
        }

        try {
            DataManagementUtil.getProperty(obj, propName);
            return Optional.ofNullable(obj.getPropertyObject(propName).getModelObjectArrayValue());
        } catch (NotLoadedException e) {
            log.error("Get property failed: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

}
