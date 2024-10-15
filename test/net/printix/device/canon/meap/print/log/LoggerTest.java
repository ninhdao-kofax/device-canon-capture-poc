// ------------------------------------------------------------------------------
// Copyright (c) 2002-2024 Tungsten Automation. All rights reserved.
// Description: LoggerTest
// ------------------------------------------------------------------------------
package net.printix.device.canon.meap.print.log;

import com.canon.meap.service.log.LogService;
import net.printix.device.canon.meap.capture.log.ConsoleLogger;
import net.printix.device.canon.meap.capture.log.LogServiceAccessor;
import net.printix.device.canon.meap.capture.log.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class LoggerTest {

  @Mock
  ConsoleLogger log;
  @InjectMocks
  Logger logger;
  @Mock
  LogService _logServiceInstance;
  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    LogServiceAccessor logServiceAccessor = new LogServiceAccessor(_logServiceInstance);
    logServiceAccessor.setDeviceLogLevel("INFO");
    logServiceAccessor.setDeviceLogEnabled(true);
  }

  @Test
  void testC() {
    Logger.c("msg");
    LogServiceAccessor result = LogServiceAccessor.getInstance();
    Assertions.assertEquals(true, result.isDeviceLogEnabled());
  }

  @Test
  void testW() {
    Logger.w("msg");
    LogServiceAccessor result = LogServiceAccessor.getInstance();
    Assertions.assertEquals(true, result.isDeviceLogEnabled());
  }

  @Test
  void testI() {
    Logger.i("msg");
    LogServiceAccessor result = LogServiceAccessor.getInstance();
    Assertions.assertEquals(true, result.isDeviceLogEnabled());
  }

  @Test
  void testE() {
    Logger.e("msg");
    LogServiceAccessor result = LogServiceAccessor.getInstance();
    Assertions.assertEquals(true, result.isDeviceLogEnabled());
  }

  @Test
  void testE2() {
    Logger.e("msg", new Throwable());
    LogServiceAccessor result = LogServiceAccessor.getInstance();
    Assertions.assertEquals(true, result.isDeviceLogEnabled());
  }

  @Test
  void testD() {
    Logger.d("msg", new Throwable());
    LogServiceAccessor result = LogServiceAccessor.getInstance();
    Assertions.assertEquals(true, result.isDeviceLogEnabled());
  }

  @Test
  void testD2() {
    Logger.d("msg");
    LogServiceAccessor result = LogServiceAccessor.getInstance();
    Assertions.assertEquals(true, result.isDeviceLogEnabled());
  }

  @Test
  void testLog() {
    Logger.log("msg");
    LogServiceAccessor result = LogServiceAccessor.getInstance();
    Assertions.assertEquals(true, result.isDeviceLogEnabled());
  }

  @Test
  void testI2() {
    Logger.i("TAG", "msg");
    LogServiceAccessor result = LogServiceAccessor.getInstance();
    Assertions.assertEquals(true, result.isDeviceLogEnabled());
  }

  @Test
  void testE3() {
    Logger.e("TAG", "msg");
    LogServiceAccessor result = LogServiceAccessor.getInstance();
    Assertions.assertEquals(true, result.isDeviceLogEnabled());
  }

  @Test
  void testD3() {
    Logger.d("TAG", "msg");
    LogServiceAccessor result = LogServiceAccessor.getInstance();
    Assertions.assertEquals(true, result.isDeviceLogEnabled());
  }

  @Test
  void testW2() {
    Logger.w("TAG", "msg");
    LogServiceAccessor result = LogServiceAccessor.getInstance();
    Assertions.assertEquals(true, result.isDeviceLogEnabled());
  }

  @Test
  void testC2() {
    Logger.c("TAG", "msg");
    LogServiceAccessor result = LogServiceAccessor.getInstance();
    Assertions.assertEquals(true, result.isDeviceLogEnabled());
  }
}
