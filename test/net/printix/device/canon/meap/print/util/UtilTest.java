// ------------------------------------------------------------------------------
// Copyright (c) 2002-2024 Tungsten Automation. All rights reserved.
// Description: UtilTest
// ------------------------------------------------------------------------------
package net.printix.device.canon.meap.print.util;


import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import net.printix.device.canon.meap.capture.util.Util;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class UtilTest {

  @Test
  void testCalculateSignature() throws NoSuchAlgorithmException, InvalidKeyException {
    List<String> list = new ArrayList<>();
    list.add("String");
    List<String> result = Util.calculateSignature(list, "timestamp", "method",
        "requestPathAndQuery", "requestBody");
    Assertions.assertEquals("xLUKN1ihCpLC89TgOnAIy1Qr6kU=", result.get(0));
  }

  @Test
  void testCompareTimestampWithinTolerance() {
    int result = Util.compareTimestampWithinTolerance(-10L);
    Assertions.assertEquals(-1, result);
  }

  @Test
  void testCompareTimestamp() {
    int result = Util.compareTimestampWithinTolerance(10000000000L);
    Assertions.assertEquals(1, result);
  }

}

