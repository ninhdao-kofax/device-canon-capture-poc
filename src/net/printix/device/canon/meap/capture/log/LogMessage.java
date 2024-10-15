// ------------------------------------------------------------------------------
// Copyright (c) 2002-2024 Tungsten Automation. All rights reserved.
// Description: LogMessage
// ------------------------------------------------------------------------------


package net.printix.device.canon.meap.capture.log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import lombok.Builder;
import lombok.Synchronized;
import lombok.Value;

@Value
@Builder
class LogMessage {

  Date date;
  String level;
  String threadName;
  String message;
  StackTraceElement callPoint;
  Throwable throwable;

  private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS",
      Locale.ENGLISH);
  private static final String NEW_LINE = "\n";
  private static final int THREADNAME_LEN = 15;
  private static final int CALLPOINT_LEN = 45;

  @Synchronized
  private static String getTimeStamp(Date date) {
    // SDF.format is not thread safe
    return SDF.format(date);
  }

  private static StringBuilder append(StringBuilder sb, String s, int len) {
    sb.append(s);
    for (; s.length() < len; len--) {
      sb.append(' ');
    }
    return sb;
  }

  public String getDateStr() {
    return getTimeStamp(date);
  }

  public String getThreadNameStr() {
    int len = Math.min(threadName.length(), THREADNAME_LEN);
    return threadName.substring(0, len);
  }

  public String getCallPointStr() {
    StringBuilder sb = new StringBuilder();

    if (callPoint != null) {
      String[] packageNames = callPoint.getClassName().split("\\.");
      int packagesLen = packageNames.length;
      if (packagesLen > 0) {
        for (int i = 0; i + 1 < packageNames.length; i++) {
          String s = packageNames[i];
          if (s.length() > 0) {
            sb.append(s.charAt(0));
          }
          sb.append('.');
        }
        sb.append(packageNames[packagesLen - 1]).append('.');
      }
      sb.append(callPoint.getMethodName()).append(':').append(callPoint.getLineNumber())
          .append(" ");
    }

    return sb.toString();
  }

  public String getThrowableStr() {
    StringBuilder sb = new StringBuilder();

    if (throwable != null) {
      StringWriter writer = new StringWriter();
      PrintWriter pWriter = new PrintWriter(writer);
      throwable.printStackTrace(pWriter);
      sb.append(NEW_LINE);
      sb.append(writer.toString().trim());
    }

    return sb.toString();
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();

    sb.append(getDateStr()).append(" ");
    append(sb, level, 6).append("[");
    append(sb, getThreadNameStr(), THREADNAME_LEN).append("] ");
    append(sb, getCallPointStr(), CALLPOINT_LEN).append("| ");
    sb.append(message);
    if (throwable != null) {
      sb.append(getThrowableStr());
    }

    return sb.toString();
  }
}
