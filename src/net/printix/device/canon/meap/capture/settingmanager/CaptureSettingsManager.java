//------------------------------------------------------------------------------
// Copyright (c) 2002-2024 Tungsten Automation. All rights reserved.
// Description: PrintSettingsManager
//------------------------------------------------------------------------------

package net.printix.device.canon.meap.capture.settingmanager;

import com.canon.meap.service.conf.ConfigurationService;

import net.printix.device.canon.meap.capture.IServiceFinder;
import net.printix.device.canon.meap.capture.log.Logger;
import net.printix.device.canon.meap.capture.settings.AppSettings;
import net.printix.device.canon.meap.capture.settings.SettingsNotFoundException;

public class CaptureSettingsManager {
    
    private ConfigurationService configService = null;
    private AppSettings appSettings = null;
    private IServiceFinder serviceFinder;

    public CaptureSettingsManager(ConfigurationService configService, IServiceFinder serviceFinder) {
        this.configService = configService;
        this.serviceFinder = serviceFinder;
    }

    public String getReleaseAppUrl() throws SettingsNotFoundException {
        AppSettings appSettings = getAppSettings();

        String deviceCanonHost = appSettings.getDeviceCanonHost();
        String tenantId = appSettings.getTenantId();
        String printerId = appSettings.getPrinterId();
        String printerSecret = appSettings.getDeviceSecret();

        return String.format("%s/ws/canon/tenants/%s/devices/%s/captureApp?authorization=%s", deviceCanonHost, tenantId, printerId,
                printerSecret);
    }

    public AppSettings getAppSettings() {
        // Device restart, then need to get settings from device memory
        if (appSettings == null) {
            try {
                appSettings = serviceFinder.getSettingsProvider().getConfigurationValue(configService);
            } catch (SettingsNotFoundException e) {
                Logger.e("getAppSettings error", e);
            }
        }

        return appSettings;
    }

    public void setAppSettings(AppSettings appSettings) {
        this.appSettings = appSettings;
    }
}
