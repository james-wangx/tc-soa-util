package com.codicefun.tc.soa.util;

import com.codicefun.tc.soa.clientx.AppXSession;
import com.teamcenter.schemas.soa._2006_03.exceptions.ServiceException;
import com.teamcenter.services.strong.classification.ClassificationService;
import com.teamcenter.services.strong.classification._2007_01.Classification;
import com.teamcenter.soa.client.Connection;
import com.teamcenter.soa.client.model.ModelObject;
import com.teamcenter.soa.client.model.strong.ItemRevision;
import com.teamcenter.soa.client.model.strong.WorkspaceObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Classification Util
 * <p>
 * TODO: Use Optional
 * <p>
 * TODO: Log error message
 * <p>
 * TODO: Perfect docs
 * <p>
 * TODO: Perfect unit test
 */
public class ClassificationUtil {

    private static final Logger logger = LogManager.getLogger(ClassificationUtil.class);

    private static final Connection connection = AppXSession.getConnection();
    private static final ClassificationService clsService = ClassificationService.getService(connection);

    /**
     * Find class by class id
     */
    public static Classification.ClassDef findClassDefByClassId(String classId) throws ServiceException {
        Classification.GetClassDescriptionsResponse response = clsService.getClassDescriptions(new String[]{classId});
        if (response.data.sizeOfPartialErrors() > 0) {
            for (int i = 0; i < response.data.sizeOfPartialErrors(); i++) {
                logger.error("getClassDescriptions error: {}", response.data.getPartialError(i));
            }
            return null;
        }

        return response.descriptions.get(classId);
    }

    /**
     * Print class structure
     */
    public static void printClassStruct(String classId) throws ServiceException {
        Classification.GetChildrenResponse response = clsService.getChildren(new String[]{classId});
        if (response.data.sizeOfPartialErrors() > 0) {
            for (int i = 0; i < response.data.sizeOfPartialErrors(); i++) {
                logger.error("getChildren error: {}", response.data.getPartialError(i));
            }
            return;
        }

        Map<String, Classification.ChildDef[]> childDefMap = response.children;
        Classification.ChildDef[] childDefs = childDefMap.get(classId);
        if (childDefs == null) {
            return;
        }

        for (Classification.ChildDef childDef : childDefs) {
            logger.info("child class id = {}", childDef.id);
            logger.info("child class name = {}", childDef.name);
            printClassStruct(childDef.id);
        }
    }

    /**
     * Get all instance by class id
     */
    public static List<WorkspaceObject> getInstByClassId(String classId) throws ServiceException {
        Map<ModelObject, Classification.ClassificationObject> icoDetailMap = getIcoDetailByClassId(classId);
        if (icoDetailMap == null) {
            return null;
        }

        List<WorkspaceObject> instList = new ArrayList<>();
        icoDetailMap.values().forEach(ico -> instList.add(ico.wsoId));

        return instList;
    }

    /**
     * Get class attribute by class id
     */
    private static Map<Integer, Classification.ClassAttribute> getClsAttrIdMapByClassId(String classId) throws
            ServiceException {
        Classification.GetAttributesForClassesResponse clsAttrResponse = clsService.getAttributesForClasses(
                new String[]{classId});
        if (clsAttrResponse.data.sizeOfPartialErrors() > 0) {
            for (int i = 0; i < clsAttrResponse.data.sizeOfPartialErrors(); i++) {
                logger.error("Get class attributes error: {}", clsAttrResponse.data.getPartialError(i));
            }
            return null;
        }

        Classification.ClassAttribute[] attributes = clsAttrResponse.attributes.get(classId);
        if (attributes == null) {
            return null;
        }

        Map<Integer, Classification.ClassAttribute> clsAttrMap = new HashMap<>();
        for (Classification.ClassAttribute attribute : attributes) {
            clsAttrMap.put(attribute.id, attribute);
        }

        return clsAttrMap;
    }

    /**
     * Get ico by class id
     */
    private static ModelObject[] getIcoByClassId(String classId) throws ServiceException {
        Classification.SearchClassAttributes[] response = new Classification.SearchClassAttributes[]{new Classification.SearchClassAttributes()};
        response[0].classIds = new String[]{classId};
        Classification.SearchResponse icosResponse = clsService.search(response);
        if (icosResponse.data.sizeOfPartialErrors() > 0) {
            for (int i = 0; i < icosResponse.data.sizeOfPartialErrors(); i++) {
                logger.error("Get icos error: {}", icosResponse.data.getPartialError(i));
            }
            return null;
        }

        return icosResponse.clsObjTags.get(classId);
    }

