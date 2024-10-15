//------------------------------------------------------------------------------
// Copyright (c) 2002-2025 Tungsten Automation. All rights reserved.
// Description: ScanBundleActivator
//------------------------------------------------------------------------------

package net.printix.device.canon.meap.capture;


import com.canon.meap.security.LoginContext;
import com.canon.meap.service.avs.IconLabelData;
import com.canon.meap.service.log.LogService;
import com.canon.meap.service.login.LocalLoginService;
import com.canon.meap.service.login.event.UserEvent;
import com.canon.meap.service.login.event.UserEventListener;
import java.awt.Image;
import java.io.IOException;
import java.net.URL;
import java.security.AccessControlException;
import java.util.Locale;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.Getter;
import net.printix.device.canon.meap.capture.log.LogServiceAccessor;
import net.printix.device.canon.meap.capture.log.Logger;
import net.printix.device.canon.meap.capture.settings.AdminSettingsProvider;
import net.printix.device.canon.meap.capture.settings.SettingsProvider;
import net.printix.device.canon.meap.capture.util.Util;
import net.printix.device.canon.meap.capture.servlet.CaptureConfigServlet;
import net.printix.device.canon.meap.capture.settingmanager.CaptureSettingsManager;
import net.printix.device.canon.meap.capture.settings.AppConstants;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.NamespaceException;

import com.canon.meap.service.avs.AppletContext;
import com.canon.meap.service.avs.AppletViewerService;
import com.canon.meap.service.avs.Icon;
import com.canon.meap.service.avs.IconImageData;
import com.canon.meap.service.conf.ConfigurationService;
import com.canon.meap.service.http.HttpExtendedService;

// Canon Printix Scan Bundle Activator
public class CaptureBundleActivator implements BundleActivator, IServiceFinder {
    private static final String TAG = CaptureBundleActivator.class.getSimpleName();
    @Getter
    private BundleContext bundleContext;
    private ServiceReference appletViewerServiceReference;
    private ServiceReference httpsServiceReference;
    private AppletViewerService appletViewerService;
    @Getter
    private ConfigurationService configurationService;
    @Getter
    private HttpExtendedService httpExtendedService;
    private Image image_32 = null;
    private Image image_48 = null;
    private Image image_72 = null;
    private Image image_110 = null;
    private boolean isNiR = false;
    private CaptureApplet applet = null;
    private CaptureConfigServlet configServlet = null;
    @Getter
    private CaptureSettingsManager scanSettingsManager;
    @Getter
    private AdminSettingsProvider adminSettingsProvider;
    @Getter
    private SettingsProvider settingsProvider;
    @Getter
    private static LoginContext loginContext;
    @Getter
    private CanonAuthListener canonAuthListener;
    private String[] languageCodeArr = {"en", "es", "da", "fr", "it", "de", "ja", "nl", "no", "pl",
        "pt", "ro", "fi", "sv", "ar", "zh-cn", "zh-tw", "he", "ko"};

    @Override
    public void start(BundleContext bundleContext) throws Exception {
        if (!supportJavaSE8()) {
        	throw(new Exception("The application works on JAVA SE 8 device only, BundleActivator stating failed!"));
        }
        this.bundleContext = bundleContext;        
        try {
            // Start logger
            startLogger();
            Logger.i("Print BundleActivator start");
            fetchConfigurationService();
            scanSettingsManager = new CaptureSettingsManager(configurationService, this);
            adminSettingsProvider = new AdminSettingsProvider(configurationService, this);
            settingsProvider = new SettingsProvider(this);
            adminSettingsProvider.creatDefaultAdmin();
            canonAuthListener = new CanonAuthListener(this);
            // Fetch services
            fetchHttpsService();
            fetchAppletViewerService();
            // Register servlet
            registerServlet();
            // Start applet
            isNiR = isNiRDevice();
            startApplet();
            fetchLocalLoginService();

        } catch (Exception e) {
            Logger.e("Start BundleActivator failed.", e);
            if (e instanceof NamespaceException) {
                unregisterServlet();
            }
            scanSettingsManager = null;
            adminSettingsProvider = null;
            canonAuthListener = null;
            throw e;
        }
    }

    private boolean supportJavaSE8() {
    	String msVer = System.getProperty("meap.spec_version");
    	boolean result = false;
    	StringTokenizer stringTokenizer = new StringTokenizer(msVer, ",");
    	while (stringTokenizer.hasMoreElements()) {
    		/* Device is Java SE 8 */
    		if (stringTokenizer.nextToken().equals("113")) {
    			result = true;
    			break;
    		}
    	}
    	return result;
    }
    
