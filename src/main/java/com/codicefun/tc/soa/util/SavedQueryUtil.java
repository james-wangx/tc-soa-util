package com.codicefun.tc.soa.util;

import com.codicefun.tc.soa.clientx.AppXSession;
import com.codicefun.tc.soa.exception.SoaUtilException;
import com.teamcenter.schemas.soa._2006_03.exceptions.ServiceException;
import com.teamcenter.services.strong.query.SavedQueryService;
import com.teamcenter.services.strong.query._2006_03.SavedQuery.GetSavedQueriesResponse;
import com.teamcenter.services.strong.query._2006_03.SavedQuery.SavedQueryObject;
import com.teamcenter.services.strong.query._2007_09.SavedQuery.SavedQueriesResponse;
import com.teamcenter.services.strong.query._2008_06.SavedQuery.QueryInput;
import com.teamcenter.soa.client.Connection;
import com.teamcenter.soa.client.model.ModelObject;
import com.teamcenter.soa.client.model.strong.ImanQuery;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
public class SavedQueryUtil {

    private static final Connection connection = AppXSession.getConnection();
    private static final SavedQueryService sqService = SavedQueryService.getService(connection);

    public static Optional<ImanQuery> getSavedQueryByName(String name) {
        try {
            GetSavedQueriesResponse response = sqService.getSavedQueries();
            if (ServiceUtil.catchPartialErrors(response.serviceData) || response.serviceData == null) {
                return Optional.empty();
            }

            for (SavedQueryObject query : response.queries) {
                if (query.name.equals(name)) {
                    return Optional.ofNullable(query.query);
                }
            }

            return Optional.empty();
        } catch (ServiceException e) {
            return Optional.empty();
        }
    }

    public static Optional<ModelObject[]> queryAll(String name, String[] entries, String[] values) {
        try {
            ImanQuery query = getSavedQueryByName(name).orElseThrow(
                    () -> new SoaUtilException("Not found saved query by name: " + name));

            QueryInput[] inputs = new QueryInput[1];
            inputs[0] = new QueryInput();
            inputs[0].query = query;
            inputs[0].entries = entries;
            inputs[0].values = values;
            SavedQueriesResponse response = sqService.executeSavedQueries(inputs);
            if (ServiceUtil.catchPartialErrors(response.serviceData) ||
                response.arrayOfResults == null ||
                response.arrayOfResults.length == 0) {
                return Optional.empty();
            }

            String[] objectUIDS = response.arrayOfResults[0].objectUIDS;
            if (objectUIDS == null || objectUIDS.length == 0) {
                return Optional.empty();
            }

            return DataManagementUtil.findMosByUids(objectUIDS);
        } catch (SoaUtilException e) {
            log.error(e.getMessage(), e);
            return Optional.empty();
        }
    }

    public static Optional<ModelObject> queryOneObject(String name, String[] entries, String[] values) {
        try {
            ModelObject[] results = queryAll(name, entries, values).orElseThrow(
                    () -> new SoaUtilException("Not found any result"));
            return Optional.ofNullable(results[0]);
        } catch (SoaUtilException e) {
            log.error(e.getMessage(), e);
            return Optional.empty();
        }
    }

}
