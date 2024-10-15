// ------------------------------------------------------------------------------
// Copyright (c) 2002-2024 Tungsten Automation. All rights reserved.
// Description: PrintSettingsManagerTest
// ------------------------------------------------------------------------------
package net.printix.device.canon.meap.print.settingmanager;

import com.canon.meap.service.conf.Configuration;
import com.canon.meap.service.conf.ConfigurationService;
import java.util.HashMap;
import java.util.Map;
import net.printix.device.canon.meap.capture.settingmanager.CaptureSettingsManager;
import net.printix.device.canon.meap.capture.settings.AppConstants;
import net.printix.device.canon.meap.capture.settings.AppSettings;
import net.printix.device.canon.meap.capture.settings.SettingsNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static net.printix.device.canon.meap.capture.settings.SettingsProvider.*;
import static org.mockito.Mockito.*;

class PrintSettingsManagerTest {

  @Mock
  ConfigurationService configService;
  @InjectMocks
  CaptureSettingsManager printSettingsManager;
  private AutoCloseable closeable;

  @BeforeEach
  void setUp() {
    closeable = MockitoAnnotations.openMocks(this);

    Configuration targetConfig = mock(Configuration.class);
    when(configService.getApplicationConfiguration(AppConstants.PRINT_CONFIG_KEY)).thenReturn(
        targetConfig);
    Map properties = new HashMap();
    properties.put(TENANT_ID, "tenantId");
    properties.put(PRINTER_ID, "SGVsbG8gV29ybGQ=");
    properties.put(DEVICE_CANON_HOST, "deviceCanonHost");
    properties.put(PRINTER_SECRET, "deviceSecret");
    properties.put(DEVICE_LOG_ENABLE, "true");
    properties.put(DEVICE_LOG_LEVEL, "INFO");
    when(targetConfig.getProperties()).thenReturn(properties);
  }

  @Test
  void testGetReleaseAppUrl() throws SettingsNotFoundException {
    String result = printSettingsManager.getReleaseAppUrl();
    Assertions.assertTrue(result.contains("deviceCanonHost"));
  }

  @Test
  void testGetAppSettings() {
    AppSettings result = printSettingsManager.getAppSettings();
    Assertions.assertEquals(true, result.isDeviceLogEnable());
  }

  @Test
  void testSetInstance() {
    Assertions.assertThrows(RuntimeException.class,
        () -> CaptureSettingsManager.setInstance(new CaptureSettingsManager(null)));
  }

  @Test
  void testSetAppSettings() {
    printSettingsManager.setAppSettings(AppSettings.builder().deviceLogEnable(true).build());
    Assertions.assertTrue(printSettingsManager.getAppSettings().isDeviceLogEnable());

  }

  @AfterEach
  void tearDown() throws Exception {
    closeable.close();
    CaptureSettingsManager.setInstance(null);
  }
}