    @Override
    public void stop(BundleContext bundleContext) throws Exception {
        Logger.i("BundleActivator stop");

        try {
            scanSettingsManager = null;
            adminSettingsProvider = null;
            canonAuthListener = null;
            unregisterServlet();
            stopApplet();
        } catch (Exception e) {
            Logger.e("Stop BundleActivator failed.", e);
            throw e;
        }

    }

    private void startApplet() throws Exception {
        applet = new CaptureApplet(bundleContext, isNiR, this);
        AppletContext appletContext = appletViewerService.createDefaultAppletContext();
        Properties properties = new Properties();

        if (isNiR) {
            Icon icon = createIcon();
            appletViewerService.registerApplet(AppConstants.APPLET_NAME, applet, icon, properties,
                appletContext);
        } else {
            appletViewerService.registerApplet(AppConstants.APPLET_NAME, applet, AppConstants.APPLET_TITLE, null, properties,
                appletContext);
        }
    }

    private void stopApplet() throws Exception {
        try {
            if (isNiR){
                disposeIcon();
            }
            appletViewerService.unregister(AppConstants.APPLET_NAME);
        } catch (Exception e) {
            throw e;
        }

        boolean ungetStatus = bundleContext.ungetService(appletViewerServiceReference);
        if (!ungetStatus) {
            throw new IllegalArgumentException("AVS ungetService failed.");
        }

        appletViewerServiceReference = null;
        appletViewerService = null;
    }

    private void registerServlet() throws Exception {
        configServlet = new CaptureConfigServlet(configurationService, this);
        final Properties properties = new Properties();
        final HttpContext defaultHttpContext = httpExtendedService.createDefaultHttpContext();
        final HttpContext noAuthContext = new HttpContext() {
            @Override
            public boolean handleSecurity(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
                return true;
            }

            @Override
            public URL getResource(final String resource) {
                return this.getClass().getClassLoader().getResource(resource);
            }

            @Override
            public String getMimeType(final String resource) {
                return defaultHttpContext.getMimeType(resource);
            }
        };
        this.httpExtendedService.registerResources(AppConstants.PRINT_RESOURCES_PATH, "webview",
            noAuthContext);
        try {
            httpExtendedService.registerServlet(AppConstants.PRINT_SERVLET_PATH, configServlet,
                HttpExtendedService.SSL, properties, noAuthContext);
        } catch (ServletException | NamespaceException e) {
            Logger.e("Servlet registration failed", e);
            throw e;
        }
    }

    private void unregisterServlet() throws Exception {
        try {
            httpExtendedService.unregister(AppConstants.PRINT_RESOURCES_PATH);
            httpExtendedService.unregister(AppConstants.PRINT_SERVLET_PATH, HttpExtendedService.SSL);
        } catch (Exception e) {
            throw e;
        }

        if (httpExtendedService != null) {
            boolean ungetStatus = bundleContext.ungetService(httpsServiceReference);
            if (!ungetStatus) {
                throw new Exception("HTTPS ungetService failed.");
            }
        }
        httpsServiceReference = null;
        httpExtendedService = null;
        configServlet = null;
    }

    private Icon createIcon() {
        String ICON_32 = "image/ic_printix_cap_32.png";
        String ICON_48 = "image/ic_printix_cap_48.png";
        String ICON_72 = "image/ic_printix_cap_72.png";
        String ICON_110 = "image/ic_printix_cap_110.png";

        ClassLoader classLoader = getClass().getClassLoader();
        URL url = classLoader.getResource(ICON_32);
        image_32 = applet.getToolkit().getImage(url);

        url = classLoader.getResource(ICON_48);
        image_48 = applet.getToolkit().getImage(url);

        url = classLoader.getResource(ICON_72);
        image_72 = applet.getToolkit().getImage(url);

        url = classLoader.getResource(ICON_110);
        image_110 = applet.getToolkit().getImage(url);

        Icon icon = new Icon(AppConstants.APPLET_TITLE);
        IconImageData iconImage_32 = new IconImageData(IconImageData.ICON_KIND_XS, image_32);
        icon.addIconImageData(iconImage_32);

        IconImageData iconImage_48 = new IconImageData(IconImageData.ICON_KIND_S, image_48);
        icon.addIconImageData(iconImage_48);

        IconImageData iconImage_72 = new IconImageData(IconImageData.ICON_KIND_M, image_72);
        icon.addIconImageData(iconImage_72);

        IconImageData iconImage_110 = new IconImageData(IconImageData.ICON_KIND_L, image_110);
        icon.addIconImageData(iconImage_110);
        setLabelData(icon);
        return icon;
    }

