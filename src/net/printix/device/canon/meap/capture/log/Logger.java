// ------------------------------------------------------------------------------
// Copyright (c) 2002-2024 Tungsten Automation. All rights reserved.
// Description: Logger
// ------------------------------------------------------------------------------


package net.printix.device.canon.meap.capture.log;

import static net.printix.device.canon.meap.capture.settings.AppConstants.MEAP_CAPTURE;

import java.util.Date;
import lombok.Synchronized;

public class Logger {
  private static final ConsoleLogger log = new ConsoleLogger(MEAP_CAPTURE);
  @Synchronized
  private static void writeLog(LogMessage logMsg, boolean forced) {
    log.log(logMsg.getMessage());
    //Device log
    LogServiceAccessor.getInstance()
        .log(logMsg.getLevel(), logMsg.getMessage(), logMsg.getThrowable());

  }

  private static void log(String level, String msg, Throwable throwable, boolean forced) {
    try {
      StackTraceElement[] callStack = new Throwable().getStackTrace();
      StackTraceElement callPoint = callStack.length > 2 ? callStack[2] : null;
      writeLog(LogMessage.builder().date(new Date()).level(level)
          .threadName(Thread.currentThread().getName()).message(msg).callPoint(callPoint)
          .throwable(throwable).build(), forced);
    } catch (Throwable t) {
      // nothing
    }
  }

  public static void c(String msg) {
    log(LogServiceUtil.CRITICAL, MEAP_CAPTURE + msg, null, false);
  }

  public static void w(String msg) {
    log(LogServiceUtil.WARNING, MEAP_CAPTURE + msg, null, false);
  }

  public static void i(String msg) {
    log(LogServiceUtil.INFO, MEAP_CAPTURE + msg, null, false);
  }

  public static void e(String msg) {
    log(LogServiceUtil.ERROR, MEAP_CAPTURE + msg, null, false);
  }

  public static void e(String msg, Throwable t) {
    log(LogServiceUtil.ERROR, MEAP_CAPTURE + msg, t, false);
  }

  public static void d(String msg, Throwable t) {
    log(LogServiceUtil.DEBUG, MEAP_CAPTURE + msg, t, true);
  }

  public static void d(String msg) {
    log(LogServiceUtil.DEBUG, MEAP_CAPTURE + msg, null, true);
  }

  public static void log(String msg) {
    log(LogServiceUtil.INFO, MEAP_CAPTURE + msg, null, false);
  }

  public static void i(String TAG, String msg) {
    log(LogServiceUtil.INFO, MEAP_CAPTURE + TAG + ": " + msg, null, false);
  }

  public static void e(String TAG, String msg) {
    log(LogServiceUtil.ERROR, MEAP_CAPTURE + TAG + ": " + msg, null, false);
  }

  public static void d(String TAG, String msg) {
    log(LogServiceUtil.DEBUG, MEAP_CAPTURE + TAG + ": " + msg, null, true);
  }

  public static void w(String TAG, String msg) {
    log(LogServiceUtil.WARNING, MEAP_CAPTURE + TAG + ": " + msg, null, false);
  }

  public static void c(String TAG, String msg) {
    log(LogServiceUtil.CRITICAL, MEAP_CAPTURE + TAG + ": " + msg, null, true);
  }

}
