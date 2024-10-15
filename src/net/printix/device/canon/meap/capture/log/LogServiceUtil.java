// ------------------------------------------------------------------------------
// Copyright (c) 2002-2024 Tungsten Automation. All rights reserved.
// Description: LogServiceUtil
// ------------------------------------------------------------------------------

package net.printix.device.canon.meap.capture.log;

import com.canon.meap.service.log.Logger;

public class LogServiceUtil {

  /**
   * Log level-CRITICAL
   */
  public static final String CRITICAL = "CRITICAL";

  /**
   * Log level-DEBUG
   */
  public static final String DEBUG = "DEBUG";

  /**
   * Log level-DEBUG
   */
  public static final String INFO = "INFO";

  /**
   * Log level-WARNING
   */
  public static final String WARNING = "WARNING";

  /**
   * Log level-ERROR
   */
  public static final String ERROR = "ERROR";

  /**
   * Log type-Application
   */
  protected static final String LOGKIND_APP = "Application";

  /**
   * Obtains log level
   *
   * @param level Log level value
   * @return Log level name
   */
  public static String getLevel(int level) {

    switch (level) {
      case Logger.LOG_LEVEL_DEBUG:
        return DEBUG;
      case Logger.LOG_LEVEL_INFO:
        return INFO;
      case Logger.LOG_LEVEL_WARNING:
        return WARNING;
      case Logger.LOG_LEVEL_ERROR:
        return ERROR;
      case Logger.LOG_LEVEL_CRITICAL:
        return CRITICAL;
      default:
        return INFO;
    }
  }

  /**
   * Obtains log level value
   *
   * @param level Log level name
   * @return Log level value
   */
  public static int getLevel(String level) {

    if (ERROR.equals(level)) {
      return Logger.LOG_LEVEL_ERROR;
    } else if (WARNING.equals(level)) {
      return Logger.LOG_LEVEL_WARNING;
    } else if (INFO.equals(level)) {
      return Logger.LOG_LEVEL_INFO;
    } else if (DEBUG.equals(level)) {
      return Logger.LOG_LEVEL_DEBUG;
    } else if (CRITICAL.equals(level)) {
      return Logger.LOG_LEVEL_CRITICAL;
    }
    return Logger.LOG_LEVEL_INFO;
  }
}
