//------------------------------------------------------------------------------
// Copyright (c) 2002-2024 Tungsten Automation. All rights reserved.
// Description: SettingsProviderTest
//------------------------------------------------------------------------------

package net.printix.device.canon.meap.print.settings;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;

import com.canon.meap.service.conf.OutOfSpaceException;
import net.printix.device.canon.meap.capture.log.LogServiceAccessor;
import net.printix.device.canon.meap.capture.settings.AppConstants;
import net.printix.device.canon.meap.capture.settings.AppSettings;
import net.printix.device.canon.meap.capture.settings.SettingsNotFoundException;
import net.printix.device.canon.meap.capture.settings.SettingsProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import com.canon.meap.service.conf.Configuration;
import com.canon.meap.service.conf.ConfigurationService;
import org.mockito.MockitoAnnotations;

public class SettingsProviderTest {

  @Mock
  private ConfigurationService configService;
  @Mock
  private Configuration targetConfig;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }
  @Test
  public void testGetConfigurationValue() {

    when(configService.getApplicationConfiguration(AppConstants.PRINT_CONFIG_KEY)).thenReturn(
        targetConfig);
    when(targetConfig.getProperties()).thenReturn(null);

    try {
      SettingsProvider.getConfigurationValue(configService);
      fail("Expected throw SettingsNotFoundException");
    } catch (SettingsNotFoundException e) {
      assertNotNull(e.getMessage());
    }
  }

  @Test
  void testSetConfigurationValue() throws OutOfSpaceException {
    when(configService.getApplicationConfiguration(AppConstants.PRINT_CONFIG_KEY)).thenReturn(
        targetConfig);
    SettingsProvider.setConfigurationValue(configService,
        new AppSettings("tenantId", "printerId", "deviceCanonHost", "deviceSecret", true,
            "logLevel"));
    Assertions.assertTrue(LogServiceAccessor.getInstance().isDeviceLogEnabled());
  }

  @Test
  void testFromJson() throws Exception {
    AppSettings result = SettingsProvider.fromJson("{\n"
        + "    \"tenantId\":\"4bb67a29-9c6f-48b9-a9f0-0fa5441dcbf1\",\n"
        + "    \"printerId\":\"5acfe9fa-c0a1-4cf3-af51-4505d24c8a44\",\n"
        + "    \"deviceCanonHost\":\"https://on-device-api.dev02.printix.dev:443\", \n"
        + "    \"printerSecret\":\"U2ggAwXL4KZcVC3SA1cRa2J7Ez1u0wXnKYoomWE7qOhbFZzYJaK5xy3B\",\n"
        + "    \"deviceSecret\":\"deviceSecret\",\n"
        + "    \"deviceLogEnable\":true,\n"
        + "    \"logLevel\":\"DEBUG\"\n"
        + "}");
    Assertions.assertTrue(result.isDeviceLogEnable());
    Assertions.assertTrue(result.getDeviceSecret().equals("deviceSecret"));
  }

}
