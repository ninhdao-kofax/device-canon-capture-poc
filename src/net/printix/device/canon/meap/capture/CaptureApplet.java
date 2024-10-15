//------------------------------------------------------------------------------
// Copyright (c) 2002-2024 Tungsten Automation. All rights reserved.
// Description: PrintApplet
//------------------------------------------------------------------------------

package net.printix.device.canon.meap.capture;

import com.canon.meap.ctk.awt.webview.LoadEvent;
import com.canon.meap.ctk.awt.webview.LoadListener;
import java.applet.Applet;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Rectangle;
import java.util.Locale;

import net.printix.device.canon.meap.capture.log.Logger;
import net.printix.device.canon.meap.capture.settings.AppConstants;
import net.printix.device.canon.meap.capture.settings.LoadingProvider;
import org.osgi.framework.BundleContext;

import com.canon.meap.ctk.awt.webview.CWebEngine.EngineState;
import com.canon.meap.ctk.awt.webview.CWebView;
import com.canon.meap.ctk.awt.webview.Callback;
import com.canon.meap.ctk.awt.webview.EngineStatusChangeListener;
import com.canon.meap.ctk.awt.webview.InitializationException;
import com.canon.meap.ctk.awt.webview.JSConsoleData;
import com.canon.meap.service.avs.CAppletContext;
import com.canon.meap.service.sis.event.CLocaleEvent;
import com.canon.meap.service.sis.event.CLocaleListener;
import net.printix.device.canon.meap.capture.settings.SettingsNotFoundException;

// Canon Printix Print Applet
public class CaptureApplet extends Applet {
    private static final long serialVersionUID = -3680174435077851754L;
    private final static String TAG = CaptureApplet.class.getSimpleName();
    private static final Color BACKGROUND_COLOR = new Color(63, 63, 63);
    private static final int MAX_APPLET_WIDTH = 800;
    private static final int MAX_APPLET_HEIGHT = 500;
    private boolean isNiR;
    private BundleContext bundleContext;
    private LocaleListener localeListener;
    private CWebView webView = null;
    private CAppletContext cAppletContext;
    private Dimension appletSize;
    private IServiceFinder serviceFinder;
    
    public CaptureApplet(BundleContext bundleContext, boolean isNiR, IServiceFinder serviceFinder) {
        super();
        this.bundleContext = bundleContext;
        this.isNiR = isNiR;
        this.serviceFinder = serviceFinder;
    }

    @Override
    public void init() {
        super.init();
        Logger.i("Applet init");

        if (isNiR) {
            cAppletContext = (CAppletContext) getAppletContext();
            appletSize = cAppletContext.getMaxAppletSize();
            localeListener = new LocaleListener();
            cAppletContext.addLocaleListener(localeListener);
        } else {
            appletSize = new Dimension(MAX_APPLET_WIDTH, MAX_APPLET_HEIGHT);
        }
        Logger.i("Applet dimension: " + appletSize);

        Rectangle appletRect = new Rectangle(appletSize);
        appletRect.setLocation(0, 0);
        setBounds(appletRect);

        setLayout(null);
        setBackground(BACKGROUND_COLOR);

        try {
            webView = new CWebView(bundleContext);
        } catch (InitializationException e) {
            Logger.e("init WebView failed.", e);
        }
        webView.setBounds(0, 0, (int) appletSize.getWidth(), (int) appletSize.getHeight());
        setWebViewLoadingBackground(appletSize);
        add(webView);

        /* Show WebView */
        webView.getEngine().setLoadListener(new LoadListener() {
            @Override
            public void titleChanged(final String arg0) {
                Logger.d("titleChanged: " + arg0);
            }

            @Override
            public void stateChanged(final LoadEvent arg0) {
                Logger.d("stateChanged: " + arg0.getState() + ", " + arg0.getException());
            }

            @Override
            public void progressChanged(final double arg0) {
                Logger.d("progressChanged: " + arg0);
            }

            @Override
            public void locationChanged(final String arg0) {
                Logger.d("locationChanged: has been changed");
            }
        });

        webView.getEngine().setOnJsConsole(new Callback<JSConsoleData, Void>() {
            @Override
            public Void call(final JSConsoleData consoleData) {
                Logger.d("js console: " + consoleData.getMessage());
                return null;
            }
        });

        webView.getEngine().setEngineStatusChangeListener(new EngineStatusChangeListener() {
            @Override
            public void stateChanged(EngineState oldState, EngineState newState) {
                Logger.d("oldState: " + oldState + ", newState: " + newState);
                if (newState == EngineState.ACTIVE) {
                    // Update new loading language on unsupported isNiR devices - without LocaleListener
                    if (!isNiR && LoadingProvider.isLanguageChanged()) {
                        setWebViewLoadingBackground(appletSize);
                    }
                    Logger.d(TAG, "canonUidPwd " + CaptureBundleActivator.getLoginContext()
                        .getUserAttribute("canonUidPwd"));
                    try {
                        if (serviceFinder.getScanSettingsManager().getAppSettings() == null) {
                            webView.getEngine().load(AppConstants.URL_NO_CONFIG);
                        }

                        if (!CaptureBundleActivator.getLoginContext()
                            .isUserInRole(AppConstants.GENERAL)
                            || !AppConstants.PRINTIX.equals(
                            CaptureBundleActivator.getLoginContext()
                                .getUserAttribute("canonUidPwd"))) {
                            webView.getEngine().load(AppConstants.URL_DENIED);
                        } else {
                            String url = serviceFinder.getScanSettingsManager().getReleaseAppUrl();
                            webView.getEngine().load(url);
                        }
                    } catch (SettingsNotFoundException | IllegalArgumentException e) {
                        Logger.e(TAG, "Load webview error.");
                    }
                }
            }
        });
    }

    private void setWebViewLoadingBackground(Dimension appletSize) {
        Logger.d(TAG, "setWebViewLoadingBackground - starting");
        Image image = null;
        try {
            image = LoadingProvider.getLoadingImage(appletSize);
            this.webView.setBackgroundImage(image);
            Logger.d(TAG, "setWebViewLoadingBackground - changed backgroundImage (with locale = " + Locale.getDefault() + ")");
        } catch (Exception e) {
            Logger.e(TAG, "Error setWebViewLoadingBackground: " + e.getMessage());
        } finally {
            if (image != null) {
                image.flush();
                image = null;
            }
        }
    }
    
    @Override
    public void destroy() {
    	if (cAppletContext != null && localeListener != null) {
    		cAppletContext.removeLocaleListener(localeListener);
		}
        super.destroy();
        Logger.i("Applet destroy");

        if (webView != null) {
            remove(webView);
            webView = null;
        }
    }
    
    private class LocaleListener implements CLocaleListener {
        @Override
        public void localeChanged(final CLocaleEvent localeEvent) {
        	if (webView != null) {
                Logger.d(TAG, "localeChanged - new locale: " + localeEvent.getLocale());
                setWebViewLoadingBackground(appletSize);
        	}
        }
    }
}
