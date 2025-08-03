package com.codicefun.tc.soa.util;

import com.codicefun.tc.soa.clientx.AppXSession;
import com.codicefun.tc.soa.exception.TestException;
import com.teamcenter.soa.client.Connection;
import com.teamcenter.soa.client.model.ModelObject;
import com.teamcenter.soa.client.model.strong.ImanQuery;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SavedQueryUtilTest {

    static Connection connection;

    @BeforeAll
    static void init() {
        SessionUtil.login("http://192.168.80.101:8888/tc", "00001", "00001", "zh_CN", "tc-soa-util-test");
        connection = AppXSession.getConnection();
    }

    @Test
    void getSavedQueryByName() {
        ImanQuery query = SavedQueryUtil.getSavedQueryByName("__query_metering")
                                        .orElseThrow(() -> new TestException("Not found saved query"));
        assertNotNull(query);
    }

    @Test
    void query() {
        String[] entries = {"Metering Id"};
        String[] values = {"64498666"};
        ModelObject obj = SavedQueryUtil.queryOneObject("__query_metering", entries, values)
                                        .orElseThrow(() -> new TestException("Not found metering"));
        assertNotNull(obj);
    }

    @Test
    void query_WithNotExistQuery_ShouldNotPresent() {
        String[] entries = {"Metering Id"};
        String[] values = {"64498666"};

        Optional<ModelObject> queryMetering = SavedQueryUtil.queryOneObject("__query_metering2", entries, values);

        assertFalse(queryMetering.isPresent());
    }

    @Test
    void query_WithErrorId_ShouldNotPresent() {
        String[] entries = {"Metering Id"};
        String[] values = {"errorId"};

        Optional<ModelObject> queryMetering = SavedQueryUtil.queryOneObject("__query_metering", entries, values);

        assertFalse(queryMetering.isPresent());
    }

}
