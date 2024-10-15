//------------------------------------------------------------------------------
// Copyright (c) 2002-2024 Tungsten Automation. All rights reserved.
// Description: SettingsProvider
//------------------------------------------------------------------------------
package net.printix.device.canon.meap.capture.settings;

import com.canon.meap.service.conf.Configuration;
import com.canon.meap.service.conf.ConfigurationService;
import com.canon.meap.service.conf.OutOfSpaceException;
import java.util.HashMap;
import java.util.Map;
import net.printix.device.canon.meap.capture.IServiceFinder;
import net.printix.device.canon.meap.capture.log.LogServiceAccessor;
import net.printix.device.canon.meap.capture.util.Util;
import org.json.JSONObject;

public class SettingsProvider {
    public static final String TENANT_ID = "tenantId";
    public static final String PRINTER_ID = "printerId";
    public static final String DEVICE_CANON_HOST = "deviceCanonHost";
    public static final String PRINTER_SECRET = "deviceSecret";
    public static final String DEVICE_LOG_ENABLE = "deviceLogEnable";
    public static final String DEVICE_LOG_LEVEL = "logLevel";
    private IServiceFinder serviceFinder;

    public SettingsProvider(IServiceFinder serviceFinder) {
        this.serviceFinder = serviceFinder;
    }

    public AppSettings getConfigurationValue(ConfigurationService configService) throws SettingsNotFoundException {
        Configuration targetConfig = configService.getApplicationConfiguration(AppConstants.CAPTURE_CONFIG_KEY);

        @SuppressWarnings("rawtypes")
        Map properties = targetConfig.getProperties();
        if (properties != null && !properties.isEmpty()) {
            AppSettings.AppSettingsBuilder appSettings = new AppSettings.AppSettingsBuilder();
            appSettings.tenantId(Util.decodeBase64(properties.get(TENANT_ID).toString()));
            appSettings.printerId(Util.decodeBase64(properties.get(PRINTER_ID).toString()));
            appSettings.deviceCanonHost(properties.get(DEVICE_CANON_HOST).toString());
            appSettings.deviceSecret(Util.decodeBase64(properties.get(PRINTER_SECRET).toString()));
            appSettings.deviceLogEnable(Boolean.parseBoolean(properties.get(DEVICE_LOG_ENABLE).toString()));
            appSettings.logLevel(properties.get(DEVICE_LOG_LEVEL).toString());

            return appSettings.build();
        }

        throw new SettingsNotFoundException("AppSettings Not Found.");
    }

    // Save Print Application Configuration to Device
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void setConfigurationValue(ConfigurationService configService, final AppSettings appSettings) throws OutOfSpaceException {
        Configuration targetConfig = configService.getApplicationConfiguration(AppConstants.CAPTURE_CONFIG_KEY);

        Map properties = targetConfig.getProperties();
        if (properties == null) {
            properties = new HashMap();
        }
        properties.put(TENANT_ID, Util.encodeBase64(appSettings.getTenantId()));
        properties.put(PRINTER_ID, Util.encodeBase64(appSettings.getPrinterId()));
        properties.put(DEVICE_CANON_HOST, appSettings.getDeviceCanonHost());
        properties.put(PRINTER_SECRET, Util.encodeBase64(appSettings.getDeviceSecret()));
        properties.put(DEVICE_LOG_ENABLE, appSettings.isDeviceLogEnable());
        properties.put(DEVICE_LOG_LEVEL, appSettings.getLogLevel());
        LogServiceAccessor.getInstance().setDeviceLogEnabled(appSettings.isDeviceLogEnable());
        LogServiceAccessor.getInstance().setDeviceLogLevel(appSettings.getLogLevel());
        targetConfig.setProperties(properties);
    }

    public void delConfigurationValue(ConfigurationService configService, final AppSettings appSettings) throws OutOfSpaceException {
        configService.deleteApplicationConfiguration(AppConstants.CAPTURE_CONFIG_KEY);
    }

    public AppSettings fromJson(String jsonString) throws Exception {
        JSONObject json = new JSONObject(jsonString);

        AppSettings.AppSettingsBuilder appSettings = new AppSettings.AppSettingsBuilder();
        appSettings.tenantId(json.getString(SettingsProvider.TENANT_ID));
        appSettings.printerId(json.getString(SettingsProvider.PRINTER_ID));
        appSettings.deviceCanonHost(json.getString(SettingsProvider.DEVICE_CANON_HOST));
        appSettings.deviceSecret(json.getString(SettingsProvider.PRINTER_SECRET));
        appSettings.deviceLogEnable(json.getBoolean(SettingsProvider.DEVICE_LOG_ENABLE));
        appSettings.logLevel(json.getString(SettingsProvider.DEVICE_LOG_LEVEL));

        return appSettings.build();
    }

    public JSONObject toJson(AppSettings appSettings) {
        JSONObject jsonObject = new JSONObject();

        if (appSettings != null) {
            jsonObject.put(SettingsProvider.TENANT_ID, appSettings.getTenantId());
            jsonObject.put(SettingsProvider.PRINTER_ID, appSettings.getPrinterId());
            jsonObject.put(SettingsProvider.DEVICE_CANON_HOST, appSettings.getDeviceCanonHost());
            jsonObject.put(SettingsProvider.DEVICE_LOG_ENABLE, appSettings.isDeviceLogEnable());
            jsonObject.put(SettingsProvider.DEVICE_LOG_LEVEL, appSettings.getLogLevel());
        }

        return jsonObject;
    }
}
