// ------------------------------------------------------------------------------
// Copyright (c) 2002-2024 Tungsten Automation. All rights reserved.
// Description: LoadingProviderTest
// ------------------------------------------------------------------------------
package net.printix.device.canon.meap.print.settings;

import java.awt.Dimension;
import java.awt.Image;
import java.lang.reflect.Field;
import net.printix.device.canon.meap.capture.settings.LoadingProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class LoadingProviderTest {

  @Test
  void testGetLoadingImage() {
    Dimension appletSize = new Dimension(100,100);
    Image result = LoadingProvider.getLoadingImage(appletSize);
    Assertions.assertEquals(100, result.getHeight(null));
  }

  @Test
  void testIsLanguageChanged() throws NoSuchFieldException, IllegalAccessException {
    Field field = LoadingProvider.class.getDeclaredField("languageCode");
    field.setAccessible(true);
    field.set(null, null);
    boolean result = LoadingProvider.isLanguageChanged();
    Assertions.assertEquals(true, result);
  }
}
