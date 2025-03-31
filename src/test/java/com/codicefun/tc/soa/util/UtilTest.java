package com.codicefun.tc.soa.util;

import com.codicefun.tc.soa.clientx.AppXSession;
import com.teamcenter.soa.client.Connection;
import org.junit.jupiter.api.BeforeAll;

class UtilTest {

    static Connection connection;

    @BeforeAll
    static void init() {
        SessionUtil.login("http://192.168.80.101:8888/tc", "james", "james", "tc-soa-util-test");
        connection = AppXSession.getConnection();
    }

}
