// ------------------------------------------------------------------------------
// Copyright (c) 2002-2024 Tungsten Automation. All rights reserved.
// Description: PrintBundleActivatorTest
// ------------------------------------------------------------------------------
package net.printix.device.canon.meap.print;

import com.canon.meap.service.avs.AppletViewerService;
import com.canon.meap.service.conf.Configuration;
import com.canon.meap.service.conf.ConfigurationService;
import com.canon.meap.service.http.HttpExtendedService;
import com.canon.meap.service.log.LogService;
import com.canon.meap.service.login.LocalLoginService;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.printix.device.canon.meap.capture.CaptureBundleActivator;
import net.printix.device.canon.meap.capture.settingmanager.CaptureSettingsManager;
import net.printix.device.canon.meap.capture.settings.AdminSettingsProvider;
import net.printix.device.canon.meap.capture.settings.AppConstants;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.NamespaceException;

import static org.mockito.Mockito.*;

class PrintBundleActivatorTest {

  @Mock
  BundleContext bundleContext;
  @Mock
  ServiceReference appletViewerServiceReference;
  @Mock
  ServiceReference httpsServiceReference;
  @Mock
  AppletViewerService appletViewerService;
  @Mock
  ConfigurationService configService;

  HttpExtendedService httpsService;
  @InjectMocks
  CaptureBundleActivator printBundleActivator;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @AfterEach
  void tearDown() {
    CaptureSettingsManager.setInstance(null);
  }

  @Test
  void testStartJava() throws Exception {
    System.setProperty("meap.spec_version", "Mocked String");
    Assertions.assertThrows(Exception.class,
        () -> printBundleActivator.start(bundleContext));
  }

  @Test
  void testStartConfigurationServiceReference() throws Exception {
    System.setProperty("meap.spec_version", "113");
    ServiceReference _logServiceRef = mock(ServiceReference.class);
    LogService _logServiceInstance = mock(LogService.class);
    when(bundleContext.getServiceReference(LogService.class.getName())).thenReturn(_logServiceRef);
    when(bundleContext.getService(_logServiceRef)).thenReturn(_logServiceInstance);
    Assertions.assertThrows(Exception.class,
        () -> printBundleActivator.start(bundleContext));
  }

  @Test
  void testStart() throws Exception {
    System.setProperty("meap.spec_version", "113");
    ServiceReference _logServiceRef = mock(ServiceReference.class);
    LogService _logServiceInstance = mock(LogService.class);
    when(bundleContext.getServiceReference(LogService.class.getName())).thenReturn(_logServiceRef);
    when(bundleContext.getService(_logServiceRef)).thenReturn(_logServiceInstance);

    String serviceNameConfig = ConfigurationService.class.getName();
    ServiceReference configurationServiceReference = mock(ServiceReference.class);
    when(bundleContext.getServiceReference(
        serviceNameConfig)).thenReturn(configurationServiceReference);
    when(bundleContext.getService(
        configurationServiceReference)).thenReturn(configService);

    AdminSettingsProvider adminSettingsProvider = new AdminSettingsProvider(configService);
    Configuration configuration = mock(Configuration.class);
    when(configService.getApplicationConfiguration(AppConstants.ADMIN_CONFIG_KEY)).thenReturn(
        configuration);
    Map propertiesFromCache = new HashMap<>();
    propertiesFromCache.put(AppConstants.USER_NAME,
        "8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918");
    propertiesFromCache.put(AppConstants.PASSWORD,
        "84d1480d9a6902a7229d3f895e3b453f76e4a9e974e2d975ee51ec8534af08e3");
    when(configuration.getProperties()).thenReturn(propertiesFromCache);

    String serviceNamehttps = "com.canon.meap.service.http.HttpExtendedService";
    when(bundleContext.getServiceReference(serviceNamehttps)).thenReturn(httpsServiceReference);
    httpsService = new HttpExtendedService() {
      @Override
      public void registerServlet(String s, Servlet servlet, int i, Dictionary dictionary,
          HttpContext httpContext) throws ServletException, NamespaceException {

      }

      @Override
      public void registerResources(String s, String s1, int i, HttpContext httpContext)
          throws NamespaceException {

      }

      @Override
      public void unregister(String s, int i) {

      }

      @Override
      public void registerFilter(javax.servlet.Filter filter, int i, String s,
          Dictionary dictionary, int i1, HttpContext httpContext) throws ServletException {

      }

      @Override
      public void unregisterFilter(javax.servlet.Filter filter, int i) {

      }

      @Override
      public boolean checkHttpSession(HttpServletRequest httpServletRequest,
          HttpServletResponse httpServletResponse) throws IOException {
        return false;
      }

      @Override
      public void registerServlet(String s, Servlet servlet, Dictionary dictionary,
          HttpContext httpContext) throws ServletException, NamespaceException {

      }

      @Override
      public void registerResources(String s, String s1, HttpContext httpContext)
          throws NamespaceException {

      }

      @Override
      public void unregister(String s) {

      }

      @Override
      public HttpContext createDefaultHttpContext() {
        return null;
      }
    };
    when(bundleContext.getService(httpsServiceReference)).thenReturn(httpsService);

    String serviceNameApplet = AppletViewerService.class.getName();
    when(bundleContext.getServiceReference(serviceNameApplet)).thenReturn(
        appletViewerServiceReference);
    when(bundleContext.getService(appletViewerServiceReference)).thenReturn(appletViewerService);

    ServiceReference ilsServiceReference = mock(ServiceReference.class);
    when(bundleContext.getServiceReference(
        "com.canon.meap.service.login.LocalLoginService")).thenReturn(ilsServiceReference);
    LocalLoginService localLoginService = mock(LocalLoginService.class);
    when(bundleContext.getService(ilsServiceReference)).thenReturn(localLoginService);

    printBundleActivator.start(bundleContext);
    Field field = CaptureBundleActivator.class.getDeclaredField("isNiR");
    field.setAccessible(true);
    Object value = field.get(printBundleActivator);
    Assertions.assertFalse((Boolean) value);
  }

