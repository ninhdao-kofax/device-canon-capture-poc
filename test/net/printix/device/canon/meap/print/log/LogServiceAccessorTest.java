// ------------------------------------------------------------------------------
// Copyright (c) 2002-2024 Tungsten Automation. All rights reserved.
// Description: LogServiceAccessorTest
// ------------------------------------------------------------------------------
package net.printix.device.canon.meap.print.log;

import com.canon.meap.security.LoginContext;
import com.canon.meap.service.log.LogService;
import com.canon.meap.service.log.Logger;
import net.printix.device.canon.meap.capture.log.LogServiceAccessor;
import net.printix.device.canon.meap.capture.log.LogServiceUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

class LogServiceAccessorTest {

  @Mock
  LogServiceAccessor instance;
  @Mock
  LogService _logServiceInstance;
  @Mock
  LoginContext _loginContext;
  @Mock
  Logger logger;
  @InjectMocks
  LogServiceAccessor logServiceAccessor;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    when(_logServiceInstance.getLogger(LogService.LOGKIND_APP)).thenReturn(logger);
    logServiceAccessor = new LogServiceAccessor(_logServiceInstance);
    logServiceAccessor.setDeviceLogLevel("DEBUG");
    logServiceAccessor.setDeviceLogEnabled(true);
  }

  @Test
  void testGetInstance() {
    LogServiceAccessor result = LogServiceAccessor.getInstance();
    Assertions.assertEquals(true, result.isDeviceLogEnabled());
  }

  @Test
  void testDeviceLogDisabled() {
    logServiceAccessor.setDeviceLogEnabled(false);
    logServiceAccessor.log("loglevel", "_logMessage", null);
    LogServiceAccessor result = LogServiceAccessor.getInstance();
    Assertions.assertEquals(false, result.isDeviceLogEnabled());
  }

  @Test
  void testDeviceLogEnabled() {
    logServiceAccessor.setDeviceLogEnabled(true);
    logServiceAccessor.setDeviceLogLevel(LogServiceUtil.WARNING);
    logServiceAccessor.log("loglevel", "_logMessage", null);
    LogServiceAccessor result = LogServiceAccessor.getInstance();
    Assertions.assertEquals(true, result.isDeviceLogEnabled());
  }

  @Test
  void testDeviceLog() {
    logServiceAccessor.log("loglevel", "_logMessage", null);
    LogServiceAccessor result = LogServiceAccessor.getInstance();
    Assertions.assertEquals(true, result.isDeviceLogEnabled());
  }

  @Test
  void testDeviceLogthrowable() {
    logServiceAccessor.log("loglevel", "_logMessage", new Throwable());
    LogServiceAccessor result = LogServiceAccessor.getInstance();
    Assertions.assertEquals(true, result.isDeviceLogEnabled());
  }
}

