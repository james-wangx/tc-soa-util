package com.xcplm.tc.soa.util;

import com.teamcenter.soa.client.model.ModelObject;
import com.xcplm.tc.soa.exception.SoaConnException;
import com.xcplm.tc.soa.exception.TestException;
import org.junit.jupiter.api.Test;

public class SessionTest extends UtilTest {

    @Test
    void reconnect() throws Exception {

        RetryUtil.executeWithRetry(() -> {
            tcUtil.login("00001", "00001", "zh_CN", "tc-soa-util-test");
            while (true) {
                ModelObject rev = tcUtil.findMoByUid("wDulNxk8o0c12D")
                                        .orElseThrow(() -> new TestException("Failed to find MO by UID"));
                String itemId = tcUtil.getPropStringValue(rev, "item_id").get();
                System.out.println("itemId = " + itemId);
                try {
                    Thread.sleep(1000 * 2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, -1, 1, SoaConnException.class);

    }

}
