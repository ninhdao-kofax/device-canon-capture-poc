//------------------------------------------------------------------------------
// Copyright (c) 2002-2024 Tungsten Automation. All rights reserved.
// Description: PrintConfigServlet
//------------------------------------------------------------------------------

package net.printix.device.canon.meap.capture.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.printix.device.canon.meap.capture.IServiceFinder;
import net.printix.device.canon.meap.capture.log.Logger;
import net.printix.device.canon.meap.capture.settings.AdminSettings;
import net.printix.device.canon.meap.capture.settings.AppConstants;
import net.printix.device.canon.meap.capture.settings.SettingsNotFoundException;
import net.printix.device.canon.meap.capture.util.Util;
import net.printix.device.canon.meap.capture.util.Util.PostContentType;
import org.json.JSONObject;

import com.canon.meap.service.conf.ConfigurationService;

import net.printix.device.canon.meap.capture.settings.AppSettings;
import net.printix.device.canon.meap.capture.settings.BadRequestException;

public class CaptureConfigServlet extends HttpServlet {
    private final static String TAG = CaptureConfigServlet.class.getSimpleName();
    private ConfigurationService configService;
    private static final String CLEAR_CACHE = "ClearCache";
    private IServiceFinder serviceFinder;

    public CaptureConfigServlet(ConfigurationService configService, IServiceFinder serviceFinder) {
        this.configService = configService;
        this.serviceFinder = serviceFinder;
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Logger.i(TAG, "doGet start");

        try {
            // Get app config from device
            AppSettings appSettings = serviceFinder.getSettingsProvider().getConfigurationValue(configService);
            Logger.d("appSettings: " + appSettings);

            // Response
            handleResponse(response, appSettings);
        } catch (Exception e) {
            Logger.e(TAG, "doGet error: " + e);
            handleBadRequest(response, e);
        }

        Logger.i(TAG, "doGet end");
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Logger.i(TAG, "doPost start");
        String jsonData = Util.getRequestData(request);

        if (!request.getRemoteAddr().contains("127.0.0.1")){
            String message = "Error: The request is not accepted.";
            Logger.e(TAG, message);
            response.sendError(HttpServletResponse.SC_FORBIDDEN, message);
            return;
        }
        if (!Util.getAccessToken(request, serviceFinder)) {
            Util.handleBadRequest(response, new Exception("The access token is not matched!"));
            return;
        }
        //Validate body
        if (jsonData == null){
            String message = "Error: The body is required.";
            Logger.e(TAG, message);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, message);
            return;
        }
        //check timestamp
        String timestamp = request.getHeader("timestamp");
        if (timestamp != null) {
            int timestampStatus = Util.compareTimestampWithinTolerance(Long.parseLong(timestamp));
            if (timestampStatus > 0) {
                String message = "Error: X-Authorization-Timestamp is too far in the future.";
                Logger.e(TAG, message);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, message);
                return;
            } else if (timestampStatus < 0) {
                String message = "Error: X-Authorization-Timestamp is too far in the past.";
                Logger.e(TAG, message);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, message);
                return;
            }
        } else {
            String message = "Error: X-Authorization-Timestamp is required.";
            Logger.e(TAG, message);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, message);
            return;
        }
        //Validate signature
        try {
            Util.verifySignature(request, response, timestamp, jsonData, serviceFinder);
        } catch (SettingsNotFoundException e) {
            throw new RuntimeException(e);
        }

        try {
            boolean isChangePass = Boolean.parseBoolean(request.getHeader(AppConstants.PRINTIX_SCAN_CONFIG_CHANGE_PASS));
            if (!isChangePass) {
                AppSettings appSettings = serviceFinder.getSettingsProvider().fromJson(jsonData);
                Logger.d(TAG, "appSettings: " + appSettings);
                //Delete cache
                String isClearCache = request.getHeader(CLEAR_CACHE);
                if (Boolean.parseBoolean(isClearCache)) {
                    Logger.d(TAG, "Start removing settings of Go Print app.");
                    if (isMatching(appSettings)) {
                        serviceFinder.getSettingsProvider().delConfigurationValue(configService, appSettings);
                    } else {
                        //Data is not matched
                        Logger.e(TAG, "Error: The data is not matched.");
                        sendResult(response, "false");
                    }
                } else {
                    Logger.d(TAG, "Start saving settings of Go Print app.");
                    // Save app config to device
                    serviceFinder.getSettingsProvider().setConfigurationValue(configService, appSettings);
                    // Save to app variable, in case device still not reset, just get setting from app variable
                    serviceFinder.getScanSettingsManager().setAppSettings(appSettings);
                }
            } else {
                Logger.d(TAG, "Device admin password is updated and passed to Go Print app.");
                AdminSettings adminSettings = serviceFinder.getAdminSettingsProvider().fromJson(jsonData);
                serviceFinder.getAdminSettingsProvider().save(adminSettings, false);
            }
            // Response
            sendResult(response, "true");
        } catch (Exception e) {
            Logger.e(TAG, "doPost error: " + e.getMessage());
            sendResult(response, "false");
        }
        Logger.i(TAG, "doPost end");
    }

    private boolean isMatching(AppSettings appSettings) throws SettingsNotFoundException {
        AppSettings oldSettings = serviceFinder.getSettingsProvider().getConfigurationValue(configService);
        return appSettings.getTenantId().equals(oldSettings.getTenantId())
            && appSettings.getPrinterId().equals(oldSettings.getPrinterId())
            && appSettings.getDeviceCanonHost().equals(oldSettings.getDeviceCanonHost());
    }

    private void sendResult(HttpServletResponse response, String result) throws IOException {
        PrintWriter pw = response.getWriter();
        response.setContentType(PostContentType.TEXT.getContentText());
        response.setContentLength(result.length());
        pw.println(result);
        pw.flush();
        pw.close();
    }

    private void handleResponse(HttpServletResponse response, AppSettings appSettings) throws Exception {
        JSONObject jsonObject = serviceFinder.getSettingsProvider().toJson(appSettings);
        String responseBody = jsonObject.toString();

        PrintWriter pw = response.getWriter();
        response.setContentType(PostContentType.JSON.getContentText());
        response.setContentLength(responseBody.length());

        pw.println(responseBody);
        pw.flush();
        pw.close();
    }

    private void handleBadRequest(HttpServletResponse response, Exception exception) {
        BadRequestException badRequestException = new BadRequestException(HttpServletResponse.SC_BAD_REQUEST, exception.getMessage(),
                exception.toString());
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

        JSONObject jsonObject = badRequestException.toJson();
        if (jsonObject != null) {
            String responseBody = jsonObject.toString();
            PrintWriter writer = null;
            try {
                writer = response.getWriter();
                response.setContentType(PostContentType.JSON.getContentText());
                response.setContentLength(responseBody.length());
                writer.println(responseBody);

            } catch (IOException e) {
                Logger.e(TAG, "handleResponseForRequest, error: " + e.getMessage());
            } finally {
                if (writer != null) {
                    writer.flush();
                    writer.close();
                }
            }
        }
    }
}
