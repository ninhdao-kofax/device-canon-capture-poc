//------------------------------------------------------------------------------
// Copyright (c) 2002-2024 Tungsten Automation. All rights reserved.
// Description: BadRequestException
//------------------------------------------------------------------------------

package net.printix.device.canon.meap.capture.settings;

import org.json.JSONObject;

public class BadRequestException {
    private int errorCode;
    private String errorMessage;
    private String exceptionDetails;

    public BadRequestException(int errorCode, String errorMessage, String exceptionDetails) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.exceptionDetails = exceptionDetails;
    }

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("errorCode", errorCode);
        jsonObject.put("errorMessage", errorMessage);
        jsonObject.put("exceptionDetails", exceptionDetails);

        return jsonObject;
    }
}
