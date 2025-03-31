package com.codicefun.tc.soa.util;

import com.codicefun.tc.soa.clientx.AppXSession;
import com.teamcenter.services.strong.structuremanagement.StructureService;
import com.teamcenter.services.strong.structuremanagement._2012_09.Structure.AddInformation;
import com.teamcenter.services.strong.structuremanagement._2012_09.Structure.AddParam;
import com.teamcenter.services.strong.structuremanagement._2012_09.Structure.AddResponse;
import com.teamcenter.soa.client.Connection;
import com.teamcenter.soa.client.model.strong.BOMLine;
import com.teamcenter.soa.client.model.strong.ItemRevision;

import java.util.Optional;

public class StructureUtil {

    private static final Connection connection = AppXSession.getConnection();
    private static final StructureService structureService = StructureService.getService(connection);

    public static Optional<BOMLine> addChild(BOMLine bomLine, ItemRevision itemRevision) {
        AddParam[] addParams = new AddParam[1];
        addParams[0] = new AddParam();
        addParams[0].parent = bomLine;
        addParams[0].toBeAdded = new AddInformation[1];
        addParams[0].toBeAdded[0] = new AddInformation();
        addParams[0].toBeAdded[0].itemRev = itemRevision;
        AddResponse response = structureService.add(addParams);
        if (ServiceUtil.catchPartialErrors(response.serviceData) ||
            response.addedLines == null ||
            response.addedLines.length == 0) {
            return Optional.empty();
        }

        return Optional.ofNullable(response.addedLines[0]);
    }

}
