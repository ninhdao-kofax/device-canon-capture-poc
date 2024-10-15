// ------------------------------------------------------------------------------
// Copyright (c) 2002-2024 Tungsten Automation. All rights reserved.
// Description: AppSettingsTest
// ------------------------------------------------------------------------------
package net.printix.device.canon.meap.print.settings;

import net.printix.device.canon.meap.capture.settings.AppSettings;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AppSettingsTest {

  AppSettings appSettings = new AppSettings("tenantId", "printerId", "deviceCanonHost",
      "deviceSecret", true, "logLevel");

  @Test
  void testToString() {
    appSettings.hashCode();
    String result = appSettings.toString();
    Assertions.assertEquals(
        "AppSettings [tenantId=tenantId, printerId=printerId, deviceCanonHost=deviceCanonHost, deviceLogEnable=true, logLevel=logLevel]",
        result);
  }

  @Test
  void testBuilder() {
    AppSettings result = AppSettings.builder().deviceSecret("deviceSecret").deviceCanonHost("deviceCanonHost").printerId("printerId").tenantId("tenantId").logLevel("DEBUG").deviceLogEnable(true).build();
    Assertions.assertEquals(true, result.isDeviceLogEnable());
  }

}

