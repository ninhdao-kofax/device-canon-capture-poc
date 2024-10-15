// ------------------------------------------------------------------------------
// Copyright (c) 2002-2024 Tungsten Automation. All rights reserved.
// Description: AdminSettingsProvider
// ------------------------------------------------------------------------------

package net.printix.device.canon.meap.capture.settings;

import com.canon.meap.service.conf.Configuration;
import com.canon.meap.service.conf.ConfigurationService;
import com.canon.meap.service.conf.OutOfSpaceException;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

import net.printix.device.canon.meap.capture.IServiceFinder;
import net.printix.device.canon.meap.capture.log.Logger;
import net.printix.device.canon.meap.capture.util.Util;
import org.json.JSONObject;

public final class AdminSettingsProvider {
    private static final String TAG = AdminSettingsProvider.class.getSimpleName();
    private ConfigurationService configurationService = null;
    private IServiceFinder serviceFinder = null;
    @Getter
    @Setter
    private AdminSettings cacheSettings = new AdminSettings.AdminSettingsBuilder().password("").username("").build();// avoid NPE
    private static final Object lock = new Object();

    public AdminSettingsProvider(ConfigurationService configurationService, IServiceFinder serviceFinder) {
        this.configurationService = configurationService;
        this.serviceFinder = serviceFinder;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void save(final AdminSettings adminSettings, boolean isDefaultAccount) throws NoSuchAlgorithmException, OutOfSpaceException {
        Logger.i(TAG, "save");
        synchronized (lock) {
            // Retrieve application-specific configuration information
            Configuration configuration = configurationService.getApplicationConfiguration(
                    AppConstants.ADMIN_CONFIG_KEY);
            Map properties = configuration.getProperties();
            if (properties == null) {
                properties = new HashMap<>();
            }
            properties.put(AppConstants.USER_NAME, AppConstants.DEFAULT_ADMIN_USERNAME);
            if (isDefaultAccount) {
                properties.put(AppConstants.PASSWORD, AppConstants.DEFAULT_ADMIN_PASSWORD);
            } else {
                properties.put(AppConstants.PASSWORD, Util.bytesToHex(adminSettings.getPassword()));
            }
            configuration.setProperties(properties);
            AdminSettings.AdminSettingsBuilder adminSettingsBuilder = AdminSettings.builder();
            adminSettingsBuilder.username(properties.get(AppConstants.USER_NAME).toString());
            adminSettingsBuilder.password(properties.get(AppConstants.PASSWORD).toString());
            cacheSettings = adminSettingsBuilder.build();
        }
    }

    public AdminSettings load() throws SettingsNotFoundException {
        Logger.i(TAG, "load");
        synchronized (lock) {
            if (cacheSettings != null && !cacheSettings.getUsername().isEmpty() && !cacheSettings.getPassword().isEmpty()) {
                return cacheSettings;
            }
            // Retrieve application-specific configuration information
            AdminSettings.AdminSettingsBuilder adminSettings = AdminSettings.builder();
            Configuration configuration = configurationService.getApplicationConfiguration(
                AppConstants.ADMIN_CONFIG_KEY);
            Map propertiesFromCache = configuration.getProperties();
            // If the configuration's value is existing in the device's cache.
            if (propertiesFromCache != null && !propertiesFromCache.isEmpty()) {
                if (propertiesFromCache.get(AppConstants.USER_NAME) == null || propertiesFromCache.get(AppConstants.USER_NAME).toString().isEmpty()) {
                    throw new SettingsNotFoundException("admin is not configured yet!");
                }
                if (propertiesFromCache.get(AppConstants.PASSWORD) == null || propertiesFromCache.get(AppConstants.PASSWORD).toString().isEmpty()) {
                    throw new SettingsNotFoundException("password is not configured yet!");
                }
                adminSettings.username(propertiesFromCache.get(AppConstants.USER_NAME).toString());
                adminSettings.password(propertiesFromCache.get(AppConstants.PASSWORD).toString());
                cacheSettings = adminSettings.build();
            }

            return cacheSettings;
        }
    }

    public void creatDefaultAdmin() throws NoSuchAlgorithmException, SettingsNotFoundException, OutOfSpaceException {
        Logger.i(TAG, "creatDefaultAdmin");
        synchronized (lock) {
            AdminSettings existedAccount = load();
            // Retrieve application-specific configuration information
            if (existedAccount == null || existedAccount.getUsername().isEmpty() || existedAccount.getPassword().isEmpty()) {
                AdminSettings.AdminSettingsBuilder adminSettings = AdminSettings.builder();
                adminSettings.username(AppConstants.DEFAULT_ADMIN_USERNAME);
                adminSettings.password(AppConstants.DEFAULT_ADMIN_PASSWORD);
                save(adminSettings.build(), true);
            } else {
                Logger.d(TAG, "The account's already existed");
            }
        }
    }

    public boolean checkCredentials(AdminSettings adminSettings)
        throws NoSuchAlgorithmException, SettingsNotFoundException {
        Logger.i(TAG, "checkCredentials");
        synchronized (lock) {
            // Retrieve application-specific configuration information
            AdminSettings existSetting = load();
            String existedAccount = String.format("%s:%s", existSetting.getUsername(), existSetting.getPassword());
            String comparedAccount = String.format("%s:%s", adminSettings.getUsername(), adminSettings.getPassword());
            return existedAccount.equals(comparedAccount);
        }
    }

    public AdminSettings fromJson(String jsonString) throws Exception {
        JSONObject json = new JSONObject(jsonString);
        AdminSettings.AdminSettingsBuilder adminSettings = new AdminSettings.AdminSettingsBuilder();
        adminSettings.username(json.getString(AppConstants.USER_NAME));
        adminSettings.password(json.getString(AppConstants.PASSWORD));

        return adminSettings.build();
    }

}
