//------------------------------------------------------------------------------
// Copyright (c) 2002-2024 Tungsten Automation. All rights reserved.
// Description: AppConstants
//------------------------------------------------------------------------------

package net.printix.device.canon.meap.capture.settings;

public class AppConstants {
    public static final String APPLET_NAME = "PrintixCaptureEmbeddedApp";
    public static final String APPLET_TITLE = "Capture";
    public static final String CAPTURE_CONFIG_KEY = "PRINTIX_CAPTURE_CONFIG";
    public static final String PRINT_SERVLET_PATH = "/printixscan";
    public static final String PRINT_RESOURCES_PATH = PRINT_SERVLET_PATH + "/webview";
    public final static String URL_DENIED = "http://127.0.0.1:8000/printixscan/webview/permissiondeniedpage.html";
    public final static String GENERAL = "general user";
    public static final String ADMIN_CONFIG_KEY = "PRINTIX_LOGIN_ADMIN";
    public static final String USER_NAME = "username";
    public static final String PASSWORD = "password";
    public static final String DEFAULT_ADMIN_USERNAME = "8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918"; //admin hash
    public static final String DEFAULT_ADMIN_PASSWORD = "84d1480d9a6902a7229d3f895e3b453f76e4a9e974e2d975ee51ec8534af08e3"; //Printix hash
    public static final String AUTHORIZATION = "Authorization";
    public static final String PRINTIX_SCAN_CONFIG = "https://127.0.0.1:8443/printixcapture";
    public static final String PRINTIX_SCAN_CONFIG_CHANGE_PASS = "changePass";

    public static final String LOADING_TEXT = "LOADING_TEXT";
    public static final String PRINTIX = "PRINTIX";
    public final static String URL_NO_CONFIG = "http://127.0.0.1:8000/printixcapture/webview/noconfigurationpage.html";

    public static final String ICONLABEL_TEXT = "ICONLABEL_TEXT";
    public static final String MEAP_CAPTURE = "[MeapCapture] ";
}
