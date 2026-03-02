package com.xcplm.tc.soa.util;

import com.teamcenter.schemas.soa._2006_03.exceptions.ServiceException;
import com.teamcenter.services.strong.core.PropDescriptorService;
import com.teamcenter.services.strong.core._2007_06.PropDescriptor.PropDescInfo;
import com.teamcenter.services.strong.core._2011_06.PropDescriptor.AttachedPropDescsResponse;
import com.teamcenter.services.strong.core._2011_06.PropDescriptor.PropDescOutput2;
import com.teamcenter.soa.client.Connection;
import com.teamcenter.soa.client.model.strong.Fnd0ListOfValuesDynamic;
import com.teamcenter.soa.exceptions.NotLoadedException;
import com.xcplm.tc.soa.clientx.AppXSession;

import java.util.Arrays;

public class PropDescriptorUtil {

    private static final Connection connection = AppXSession.getConnection();
    private static final PropDescriptorService pdService = PropDescriptorService.getService(connection);

    public static void getAttachedPropDesc(String typeName, String propName) {
        PropDescInfo[] infos = new PropDescInfo[1];
        infos[0] = new PropDescInfo();
        infos[0].typeName = typeName;
        infos[0].propNames = new String[]{propName};

        try {
            AttachedPropDescsResponse response = pdService.getAttachedPropDescs2(infos);
            if (ServiceUtil.catchPartialErrors(response.serviceData) ||
                response.inputTypeNameToPropDescOutput == null ||
                response.inputTypeNameToPropDescOutput.get(typeName) == null ||
                response.inputTypeNameToPropDescOutput.get(typeName).length == 0) {
                return;
            }
            PropDescOutput2 output = response.inputTypeNameToPropDescOutput.get(typeName)[0];
            Fnd0ListOfValuesDynamic dlov = (Fnd0ListOfValuesDynamic) output.propertyDesc.lov;
            // TcUtil.getProperties(dlov,
            //                                  new String[]{"fnd0query_clause", "fnd0filter_attributes", "fnd0lov_desc",
            //                                               "fnd0query_type", "fnd0lov_value"});
            String[] queryClause = dlov.get_fnd0query_clause();
            String[] filterAttributes = dlov.get_fnd0filter_attributes();
            String fnd0lovValue = dlov.get_fnd0lov_value();
            String lovDesc = dlov.get_fnd0lov_desc();
            String queryType = dlov.get_fnd0query_type();
            System.out.println("queryClause = " + Arrays.toString(queryClause));
            System.out.println("filterAttributes = " + Arrays.toString(filterAttributes));
            System.out.println("fnd0lovValue = " + fnd0lovValue);
            System.out.println("lovDesc = " + lovDesc);
            System.out.println("queryType = " + queryType);
        } catch (ServiceException | NotLoadedException e) {
            throw new RuntimeException(e);
        }
    }

}
