// ------------------------------------------------------------------------------
// Copyright (c) 2002-2024 Tungsten Automation. All rights reserved.
// Description: BadRequestException
// ------------------------------------------------------------------------------

package net.printix.device.canon.meap.capture.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.json.JSONObject;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BadRequestException extends Exception {

    private int errorCode;
    private String errorMessage;
    private String exceptionDetails;

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("errorCode", getErrorCode());
        jsonObject.put("errorMessage", getErrorMessage());
        jsonObject.put("exceptionDetails", getExceptionDetails());
        return jsonObject;
    }
}