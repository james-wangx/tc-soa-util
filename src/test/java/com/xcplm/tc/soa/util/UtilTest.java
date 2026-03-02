package com.xcplm.tc.soa.util;

import com.teamcenter.soa.client.Connection;
import com.xcplm.tc.soa.clientx.AppXSession;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

@Slf4j
class UtilTest {

    static Connection connection;

    static TcUtil tcUtil;

    @BeforeAll
    static void init() {
        SessionUtil.login("http://192.168.80.105:8888/tc", "00001", "00001", "zh_CN", "tc-soa-util-test");
        // SessionUtil.login("http://172.168.10.101:7001/tc", "james", "james", "zh_CN", "tc-soa-util-test");
        // SessionUtil.login("http://192.168.80.101:7001/tc", "james", "james", "zh_CN", "tc-soa-util-test");
        // Login use tccs
        // SessionUtil.login("tccs://TcWeb1", "james", "james", "zh_CN", "tc-soa-util-test");
        connection = AppXSession.getConnection();
        tcUtil = new TcUtil(connection);
    }

    @AfterAll
    static void done() {
        SessionUtil.logout();
    }

}
