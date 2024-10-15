//------------------------------------------------------------------------------
// Copyright (c) 2002-2024 Tungsten Automation. All rights reserved.
// Description : IServiceFinder
//------------------------------------------------------------------------------

package net.printix.device.canon.meap.capture;

import com.canon.meap.service.conf.ConfigurationService;
import com.canon.meap.service.http.HttpExtendedService;
import net.printix.device.canon.meap.capture.settingmanager.CaptureSettingsManager;
import net.printix.device.canon.meap.capture.settings.AdminSettingsProvider;
import net.printix.device.canon.meap.capture.settings.SettingsProvider;
import org.osgi.framework.BundleContext;

public interface IServiceFinder {
    BundleContext getBundleContext();
    AdminSettingsProvider getAdminSettingsProvider();
    ConfigurationService getConfigurationService();
    CaptureSettingsManager getScanSettingsManager();
    HttpExtendedService getHttpExtendedService();
    CanonAuthListener getCanonAuthListener();
    SettingsProvider getSettingsProvider();
}