    private void disposeIcon() {
        if (image_32 != null) {
            image_32.flush();
            image_32 = null;
        }
        if (image_48 != null) {
            image_48.flush();
            image_48 = null;
        }
        if (image_72 != null) {
            image_72.flush();
            image_72 = null;
        }
        if (image_110 != null) {
            image_110.flush();
            image_110 = null;
        }
    }

    private void fetchAppletViewerService() throws Exception {
        String serviceName = AppletViewerService.class.getName();

        appletViewerServiceReference = bundleContext.getServiceReference(serviceName);
        if (appletViewerServiceReference == null) {
            throw new Exception(serviceName + " getServiceReference failed.");
        }

        appletViewerService = (AppletViewerService) bundleContext.getService(appletViewerServiceReference);
        if (appletViewerService == null) {
            throw new Exception(serviceName + " getService failed.");
        }
    }

    private void fetchConfigurationService() throws Exception {
        String serviceName = ConfigurationService.class.getName();

        ServiceReference configurationServiceReference = bundleContext.getServiceReference(
            serviceName);
        if (configurationServiceReference == null) {
            throw new Exception(serviceName + " getServiceReference failed.");
        }

        configurationService = (ConfigurationService) bundleContext.getService(
            configurationServiceReference);
        if (configurationService == null) {
            throw new Exception(serviceName + " getService failed.");
        }
    }

    private void fetchHttpsService() throws Exception {
        String serviceName = "com.canon.meap.service.http.HttpExtendedService";

        httpsServiceReference = bundleContext.getServiceReference(serviceName);
        if (httpsServiceReference == null) {
            throw new Exception(serviceName + " getServiceReference failed.");
        }

        httpExtendedService = (HttpExtendedService) bundleContext.getService(httpsServiceReference);
        if (httpExtendedService == null) {
            throw new Exception(serviceName + " getService failed.");
        }
    }
    private boolean isNiRDevice() {
        String msVer = null;
        try {
            msVer = System.getProperty("meap.spec_version");
            Logger.i("msVer: " + msVer);

            if (msVer != null) {
                StringTokenizer stringTokenizer = new StringTokenizer(msVer, ",");
                while (stringTokenizer.hasMoreElements()) {
                    if (stringTokenizer.nextToken().equals("33")) {
                        return true;
                    }
                }
            }
        } catch (AccessControlException e) {
            Logger.e("Failed to access system property meap.spec_version.", e);
        }

        return false;
    }

    private void startLogger() {
        final String LOGSERVICE_NAME = LogService.class.getName();
        ServiceReference _logServiceRef = bundleContext.getServiceReference(LOGSERVICE_NAME);
        LogService _logServiceInstance = (LogService) bundleContext.getService(_logServiceRef);
        LogServiceAccessor logServiceAccessor = new LogServiceAccessor(_logServiceInstance);
    }

    /**
     * UserEventReceiver receives the event of the "Login" and "Logout".
     */
    private class UserEventReceiver implements UserEventListener {

        /**
         * It will be called if this service is logged in.
         *
         * @param event UserEvent
         */
        public void login(UserEvent event) {
            loginContext = event.getLoginContext();
        }

        /**
         * It will be called if this service is logged out.
         *
         * @param event UserEvent
         */
        public void logout(UserEvent event) {
            loginContext = null;
        }
    }

    private void fetchLocalLoginService() {
        ServiceReference ilsServiceReference
            = bundleContext.getServiceReference(
            "com.canon.meap.service.login.LocalLoginService");
        LocalLoginService localLoginService
            = (LocalLoginService) bundleContext.getService(ilsServiceReference);
        UserEventListener userEventListener = new UserEventReceiver();
        localLoginService.addUserEventListener(userEventListener);
    }

    private void setLabelData(Icon icon) {
        Logger.d(TAG, "setLabelData start");
        for (int i = 0; i < languageCodeArr.length; i++) {
            Logger.d(TAG, "languageCode " + languageCodeArr[i]);
            IconLabelData iconLabelData;
            if (languageCodeArr[i].equalsIgnoreCase("zh-tw")) {
                iconLabelData = new IconLabelData("zhTW",
                    Util.getLocalizedText(AppConstants.ICONLABEL_TEXT, Locale.TAIWAN));
            } else if (languageCodeArr[i].equalsIgnoreCase("zh-cn")) {
                iconLabelData = new IconLabelData("zhCN",
                    Util.getLocalizedText(AppConstants.ICONLABEL_TEXT, Locale.CHINA));
            } else {
                iconLabelData = new IconLabelData(languageCodeArr[i],
                    Util.getLocalizedText(AppConstants.ICONLABEL_TEXT,
                        new Locale(languageCodeArr[i])));
            }
            Logger.d(TAG, "iconLabelData: " + iconLabelData.getLabel());
            icon.addIconLabelData(iconLabelData);
        }
    }
}
