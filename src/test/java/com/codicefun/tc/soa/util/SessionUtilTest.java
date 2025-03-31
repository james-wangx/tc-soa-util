package com.codicefun.tc.soa.util;

import com.teamcenter.soa.client.model.strong.Folder;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SessionUtilTest extends UtilTest {

    @Test
    void getHomeFolder() {
        Optional<Folder> homeFolder = SessionUtil.getHomeFolder();

        assertTrue(homeFolder.isPresent());
    }

}
