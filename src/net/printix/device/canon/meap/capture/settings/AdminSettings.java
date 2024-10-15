// ------------------------------------------------------------------------------
// Copyright (c) 2002-2024 Tungsten Automation. All rights reserved.
// Description: AdminSettings
// ------------------------------------------------------------------------------
package net.printix.device.canon.meap.capture.settings;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class AdminSettings {
    String username;
    String password;
}
