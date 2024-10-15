//------------------------------------------------------------------------------
// Copyright (c) 2002-2024 Tungsten Automation. All rights reserved.
// Description: AdminSettingsProviderTest
//------------------------------------------------------------------------------
package net.printix.device.canon.meap.print.settings;

import com.canon.meap.service.conf.Configuration;
import com.canon.meap.service.conf.ConfigurationService;
import com.canon.meap.service.conf.ForbiddenOperationException;
import com.canon.meap.service.conf.OutOfSpaceException;
import com.canon.security.crypt.CryptoException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import net.printix.device.canon.meap.capture.settings.AdminSettings;
import net.printix.device.canon.meap.capture.settings.AdminSettingsProvider;
import net.printix.device.canon.meap.capture.settings.AppConstants;
import net.printix.device.canon.meap.capture.settings.SettingsNotFoundException;
import net.printix.device.canon.meap.capture.util.Util;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

class AdminSettingsProviderTest {

  @Mock
  ConfigurationService configurationService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testSave()
      throws NoSuchAlgorithmException, OutOfSpaceException {
    Configuration configuration = mock(Configuration.class);
    when(configurationService.getApplicationConfiguration(
        AppConstants.ADMIN_CONFIG_KEY)).thenReturn(configuration);

    AdminSettingsProvider adminSettingsProvider = new AdminSettingsProvider(configurationService);;
    adminSettingsProvider.save(new AdminSettings("username", "password"), false);
    Assertions.assertEquals(Util.bytesToHex("password"),adminSettingsProvider.getCacheSettings().getPassword());
  }


  @Test
  void testFromJson() throws Exception {
    AdminSettings result = AdminSettingsProvider.fromJson("{\n"
        + "    \"username\":\"username\",\n"
        + "    \"password\":\"password\"\n"
        + "}");
    Assertions.assertEquals(new AdminSettings("username", "password"), result);
  }


  @Test
  void testSetCacheSettings() {
    AdminSettingsProvider adminSettingsProvider = new AdminSettingsProvider(configurationService);;
    adminSettingsProvider.setCacheSettings(new AdminSettings("username", "password"));
    Assertions.assertEquals(adminSettingsProvider.getCacheSettings().getPassword(),"password");
  }

  @Test
  void testCreatDefaultAdmin()
      throws SettingsNotFoundException, NoSuchAlgorithmException, OutOfSpaceException {
    AdminSettingsProvider adminSettingsProvider = new AdminSettingsProvider(configurationService);;

    Configuration configuration = new Configuration() {
      @Override
      public String getName() {
        return null;
      }

      @Override
      public void setProperties(Map map) throws OutOfSpaceException {

      }

      @Override
      public Map getProperties() {
        return null;
      }

      @Override
      public void setEncryptedProperties(Map map) throws CryptoException, OutOfSpaceException {

      }

      @Override
      public Map getDecryptedProperties() throws CryptoException, ForbiddenOperationException {
        return null;
      }

      @Override
      public Object exportObject(String s, byte[] bytes, int i) throws CryptoException {
        return null;
      }

      @Override
      public void importObject(String s, Object o, byte[] bytes, Class aClass, Class[] classes,
          int i) throws OutOfSpaceException, CryptoException {

      }

      @Override
      public int getPropertyCondition() {
        return 0;
      }

      @Override
      public Class getPropertyType(String s) {
        return null;
      }

      @Override
      public Class[] getPropertyElementType(String s) {
        return new Class[0];
      }

      @Override
      public String[] getKeys() {
        return new String[0];
      }

      @Override
      public void removeObject(String s) throws CryptoException {

      }
    };
    when( configurationService.getApplicationConfiguration(
        AppConstants.ADMIN_CONFIG_KEY)).thenReturn(configuration);
    adminSettingsProvider.creatDefaultAdmin();
    Assertions.assertEquals(adminSettingsProvider.getCacheSettings().getPassword(),AppConstants.DEFAULT_ADMIN_PASSWORD);
  }
}

