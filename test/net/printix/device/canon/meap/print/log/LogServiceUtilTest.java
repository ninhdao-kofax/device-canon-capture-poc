// ------------------------------------------------------------------------------
// Copyright (c) 2002-2024 Tungsten Automation. All rights reserved.
// Description: LogServiceUtilTest
// ------------------------------------------------------------------------------
package net.printix.device.canon.meap.print.log;

import com.canon.meap.service.log.Logger;
import net.printix.device.canon.meap.capture.log.LogServiceUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class LogServiceUtilTest {

  @Test
  void testGetLevel0() {
    String result = LogServiceUtil.getLevel(0);
    Assertions.assertEquals(LogServiceUtil.INFO, result);
  }

  @Test
  void testGetLevel1() {
    String result = LogServiceUtil.getLevel(1);
    Assertions.assertEquals(LogServiceUtil.CRITICAL, result);
  }

  @Test
  void testGetLevel2() {
    String result = LogServiceUtil.getLevel(2);
    Assertions.assertEquals(LogServiceUtil.ERROR, result);
  }

  @Test
  void testGetLevel3() {
    String result = LogServiceUtil.getLevel(3);
    Assertions.assertEquals(LogServiceUtil.WARNING, result);
  }

  @Test
  void testGetLevel4() {
    String result = LogServiceUtil.getLevel(4);
    Assertions.assertEquals(LogServiceUtil.INFO, result);
  }

  @Test
  void testGetLevel5() {
    String result = LogServiceUtil.getLevel(5);
    Assertions.assertEquals(LogServiceUtil.DEBUG, result);
  }

  @Test
  void testGetLevelx() {
    int result = LogServiceUtil.getLevel("level");
    Assertions.assertEquals(Logger.LOG_LEVEL_INFO, result);
  }

  @Test
  void testGetLevelDebug() {
    int result = LogServiceUtil.getLevel(LogServiceUtil.DEBUG);
    Assertions.assertEquals(Logger.LOG_LEVEL_DEBUG, result);
  }

  @Test
  void testGetLevelInfo() {
    int result = LogServiceUtil.getLevel(LogServiceUtil.INFO);
    Assertions.assertEquals(Logger.LOG_LEVEL_INFO, result);
  }

  @Test
  void testGetLevelWarnning() {
    int result = LogServiceUtil.getLevel(LogServiceUtil.WARNING);
    Assertions.assertEquals(Logger.LOG_LEVEL_WARNING, result);
  }

  @Test
  void testGetLevelError() {
    int result = LogServiceUtil.getLevel(LogServiceUtil.ERROR);
    Assertions.assertEquals(Logger.LOG_LEVEL_ERROR, result);
  }

  @Test
  void testGetLevelC() {
    int result = LogServiceUtil.getLevel(LogServiceUtil.CRITICAL);
    Assertions.assertEquals(Logger.LOG_LEVEL_CRITICAL, result);
  }
}