  @Test
  void testStop() throws Exception {
    testStart();
    when(bundleContext.ungetService(httpsServiceReference)).thenReturn(true);
    when(bundleContext.ungetService(appletViewerServiceReference)).thenReturn(true);
    printBundleActivator.stop(bundleContext);

    Field fieldStop = CaptureBundleActivator.class.getDeclaredField("isNiR");
    fieldStop.setAccessible(true);
    Object valueStop = fieldStop.get(printBundleActivator);
    Assertions.assertFalse((Boolean) valueStop);
  }

  @Test
  void testStartNIR() throws Exception {
    System.setProperty("meap.spec_version", "113,33");
    ServiceReference _logServiceRef = mock(ServiceReference.class);
    LogService _logServiceInstance = mock(LogService.class);
    when(bundleContext.getServiceReference(LogService.class.getName())).thenReturn(_logServiceRef);
    when(bundleContext.getService(_logServiceRef)).thenReturn(_logServiceInstance);

    String serviceNameConfig = ConfigurationService.class.getName();
    ServiceReference configurationServiceReference = mock(ServiceReference.class);
    when(bundleContext.getServiceReference(
        serviceNameConfig)).thenReturn(configurationServiceReference);
    when(bundleContext.getService(
        configurationServiceReference)).thenReturn(configService);

    AdminSettingsProvider adminSettingsProvider = new AdminSettingsProvider(configService);
    Configuration configuration = mock(Configuration.class);
    when(configService.getApplicationConfiguration(AppConstants.ADMIN_CONFIG_KEY)).thenReturn(
        configuration);
    Map propertiesFromCache = new HashMap<>();
    propertiesFromCache.put(AppConstants.USER_NAME,
        "8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918");
    propertiesFromCache.put(AppConstants.PASSWORD,
        "84d1480d9a6902a7229d3f895e3b453f76e4a9e974e2d975ee51ec8534af08e3");
    when(configuration.getProperties()).thenReturn(propertiesFromCache);

    String serviceNamehttps = "com.canon.meap.service.http.HttpExtendedService";
    when(bundleContext.getServiceReference(serviceNamehttps)).thenReturn(httpsServiceReference);
    httpsService = new HttpExtendedService() {
      @Override
      public void registerServlet(String s, Servlet servlet, int i, Dictionary dictionary,
          HttpContext httpContext) throws ServletException, NamespaceException {

      }

      @Override
      public void registerResources(String s, String s1, int i, HttpContext httpContext)
          throws NamespaceException {

      }

      @Override
      public void unregister(String s, int i) {

      }

      @Override
      public void registerFilter(javax.servlet.Filter filter, int i, String s,
          Dictionary dictionary, int i1, HttpContext httpContext) throws ServletException {

      }

      @Override
      public void unregisterFilter(javax.servlet.Filter filter, int i) {

      }

      @Override
      public boolean checkHttpSession(HttpServletRequest httpServletRequest,
          HttpServletResponse httpServletResponse) throws IOException {
        return false;
      }

      @Override
      public void registerServlet(String s, Servlet servlet, Dictionary dictionary,
          HttpContext httpContext) throws ServletException, NamespaceException {

      }

      @Override
      public void registerResources(String s, String s1, HttpContext httpContext)
          throws NamespaceException {

      }

      @Override
      public void unregister(String s) {

      }

      @Override
      public HttpContext createDefaultHttpContext() {
        return null;
      }
    };
    when(bundleContext.getService(httpsServiceReference)).thenReturn(httpsService);

    String serviceNameApplet = AppletViewerService.class.getName();
    when(bundleContext.getServiceReference(serviceNameApplet)).thenReturn(
        appletViewerServiceReference);
    when(bundleContext.getService(appletViewerServiceReference)).thenReturn(appletViewerService);

    ServiceReference ilsServiceReference = mock(ServiceReference.class);
    when(bundleContext.getServiceReference(
        "com.canon.meap.service.login.LocalLoginService")).thenReturn(ilsServiceReference);
    LocalLoginService localLoginService = mock(LocalLoginService.class);
    when(bundleContext.getService(ilsServiceReference)).thenReturn(localLoginService);

    printBundleActivator.start(bundleContext);
    Field field = CaptureBundleActivator.class.getDeclaredField("isNiR");
    field.setAccessible(true);
    Object value = field.get(printBundleActivator);
    Assertions.assertTrue((Boolean) value);
  }

  @Test
  void testStopNIR() throws Exception {
    testStartNIR();
    when(bundleContext.ungetService(httpsServiceReference)).thenReturn(true);
    when(bundleContext.ungetService(appletViewerServiceReference)).thenReturn(true);
    printBundleActivator.stop(bundleContext);

    Field fieldStop = CaptureBundleActivator.class.getDeclaredField("isNiR");
    fieldStop.setAccessible(true);
    Object valueStop = fieldStop.get(printBundleActivator);
    Assertions.assertTrue((Boolean) valueStop);
  }
}

