//------------------------------------------------------------------------------
// Copyright (c) 2002-2024 Tungsten Automation. All rights reserved.
// Description: AppSettings
//------------------------------------------------------------------------------

package net.printix.device.canon.meap.capture.settings;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class AppSettings {
    String tenantId;
    String printerId;
    String deviceCanonHost;
    String deviceSecret;
    boolean deviceLogEnable;
    String logLevel;

    @Override
    public String toString() {
        return "AppSettings [tenantId=" + tenantId + ", printerId=" + printerId + ", deviceCanonHost=" + deviceCanonHost
                + ", deviceLogEnable=" + deviceLogEnable + ", logLevel=" + logLevel + "]";
    }
}
