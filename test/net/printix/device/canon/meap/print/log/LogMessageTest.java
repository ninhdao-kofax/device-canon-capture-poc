// ------------------------------------------------------------------------------
// Copyright (c) 2002-2024 Tungsten Automation. All rights reserved.
// Description: LogMessageTest
// ------------------------------------------------------------------------------
package net.printix.device.canon.meap.print.log;

import java.util.Date;
import net.printix.device.canon.meap.capture.log.LogMessage;
import net.printix.device.canon.meap.capture.log.LogMessage.LogMessageBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class LogMessageTest {

  @Mock
  Date date = new Date();

  @InjectMocks
  LogMessage logMessage;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    LogMessageBuilder logMessageBuilder = new LogMessageBuilder();
    logMessage = logMessageBuilder.threadName("threadName").date(date).message("message")
        .level("level").build();
  }

  @Test
  void testGetThreadNameStr() {

    String result = logMessage.getThreadNameStr();
    Assertions.assertEquals("threadName", result);
  }

  @Test
  void testGetCallPointStr() {

    LogMessageBuilder logMessageBuilder = new LogMessageBuilder();
    StackTraceElement stackTraceElement =
        new StackTraceElement("name", "name", "moduleversion",
            10);
    logMessage = logMessageBuilder.threadName("threadName").date(date).message("message")
        .level("level").callPoint(stackTraceElement).build();

    String result = logMessage.getCallPointStr();
    Assertions.assertTrue(result.contains("name"));
  }

  @Test
  void testGetThrowableStr() {
    String result = logMessage.getThrowableStr();
    Assertions.assertEquals("", result);
  }

  @Test
  void testToString() {
    String result = logMessage.toString();
    Assertions.assertTrue(result.contains("level"));
  }

  @Test
  void testToStringThrowable() {
    Throwable throwable1 = new Throwable();
    LogMessageBuilder a = new LogMessageBuilder();
    logMessage = a.threadName("threadName").date(date).message("message").level("level")
        .throwable(throwable1).build();
    String result = logMessage.toString();
    Assertions.assertTrue(result.contains("level"));
  }

  @Test
  void testEquals() {
    boolean result = logMessage.equals("o");
    Assertions.assertEquals(false, result);
  }

  @Test
  void testHashCode() {
    String result = logMessage.getMessage();
    Assertions.assertEquals("message", result);
  }
}

