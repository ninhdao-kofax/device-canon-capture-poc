// ------------------------------------------------------------------------------
// Copyright (c) 2002-2024 Tungsten Automation. All rights reserved.
// Description: AdminSettingsTest
// ------------------------------------------------------------------------------
package net.printix.device.canon.meap.print.settings;

import net.printix.device.canon.meap.capture.settings.AdminSettings;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AdminSettingsTest {

  AdminSettings adminSettings = new AdminSettings("username", "password");

  @Test
  void testBuilder() {
    adminSettings.hashCode();
    AdminSettings result = AdminSettings.builder().password("password").username("username")
        .build();
    Assertions.assertEquals("password", result.getPassword());
    Assertions.assertEquals("username", result.getUsername());
  }

}

