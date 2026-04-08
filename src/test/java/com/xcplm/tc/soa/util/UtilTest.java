package com.xcplm.tc.soa.util;

import com.xcplm.tc.soa.clientx.AppXSession;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

@Slf4j
class UtilTest {

    static AppXSession session;

    static TcUtil tcUtil;

    @BeforeAll
    static void init() {
        session = new AppXSession("http://192.168.80.105:8888/tc");
        // SessionUtil.login("http://172.168.10.101:7001/tc", "james", "james", "zh_CN", "tc-soa-util-test");
        // SessionUtil.login("http://192.168.80.101:7001/tc", "james", "james", "zh_CN", "tc-soa-util-test");
        // Login use tccs
        // SessionUtil.login("tccs://TcWeb1", "james", "james", "zh_CN", "tc-soa-util-test");
        tcUtil = new TcUtil(session.getConnection());
        // tcUtil.login("00001", "00001", "zh_CN", "tc-soa-util-test");
    }

    @AfterAll
    static void done() {
        tcUtil.logout();
    }

}