    private static Map<ModelObject, Classification.ClassificationObject> getIcoDetailByIcos(ModelObject[] icos) throws
            ServiceException {
        Classification.GetClassificationObjectsResponse response = clsService.getClassificationObjects(icos);
        if (response.data.sizeOfPartialErrors() > 0) {
            for (int i = 0; i < response.data.sizeOfPartialErrors(); i++) {
                logger.error("Get ico details error: {}", response.data.getPartialError(i));
            }
            return null;
        }

        return response.clsObjs;
    }

    /**
     * Get ico detail by class id
     */
    private static Map<ModelObject, Classification.ClassificationObject> getIcoDetailByClassId(String classId) throws
            ServiceException {
        ModelObject[] icos = getIcoByClassId(classId);
        if (icos == null) {
            return null;
        }

        return getIcoDetailByIcos(icos);
    }

    /**
     * Get class attributes by ico properties, filter by class attr id map
     */
    private static Map<String, String> getClsAttrByIcoProps(Classification.ClassificationProperty[] icoProperties,
                                                            Map<Integer, Classification.ClassAttribute> clsAttrIdMap) {
        Map<String, String> result = new HashMap<>();

        for (Classification.ClassificationProperty icoProperty : icoProperties) {
            if (!clsAttrIdMap.containsKey(icoProperty.attributeId)) {
                continue;
            }
            Classification.ClassAttribute classAttribute = clsAttrIdMap.get(icoProperty.attributeId);
            if (classAttribute == null) {
                continue;
            }
            String attrName = classAttribute.name;
            String attrValue = "";
            if (icoProperty.values.length > 0) {
                attrValue = Arrays.stream(icoProperty.values)
                                  .map(val -> val.dbValue)
                                  .collect(Collectors.joining(","));
            }
            result.put(attrName, attrValue);
        }

        return result;
    }

    /**
     * Get class attributes by class id
     */
    public static Map<ModelObject, Map<String, String>> getClsAttrByClassId(String classId) throws
            ServiceException {
        Map<Integer, Classification.ClassAttribute> clsAttrMap = getClsAttrIdMapByClassId(classId);
        if (clsAttrMap == null) {
            return null;
        }

        Map<ModelObject, Classification.ClassificationObject> icoDetailMap = getIcoDetailByClassId(classId);
        if (icoDetailMap == null) {
            return null;
        }

        Map<ModelObject, Map<String, String>> result = new HashMap<>();
        icoDetailMap.forEach((k, v) -> {
            Map<String, String> attrValueMap = getClsAttrByIcoProps(v.properties, clsAttrMap);
            result.put(v.wsoId, attrValueMap);
        });

        return result;
    }

    /**
     * Get class attributes by item revision
     */
    public static Map<String, String> getClsAttrByItemRev(ItemRevision itemRevision) throws ServiceException {
        Classification.FindClassificationObjectsResponse response = clsService.findClassificationObjects(
                new WorkspaceObject[]{itemRevision});
        if (response.data.sizeOfPartialErrors() > 0) {
            for (int i = 0; i < response.data.sizeOfPartialErrors(); i++) {
                logger.error("Find classification objects error: {}", response.data.getPartialError(i));
            }
            return null;
        }

        Map<WorkspaceObject, ModelObject[]> icosMap = response.icos;
        if (icosMap == null) {
            return null;
        }

        ModelObject[] icos = icosMap.get(itemRevision);
        if (icos == null) {
            return null;
        }

        Map<ModelObject, Classification.ClassificationObject> icoDetailMap = getIcoDetailByIcos(icos);
        if (icoDetailMap == null) {
            return null;
        }

        Classification.ClassificationObject icoDetail = icoDetailMap.get(icos[0]);
        if (icoDetail == null) {
            return null;
        }

        Map<Integer, Classification.ClassAttribute> clsAttrMap = getClsAttrIdMapByClassId(icoDetail.classId);
        if (clsAttrMap == null) {
            return null;
        }

        return getClsAttrByIcoProps(icoDetail.properties, clsAttrMap);
    }

}
