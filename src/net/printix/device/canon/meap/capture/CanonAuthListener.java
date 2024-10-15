//------------------------------------------------------------------------------
// Copyright (c) 2002-2021 Kofax. All rights reserved.
// Description : CanonAuthListener
//------------------------------------------------------------------------------

package net.printix.device.canon.meap.capture;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.Value;
import com.canon.meap.security.LoginContext;
import com.canon.meap.service.login.event.UserEvent;
import com.canon.meap.service.login.event.UserEventListener;
import net.printix.device.canon.meap.capture.log.Logger;


public class CanonAuthListener implements UserEventListener {
    private static final String FAKE_USER_NAME = "-----";
    private static final String FAKE_USER_DEPT_ID = "0000000";
    private static final String CANON_ADMIN_USER_NAME = "Administrator";
    private static final String CANON_ADMIN_DEPT_ID = "7654321";
    private IServiceFinder serviceFinder;
    public CanonAuthListener(IServiceFinder serviceFinder) {
        this.serviceFinder = serviceFinder;
    }

    @Value
    @EqualsAndHashCode
    private static class CanonIdentity {
        final String userName;
        final String deptId;
    }

    private static final Set<CanonIdentity> IGNORED_USERS = new HashSet<>(Arrays.asList(new CanonIdentity[] {
            new CanonIdentity(FAKE_USER_NAME, FAKE_USER_DEPT_ID), new CanonIdentity(CANON_ADMIN_USER_NAME, CANON_ADMIN_DEPT_ID) }));
    private boolean userLoggedIn = false;

    @Override
    public void login(final UserEvent ue) {
        LoginContext loginContext = ue.getLoginContext();
        if (loginContext != null) {
            Logger.d("Login event: " + loginContext.getClass().getCanonicalName());
            if (validateLoginContext(loginContext)) {
                userLoggedIn = true;
            } else {
                String userName = loginContext.getUserName();
                String departmentId = loginContext.getUserAttribute("canonUid");
                Logger.d("Ignoring invalid login event: " + userName + ", " + departmentId);
            }
        }
    }

    private static boolean validateLoginContext(final LoginContext loginContext) {
        String userName = loginContext.getUserName();
        String departmentId = loginContext.getUserAttribute("canonUid");

        CanonIdentity canonUser = new CanonIdentity(userName, departmentId);
        return !IGNORED_USERS.contains(canonUser) && !FAKE_USER_NAME.equals(userName);
    }

    @Override
    public void logout(final UserEvent ue) {
        LoginContext loginContext = ue.getLoginContext();
        if (loginContext != null) {
            Logger.d("Logout event: " + loginContext.getClass().getCanonicalName());

            if (validateLoginContext(loginContext) && userLoggedIn) {
                Logger.d("Logout: User Attributes: " + dumpLoginContext(loginContext));
            } else {
                String userName = loginContext.getUserName();
                String departmentId = loginContext.getUserAttribute("canonUid");
                Logger.d("Ignoring invalid logout event: " + userName + ", " + departmentId);
            }

            userLoggedIn = false;
        }
    }

    private static StringBuffer dumpLoginContext(LoginContext loginContext) {
        StringBuffer sb = new StringBuffer();
        String identificationName = loginContext.getUserAttribute("dn"); // Identification Name
        String loginName = loginContext.getUserAttribute("uid"); // Login Name
        String domainName = loginContext.getUserAttribute("dc"); // Domain Name
        String deptId = loginContext.getUserAttribute("canonUid"); // Department Id
        String userName = loginContext.getUserAttribute("cn"); // User Name
        String read = loginContext.getUserAttribute("cn;lang-ja;phonetic"); // Read
        String email = loginContext.getUserAttribute("mail"); // e-mail Address
        sb.append("identificationName: ");
        sb.append(identificationName);
        sb.append("\nloginName: ");
        sb.append(loginName);
        sb.append("\ndomainName: ");
        sb.append(domainName);
        sb.append("\ndeptId: ");
        sb.append(deptId);
        sb.append("\nuserName: ");
        sb.append(userName);
        sb.append("\nread: ");
        sb.append(read);
        sb.append("\nemail: ");
        sb.append(email);
        return sb;
    }
}