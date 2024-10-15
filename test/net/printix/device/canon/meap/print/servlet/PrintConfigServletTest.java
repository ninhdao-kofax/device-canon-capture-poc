// ------------------------------------------------------------------------------
// Copyright (c) 2002-2024 Tungsten Automation. All rights reserved.
// Description: PrintConfigServletTest
// ------------------------------------------------------------------------------
package net.printix.device.canon.meap.print.servlet;

import com.canon.meap.service.conf.Configuration;
import com.canon.meap.service.conf.ConfigurationService;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.printix.device.canon.meap.capture.servlet.CaptureConfigServlet;
import net.printix.device.canon.meap.capture.settings.AdminSettingsProvider;
import net.printix.device.canon.meap.capture.settings.AppConstants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static net.printix.device.canon.meap.capture.settings.SettingsProvider.DEVICE_CANON_HOST;
import static net.printix.device.canon.meap.capture.settings.SettingsProvider.DEVICE_LOG_ENABLE;
import static net.printix.device.canon.meap.capture.settings.SettingsProvider.DEVICE_LOG_LEVEL;
import static net.printix.device.canon.meap.capture.settings.SettingsProvider.PRINTER_ID;
import static net.printix.device.canon.meap.capture.settings.SettingsProvider.PRINTER_SECRET;
import static net.printix.device.canon.meap.capture.settings.SettingsProvider.TENANT_ID;
import static org.mockito.Mockito.*;

class PrintConfigServletTest {

  @Mock
  ConfigurationService configService;
  @Mock
  ResourceBundle lStrings;
  @Mock
  ServletConfig config;
  @InjectMocks
  CaptureConfigServlet printConfigServlet;

  @Mock
  HttpServletRequest request;

  @Mock
  HttpServletResponse response;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testDoGet() throws IOException {
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
    final String[] result = new String[1];
    when(response.getWriter()).thenReturn(new PrintWriter(new Writer() {
      @Override
      public void write(char[] cbuf, int off, int len) throws IOException {
        result[0] = new String(cbuf);
      }

      @Override
      public void flush() throws IOException {

      }

      @Override
      public void close() throws IOException {

      }
    }));

    printConfigServlet.doGet(request, response);
    Assertions.assertTrue(result[0].contains("deviceLogEnable"));
  }

  @Test
  void testDoGetEror() throws IOException {
    Configuration targetConfig = mock(Configuration.class);
    when(configService.getApplicationConfiguration(AppConstants.PRINT_CONFIG_KEY)).thenReturn(
        targetConfig);
    Map properties = new HashMap();
    when(targetConfig.getProperties()).thenReturn(properties);
    final String[] result = new String[1];
    when(response.getWriter()).thenReturn(new PrintWriter(new Writer() {
      @Override
      public void write(char[] cbuf, int off, int len) throws IOException {
        result[0] = new String(cbuf);
      }

      @Override
      public void flush() throws IOException {

      }

      @Override
      public void close() throws IOException {

      }
    }));
    printConfigServlet.doGet(request, response);
    Assertions.assertTrue(result[0].contains("exception"));
  }

  @Test
  void testDoPostRemoteAddr() throws IOException {
    when(request.getRemoteAddr()).thenReturn("???");
    printConfigServlet.doPost(request, response);
    verify(response, times(1)).sendError(HttpServletResponse.SC_FORBIDDEN,"Error: The request is not accepted.");
  }

  @Test
  void testDoPostAccessToken() throws IOException {
    when(request.getRemoteAddr()).thenReturn("127.0.0.1");
    final String[] result = new String[1];
    when(response.getWriter()).thenReturn(new PrintWriter(new Writer() {
      @Override
      public void write(char[] cbuf, int off, int len) throws IOException {
        result[0] = new String(cbuf);
      }

      @Override
      public void flush() throws IOException {

      }

      @Override
      public void close() throws IOException {

      }
    }));
    printConfigServlet.doPost(request, response);
    Assertions.assertTrue(result[0].contains("access"));
  }

  @Test
  void testDoPostJson() throws IOException {
    when(request.getRemoteAddr()).thenReturn("127.0.0.1");
    final String[] result = new String[1];
    when(response.getWriter()).thenReturn(new PrintWriter(new Writer() {
      @Override
      public void write(char[] cbuf, int off, int len) throws IOException {
        result[0] = new String(cbuf);
      }

      @Override
      public void flush() throws IOException {

      }

      @Override
      public void close() throws IOException {

      }
    }));
    AdminSettingsProvider adminSettingsProvider = new AdminSettingsProvider(configService);
    Configuration configuration = mock(Configuration.class);
    when(configService.getApplicationConfiguration(AppConstants.ADMIN_CONFIG_KEY)).thenReturn(configuration);
    when(request.getHeader(AppConstants.AUTHORIZATION)).thenReturn("Basic OGM2OTc2ZTViNTQxMDQxNWJkZTkwOGJkNGRlZTE1ZGZiMTY3YTljODczZmM0YmI4YTgxZjZmMmFiNDQ4YTkxODo4NGQxNDgwZDlhNjkwMmE3MjI5ZDNmODk1ZTNiNDUzZjc2ZTRhOWU5NzRlMmQ5NzVlZTUxZWM4NTM0YWYwOGUz");
    Map propertiesFromCache = new HashMap<>();
    propertiesFromCache.put(AppConstants.USER_NAME,"8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918");
    propertiesFromCache.put(AppConstants.PASSWORD,"84d1480d9a6902a7229d3f895e3b453f76e4a9e974e2d975ee51ec8534af08e3");
    when(configuration.getProperties()).thenReturn(propertiesFromCache);
    printConfigServlet.doPost(request, response);
    verify(response, times(1)).sendError(HttpServletResponse.SC_BAD_REQUEST,"Error: The body is required.");
  }

}

