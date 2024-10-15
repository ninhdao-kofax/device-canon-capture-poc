// ------------------------------------------------------------------------------
// Copyright (c) 2002-2024 Tungsten Automation. All rights reserved.
// Description: ConsoleLoggerTest
// ------------------------------------------------------------------------------
package net.printix.device.canon.meap.print.log;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import net.printix.device.canon.meap.capture.log.ConsoleLogger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class ConsoleLoggerTest {

  ConsoleLogger consoleLogger = new ConsoleLogger("componentName");
  private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
  private final PrintStream originalOut = System.out;
  @BeforeEach
  public void setUpStreams() {
    System.setOut(new PrintStream(outContent));
  }

  @AfterEach
  public void restoreStreams() {
    System.setOut(originalOut);
  }
  @Test
  void testLog() {
    ConsoleLogger console = spy(consoleLogger);
    console.log("hello");
    assertTrue(outContent.toString().contains("hello"));
  }

  @Test
  void testLog2() {
    ConsoleLogger console = spy(consoleLogger);
    Throwable t = new Throwable();
    console.log("message", t);
    assertTrue(outContent.toString().contains("message"));
  }
}

