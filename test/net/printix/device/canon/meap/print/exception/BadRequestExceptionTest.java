// ------------------------------------------------------------------------------
// Copyright (c) 2002-2024 Tungsten Automation. All rights reserved.
// Description: BadRequestExceptionTest
// ------------------------------------------------------------------------------
package net.printix.device.canon.meap.print.exception;

import net.printix.device.canon.meap.capture.exception.BadRequestException;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

class BadRequestExceptionTest {

  @InjectMocks
  BadRequestException badRequestException;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    badRequestException.setErrorCode(0);
    badRequestException.setErrorMessage("errorMessage");
    badRequestException.setExceptionDetails("exceptionDetails");
  }

  @Test
  void testToJson() {
    JSONObject result = badRequestException.toJson();
    Assertions.assertEquals("errorMessage", result.get("errorMessage"));
    Assertions.assertEquals("exceptionDetails", result.get("exceptionDetails"));
  }

}

