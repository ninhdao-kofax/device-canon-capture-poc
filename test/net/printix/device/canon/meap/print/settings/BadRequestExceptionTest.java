// ------------------------------------------------------------------------------
// Copyright (c) 2002-2024 Tungsten Automation. All rights reserved.
// Description: BadRequestExceptionTest
// ------------------------------------------------------------------------------
package net.printix.device.canon.meap.print.settings;

import net.printix.device.canon.meap.capture.settings.BadRequestException;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class BadRequestExceptionTest {

  BadRequestException badRequestException = new BadRequestException(0, "errorMessage",
      "exceptionDetails");

  @Test
  void testToJson() {
    JSONObject result = badRequestException.toJson();
    Assertions.assertEquals("errorMessage", result.get("errorMessage"));
    Assertions.assertEquals("exceptionDetails", result.get("exceptionDetails"));
  }
}

