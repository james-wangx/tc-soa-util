package com.codicefun.tc.soa.util;

import com.codicefun.tc.soa.clientx.AppXSession;
import com.teamcenter.services.strong.core.DataManagementService;
import com.teamcenter.services.strong.core._2006_03.DataManagement.*;
import com.teamcenter.services.strong.core._2007_01.DataManagement.GetItemFromIdPref;
import com.teamcenter.services.strong.core._2008_06.DataManagement.*;
import com.teamcenter.services.strong.core._2009_10.DataManagement.GetItemFromAttributeInfo;
import com.teamcenter.services.strong.core._2009_10.DataManagement.GetItemFromAttributeResponse;
import com.teamcenter.services.strong.core._2010_09.DataManagement.NameValueStruct1;
import com.teamcenter.services.strong.core._2010_09.DataManagement.PropInfo;
import com.teamcenter.services.strong.core._2010_09.DataManagement.SetPropertyResponse;
import com.teamcenter.services.strong.core._2015_07.DataManagement.CreateIn2;
import com.teamcenter.soa.client.Connection;
import com.teamcenter.soa.client.model.ModelObject;
import com.teamcenter.soa.client.model.ServiceData;
import com.teamcenter.soa.client.model.strong.Folder;
import com.teamcenter.soa.client.model.strong.Item;
import com.teamcenter.soa.client.model.strong.ItemRevision;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

/**
 * Data management util
 */
public class DataManagementUtil {

    private static final Connection connection = AppXSession.getConnection();
    private static final DataManagementService dmService = DataManagementService.getService(connection);

    /**
     * Refresh model object
     *
     * @param mo the model object
     * @return true if successful, otherwise false
     */
    public static boolean refreshMo(ModelObject mo) {
        ServiceData serviceData = dmService.refreshObjects(new ModelObject[]{mo});

        return !ServiceUtil.catchPartialErrors(serviceData);
    }

    /**
     * Get property from tc
     *
     * @param mo       the model object
     * @param propName the property name
     * @return true if successful, otherwise false
     */
    public static boolean getProperty(ModelObject mo, String propName) {
        ServiceData serviceData = dmService.getProperties(new ModelObject[]{mo}, new String[]{propName});

        return !ServiceUtil.catchPartialErrors(serviceData);
    }

    /**
     * Find model object by uid
     *
     * @param uid target model object's uid
     * @return the result model object
     */
    public static Optional<ModelObject> findMoByUid(String uid) {
        ServiceData serviceData = dmService.loadObjects(new String[]{uid});

        if (ServiceUtil.catchPartialErrors(serviceData) || serviceData.sizeOfPlainObjects() == 0) {
            return Optional.empty();
        }

        return Optional.ofNullable(serviceData.getPlainObject(serviceData.sizeOfPlainObjects() - 1));
    }

    /**
     * Get naming rules
     *
     * @param typeName the type name
     * @param propName the property name
     * @return the naming rules
     */
    public static Optional<String[]> getNamingRules(String typeName, String propName) {
        NRAttachInfo nrAttachInfo = new NRAttachInfo();
        nrAttachInfo.typeName = typeName;
        nrAttachInfo.propName = propName;
        GetNRPatternsWithCountersResponse response = dmService.getNRPatternsWithCounters(
                new NRAttachInfo[]{nrAttachInfo});
        if (ServiceUtil.catchPartialErrors(response.serviceData)
            || response.patterns == null
            || response.patterns.length == 0) {
            return Optional.empty();
        }

        return Optional.ofNullable(response.patterns[0].patternStrings);
    }

    /**
     * Get next id
     *
     * @param typeName the type name
     * @param propName the property name
     * @param pattern  the pattern
     * @return the next id
     */
    public static Optional<String> getNextId(String typeName, String propName, String pattern) {
        InfoForNextId infoForNextId = new InfoForNextId();
        infoForNextId.pattern = pattern;
        infoForNextId.propName = propName;
        infoForNextId.typeName = typeName;
        GetNextIdsResponse response = dmService.getNextIds(new InfoForNextId[]{infoForNextId});
        if (ServiceUtil.catchPartialErrors(response.serviceData)
            || response.nextIds == null
            || response.nextIds.length == 0) {
            return Optional.empty();
        }

        return Optional.ofNullable(response.nextIds[0]);
    }

    /**
     * Creates an item with the specified properties and adds it to the given folder.
     *
     * @param type   Type of the Item to be created, optional, default is Item
     * @param id     ID of the Item to be created, optional
     * @param name   Name of the Item to be created, optional
     * @param desc   Description of the Item to be created, optional
     * @param folder the folder to which the item will be added
     * @return an Optional containing the created item if successful, otherwise an empty Optional
     */
    public static Optional<Item> createItem(String type, String id, String name, String desc, Folder folder) {
        ItemProperties itemProperties = new ItemProperties();
        itemProperties.type = type;
        itemProperties.itemId = id;
        itemProperties.name = name;
        itemProperties.description = desc;
        CreateItemsResponse response = dmService.createItems(new ItemProperties[]{itemProperties}, folder, "contents");
        if (ServiceUtil.catchPartialErrors(response.serviceData)
            || response.output == null
            || response.output.length == 0) {
            return Optional.empty();
        }

        return Optional.ofNullable(response.output[0].item);
    }

