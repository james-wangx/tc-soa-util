package com.codicefun.tc.soa.util;

import com.codicefun.tc.soa.clientx.AppXSession;
import com.codicefun.tc.soa.exception.SoaUtilException;
import com.teamcenter.schemas.soa._2006_03.exceptions.ServiceException;
import com.teamcenter.services.strong.core.SessionService;
import com.teamcenter.services.strong.core._2006_03.Session;
import com.teamcenter.services.strong.core._2007_01.Session.GetTCSessionInfoResponse;
import com.teamcenter.services.strong.core._2007_12.Session.StateNameValue;
import com.teamcenter.soa.client.Connection;
import com.teamcenter.soa.client.model.Property;
import com.teamcenter.soa.client.model.ServiceData;
import com.teamcenter.soa.client.model.strong.Folder;
import com.teamcenter.soa.client.model.strong.User;
import com.teamcenter.soa.exceptions.NotLoadedException;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
public class SessionUtil {

    private static SessionService sessionService;

    private static GetTCSessionInfoResponse sessionInfoResponse;

    public static boolean login(String host, String username, String password, String sessionDiscriminator) {
        try {
            new AppXSession(host);
            Connection connection = AppXSession.getConnection();
            sessionService = SessionService.getService(connection);
            Session.LoginResponse response = sessionService.login(username, password, "", "", "", sessionDiscriminator);
            User user = response.user;
            log.info("Login with user {}", user.get_user_name());
            return true;
        } catch (Exception e) {
            log.error("Login failed", e);
            return false;
        }
    }

    public static Optional<GetTCSessionInfoResponse> getSessionInfo() {
        try {
            GetTCSessionInfoResponse response = sessionService.getTCSessionInfo();
            if (ServiceUtil.catchPartialErrors(response.serviceData)) {
                return Optional.empty();
            }
            sessionInfoResponse = response;
            return Optional.of(response);
        } catch (ServiceException e) {
            log.error("Catch Exception: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    public static void setBypass(boolean bypass) {
        StateNameValue[] stateNameValues = new StateNameValue[1];
        stateNameValues[0] = new StateNameValue();
        stateNameValues[0].name = "bypassFlag";
        stateNameValues[0].value = Property.toBooleanString(bypass);
        sessionService.setUserSessionState(stateNameValues);
    }

    public static boolean logout() {
        try {
            ServiceData serviceData = sessionService.logout();
            if (ServiceUtil.catchPartialErrors(serviceData)) {
                return false;
            }
            log.info("Logout successful");
            return true;
        } catch (ServiceException e) {
            log.error("Logout failed: {}", e.getMessage(), e);
            return false;
        }
    }

    public static Optional<User> getUser() {
        try {
            if (sessionInfoResponse == null) {
                sessionInfoResponse = getSessionInfo().orElseThrow(
                        () -> new SoaUtilException("Failed to retrieve session info"));
            }

            return Optional.ofNullable(sessionInfoResponse.user);
        } catch (SoaUtilException e) {
            log.error(e.getMessage(), e);
            return Optional.empty();
        }
    }

    public static Optional<Folder> getHomeFolder() {
        try {
            User user = getUser().orElseThrow(() -> new SoaUtilException("Failed to retrieve user"));
            Folder homeFolder = user.get_home_folder();

            return Optional.ofNullable(homeFolder);
        } catch (NotLoadedException | SoaUtilException e) {
            log.error(e.getMessage(), e);
            return Optional.empty();
        }
    }

}
