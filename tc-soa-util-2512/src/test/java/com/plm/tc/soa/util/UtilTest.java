package com.plm.tc.soa.util;

import com.plm.tc.soa.clientx.AppXSession;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

@Slf4j
class UtilTest {

    static AppXSession session;

    static TcUtil tcUtil;

    @BeforeAll
    static void init() {
        // session = new AppXSession("http://192.168.80.107:7001/tc");
        session = new AppXSession("http://tc2412:7001/tc");
        // Login use tccs
        // SessionUtil.login("tccs://TcWeb1", "james", "james", "zh_CN", "tc-soa-util-test");
        tcUtil = new TcUtil(session.getConnection());
        tcUtil.login("james", "james", "zh_CN", "tc-soa-util-test");
    }

    @AfterAll
    static void done() {
        tcUtil.logout();
    }

}