    /**
     * Get item from id
     *
     * @param itemId the item id
     * @return the item
     */
    public static Optional<Item> getItemFromId(String itemId) {
        GetItemFromAttributeInfo[] infos = new GetItemFromAttributeInfo[1];
        infos[0] = new GetItemFromAttributeInfo();
        infos[0].itemAttributes.put("item_id", itemId);
        GetItemFromIdPref pref = new GetItemFromIdPref();
        GetItemFromAttributeResponse response = dmService.getItemFromAttribute(infos, -1, pref);
        if (ServiceUtil.catchPartialErrors(response.serviceData)
            || response.output == null
            || response.output.length == 0) {
            return Optional.empty();
        }

        return Optional.ofNullable(response.output[0].item);
    }

    /**
     * Set properties
     *
     * @param obj     the model object
     * @param propMap the property map
     * @return true if successful, otherwise false
     */
    public static boolean setProperties(ModelObject obj, Map<String, String> propMap) {
        PropInfo[] infos = new PropInfo[1];
        infos[0] = new PropInfo();
        infos[0].object = obj;
        NameValueStruct1[] structs = new NameValueStruct1[propMap.size()];
        int i = 0;
        for (Entry<String, String> entry : propMap.entrySet()) {
            NameValueStruct1 struct = new NameValueStruct1();
            struct.name = entry.getKey();
            struct.values = new String[]{entry.getValue()};
            structs[i++] = struct;
        }
        infos[0].vecNameVal = structs;

        SetPropertyResponse response = dmService.setProperties(infos, new String[]{});

        return !ServiceUtil.catchPartialErrors(response.data);
    }

    /**
     * Get latest item revision
     *
     * @param item the item
     * @return the latest item revision
     */
    public static Optional<ItemRevision> getLatestItemRevision(Item item) {
        ModelObject[] revisions = ModelObjectUtil.getPropArrayValues(item, "revision_list").orElse(new ModelObject[0]);
        if (revisions.length == 0) {
            return Optional.empty();
        }

        return Optional.ofNullable((ItemRevision) revisions[revisions.length - 1]);
    }

    /**
     * Create folder by parent folder object and new folder name
     *
     * @param parent parent folder object
     * @param name   new folder name
     * @return new folder
     */
    public static Optional<Folder> createFolder(Folder parent, String name) {
        CreateFolderInput[] inputs = new CreateFolderInput[1];
        inputs[0] = new CreateFolderInput();
        inputs[0].name = name;
        CreateFoldersResponse response = dmService.createFolders(inputs, parent, "");

        if (ServiceUtil.catchPartialErrors(response.serviceData) ||
            response.output == null ||
            response.output.length == 0) {
            return Optional.empty();
        }

        return Optional.ofNullable(response.output[0].folder);
    }

    /**
     * Create object and relate to parent object
     *
     * @param parent  the parent object to be related
     * @param type    the new object type
     * @param propMap the property map
     * @return the new object
     */
    public static Optional<ModelObject> createObject(ModelObject parent, String type, Map<String, String[]> propMap) {
        CreateIn2[] inputs = new CreateIn2[1];
        inputs[0] = new CreateIn2();
        inputs[0].targetObject = parent;
        inputs[0].createData.boName = type;
        inputs[0].createData.propertyNameValues = propMap;
        CreateResponse response = dmService.createRelateAndSubmitObjects2(inputs);
        if (ServiceUtil.catchPartialErrors(response.serviceData) ||
            response.output == null ||
            response.output.length == 0 ||
            response.output[0].objects == null ||
            response.output[0].objects.length == 0) {
            return Optional.empty();
        }

        return Optional.ofNullable(response.output[0].objects[0]);
    }

    /**
     * Copy a model object to a folder
     *
     * @param from the model object
     * @param to   the folder
     * @return success or failed
     */
    public static boolean copy(ModelObject from, Folder to) {
        Relationship[] inputs = new Relationship[1];
        inputs[0] = new Relationship();
        inputs[0].primaryObject = to;
        inputs[0].secondaryObject = from;
        inputs[0].relationType = "contents";
        CreateRelationsResponse response = dmService.createRelations(inputs);

        return !ServiceUtil.catchPartialErrors(response.serviceData);
    }

    /**
     * Delete a model object
     *
     * @param obj the model object
     * @return success or failed
     */
    public static boolean deleteObject(ModelObject obj) {
        ServiceData serviceData = dmService.deleteObjects(new ModelObject[]{obj});

        return !ServiceUtil.catchPartialErrors(serviceData);
    }

}
