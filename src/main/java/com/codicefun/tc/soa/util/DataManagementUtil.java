package com.codicefun.tc.soa.util;

import com.codicefun.tc.soa.clientx.AppXSession;
import com.teamcenter.services.strong.core.DataManagementService;
import com.teamcenter.services.strong.core._2006_03.DataManagement.CreateItemsResponse;
import com.teamcenter.services.strong.core._2006_03.DataManagement.ItemProperties;
import com.teamcenter.services.strong.core._2008_06.DataManagement.GetNRPatternsWithCountersResponse;
import com.teamcenter.services.strong.core._2008_06.DataManagement.GetNextIdsResponse;
import com.teamcenter.services.strong.core._2008_06.DataManagement.InfoForNextId;
import com.teamcenter.services.strong.core._2008_06.DataManagement.NRAttachInfo;
import com.teamcenter.soa.client.Connection;
import com.teamcenter.soa.client.model.ModelObject;
import com.teamcenter.soa.client.model.ServiceData;
import com.teamcenter.soa.client.model.strong.Folder;
import com.teamcenter.soa.client.model.strong.Item;

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

}
