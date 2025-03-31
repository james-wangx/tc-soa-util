package com.codicefun.tc.soa.util;

import com.codicefun.tc.soa.clientx.AppXSession;
import com.teamcenter.soa.client.Connection;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

@Slf4j
class UtilTest {

    static Connection connection;

    @BeforeAll
    static void init() {
        SessionUtil.login("http://192.168.80.101:8888/tc", "james", "james", "tc-soa-util-test");
        connection = AppXSession.getConnection();
    }

    @AfterAll
    static void done() {
        SessionUtil.logout();
    }

}
