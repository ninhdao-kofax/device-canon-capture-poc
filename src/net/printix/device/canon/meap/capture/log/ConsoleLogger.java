//------------------------------------------------------------------------------
// Copyright (c) 2002-2024 Tungsten Automation. All rights reserved.
// Description: ConsoleLogger
//------------------------------------------------------------------------------

package net.printix.device.canon.meap.capture.log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ConsoleLogger {

  private String componentName;

  private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS",
      Locale.ENGLISH);

  public ConsoleLogger(String componentName) {
    this.componentName = componentName;
  }

  public void log(String message) {
    System.out.println(String.format("%s %s: %s", SDF.format(new Date()), componentName, message));
  }

  public void log(String message, Throwable t) {
    System.out.println(String.format("%s %s: %s", SDF.format(new Date()), componentName, message));

    if (t != null) {
      t.printStackTrace();
    }
  }
}
