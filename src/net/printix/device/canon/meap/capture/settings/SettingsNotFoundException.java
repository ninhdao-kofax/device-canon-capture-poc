//------------------------------------------------------------------------------
// Copyright (c) 2002-2024 Tungsten Automation. All rights reserved.
// Description: SettingsNotFoundException
//------------------------------------------------------------------------------
package net.printix.device.canon.meap.capture.settings;

public class SettingsNotFoundException extends Exception {

    public SettingsNotFoundException(String message) {
        super(message, null);
    }
}
