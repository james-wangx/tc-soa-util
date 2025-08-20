package com.codicefun.tc.soa.util;

import com.teamcenter.soa.client.model.ModelObject;
import com.teamcenter.soa.exceptions.NotLoadedException;
import lombok.extern.slf4j.Slf4j;

import java.util.Calendar;
import java.util.List;
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
    public static Optional<ModelObject[]> getPropArrayValue(ModelObject obj, String propName) {
        if (obj == null) {
            return Optional.empty();
        }

        try {
            DataManagementUtil.getProperty(obj, propName);
            ModelObject[] objs = obj.getPropertyObject(propName).getModelObjectArrayValue();
            if (objs == null || objs.length == 0) {
                return Optional.empty();
            }
            return Optional.of(objs);
        } catch (NotLoadedException e) {
            log.error("Get property failed: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    public static Optional<List<ModelObject>> getPropListValue(ModelObject obj, String propName) {
        if (obj == null) {
            return Optional.empty();
        }

        try {
            DataManagementUtil.getProperty(obj, propName);
            List<ModelObject> objs = obj.getPropertyObject(propName).getModelObjectListValue();
            if (objs == null || objs.isEmpty()) {
                return Optional.empty();
            }
            return Optional.of(objs);
        } catch (NotLoadedException e) {
            log.error("Get property failed: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    public static Optional<ModelObject> getPropObjValue(ModelObject obj, String propName) {
        if (obj == null) {
            return Optional.empty();
        }

        try {
            DataManagementUtil.getProperty(obj, propName);
            return Optional.ofNullable(obj.getPropertyObject(propName).getModelObjectValue());
        } catch (NotLoadedException e) {
            log.error("Get property failed: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    public static Optional<Calendar> getPropCalendarValue(ModelObject obj, String propName) {
        if (obj == null) {
            return Optional.empty();
        }

        try {
            DataManagementUtil.getProperty(obj, propName);
            return Optional.ofNullable(obj.getPropertyObject(propName).getCalendarValue());
        } catch (NotLoadedException e) {
            log.error("Get property failed: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

}
