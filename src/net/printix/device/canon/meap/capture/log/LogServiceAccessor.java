// ------------------------------------------------------------------------------
// Copyright (c) 2002-2024 Tungsten Automation. All rights reserved.
// Description: LogServiceAccessor
// ------------------------------------------------------------------------------


package net.printix.device.canon.meap.capture.log;

import com.canon.meap.security.LoginContext;
import com.canon.meap.service.log.LogService;
import com.canon.meap.service.log.Logger;
import java.io.IOException;
import java.io.PrintWriter;

public class LogServiceAccessor {


  private static LogServiceAccessor instance;

  public static LogServiceAccessor getInstance() {
    if (instance == null) {
      instance = new LogServiceAccessor();
    }
    return instance;
  }

  public LogServiceAccessor() {
  }

  /**
   * LogService instance
   */
  private LogService _logServiceInstance;

  /**
   * LoginContext
   */
  private LoginContext _loginContext = null;


  private Logger logger;

  private boolean deviceLogEnabled = true;

  private String deviceLogLevel = LogServiceUtil.DEBUG;

  public LogServiceAccessor(LogService _logServiceInstance) {
    this._logServiceInstance = _logServiceInstance;
    this.logger = _logServiceInstance.getLogger(LogService.LOGKIND_APP);
    this.instance = this;
  }

  public boolean isDeviceLogEnabled() {
    return deviceLogEnabled;
  }

  public void setDeviceLogEnabled(boolean deviceLogEnabled) {
    this.deviceLogEnabled = deviceLogEnabled;
  }

  public String getDeviceLogLevel() {
    return deviceLogLevel;
  }

  public void setDeviceLogLevel(String deviceLogLevel) {
    this.deviceLogLevel = deviceLogLevel;
  }

  public void log(String loglevel, String _logMessage, Throwable throwable) {

    if (!isDeviceLogEnabled()) {
      return;
    }

    if (LogServiceUtil.getLevel(deviceLogLevel.toUpperCase()) < LogServiceUtil.getLevel(loglevel)) {
      return;
    }
    // Trace message output on MEAP Application Log Management
    if (throwable != null) {
      String formattedLogMessage = formatLogMessage(_logMessage, throwable);
      logger.log(_loginContext, LogServiceUtil.getLevel(loglevel), formattedLogMessage);
    } else {
      logger.log(_loginContext, LogServiceUtil.getLevel(loglevel), _logMessage);
    }

  }


  private static String formatLogMessage(final String message, Throwable throwable) {
    StringBuffer sb = new StringBuffer();
    sb.append(message);
    if (throwable != null) {
      PrintWriter pw = new PrintWriter(new WritertoStringBuffer(sb));
      throwable.printStackTrace(pw);
    }
    return sb.toString().trim();
  }

  private static class WritertoStringBuffer extends java.io.Writer {

    private final StringBuffer mStringBuffer;

    /**
     * @see java.io.Writer#write(char[], int, int)
     */
    WritertoStringBuffer(final StringBuffer sb) {
      mStringBuffer = sb;
    }

    @Override
    public void write(final char[] arg0, final int arg1, final int arg2) throws IOException {
      mStringBuffer.append(arg0, arg1, arg2);
    }

    /**
     * @see java.io.Writer#flush()
     */
    @Override
    public void flush() throws IOException {
      //Intentionally left blank
      //intentionally empty
    }

    /**
     * @see java.io.Writer#close()
     */
    @Override
    public void close() throws IOException {
      //Intentionally left blank
      //intentionally empty
    }

    @Override
    public void write(final int i) {
      mStringBuffer.append((char) i);
    }

    @Override
    public void write(final String s) {
      mStringBuffer.append(s);
    }
  }


}
