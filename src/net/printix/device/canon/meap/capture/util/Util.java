// ------------------------------------------------------------------------------
// Copyright (c) 2002-2024 Tungsten Automation. All rights reserved.
// Description: Util
// ------------------------------------------------------------------------------

package net.printix.device.canon.meap.capture.util;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.Getter;
import net.printix.device.canon.meap.capture.IServiceFinder;
import net.printix.device.canon.meap.capture.exception.BadRequestException;
import net.printix.device.canon.meap.capture.log.Logger;
import net.printix.device.canon.meap.capture.settings.AdminSettings;
import net.printix.device.canon.meap.capture.settings.AppConstants;
import net.printix.device.canon.meap.capture.settings.SettingsNotFoundException;
import org.json.JSONObject;


public final class Util {
    private final static String TAG = Util.class.getSimpleName();
    private static final Object lock = new Object();
    private Util() {
    }

    /**
     * The method handles bad request and response to client
     *
     * @return
     */
    public static void handleBadRequest(HttpServletResponse response, Exception exception) {
        BadRequestException badRequestException =
            new BadRequestException(HttpServletResponse.SC_BAD_REQUEST, exception.getMessage(),
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

    public static List<String> calculateSignature(List<String> secretKeys, String timestamp,
        String method, String requestPathAndQuery, String requestBody)
        throws NoSuchAlgorithmException, InvalidKeyException {
        ArrayList<String> calculatedSignatures = new ArrayList<>();
        // Create the raw signature input string from the request's parameters.
        String contentToSign = String.format("%s.%s.%s.%s", timestamp,
            Util.encodeBase64(method.toLowerCase()),
            Util.encodeBase64(requestPathAndQuery), requestBody);

        byte[] stringToSignUtf8 = contentToSign.getBytes(StandardCharsets.UTF_8);

        for (String key : secretKeys) {
            byte[] keyBytes = Base64.getDecoder().decode(key);
            String algorithm = getAlgorithm(keyBytes);
            Mac hmac = Mac.getInstance(algorithm);
            hmac.init(new SecretKeySpec(keyBytes, algorithm));
            calculatedSignatures.add(
                Base64.getEncoder().encodeToString(hmac.doFinal(stringToSignUtf8)));
        }

        return calculatedSignatures;
    }

    public static String bytesToHex(String originalString) throws NoSuchAlgorithmException {
        byte[] hash = hashString(originalString);
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    private static byte[] hashString(String originalString) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return digest.digest(originalString.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Determines the algorithm from the secret key length.
     *
     * @param keyBytes Binary representation of a secret key.
     * @return The calculated algorithm.
     */
    private static String getAlgorithm(byte[] keyBytes) {
        if (keyBytes.length == 32) {
            // If the secret key is 32-byte (256-bit) long, we must use the HMAC-SHA256 algorithm.
            return "HmacSHA256";
        } else if (keyBytes.length == 64) {
            // If the secret key is 64-byte (512-bit) long, we must use the HMAC-SHA512 algorithm.
            return "HmacSHA512";
        } else {
            return "HmacSHA1";
        }
    }

    public static String encodeBase64(String s) {
        return Base64.getEncoder().encodeToString(s.getBytes(
            StandardCharsets.UTF_8));
    }

    public static String decodeBase64(String s) {
        byte[] decodedBytes = Base64.getDecoder().decode(s);
        return new String(decodedBytes);
    }

    public static boolean getAccessToken(HttpServletRequest request, IServiceFinder serviceFinder) {
        try {
            final String authorization = request.getHeader(AppConstants.AUTHORIZATION);
            if (authorization != null && authorization.toLowerCase().startsWith("basic")) {
                // Authorization: Basic base64credentials
                String base64Credentials = authorization.substring("Basic".length()).trim();
                byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
                String credentials = new String(credDecoded, StandardCharsets.UTF_8);
                // credentials = username:password
                final String[] values = credentials.split(":", 2);
                for (int i = 0; i < values.length; i++) {
                    if (values[0] == null || values[0].isEmpty()) {
                        return false;
                    }
                }
                try {
                    AdminSettings.AdminSettingsBuilder adminSettings = AdminSettings.builder();
                    adminSettings.username(values[0]);
                    adminSettings.password(values[1]);
                    return serviceFinder.getAdminSettingsProvider().checkCredentials(adminSettings.build());
                } catch (NoSuchAlgorithmException | SettingsNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }

        } catch (Exception e){
            Logger.e(TAG, e.getMessage());
        }
        return false;
    }

    public static String getRequestData(HttpServletRequest request) {
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = null;
        String line = null;
        InputStream inputStream = null;
        synchronized (lock) {
            try {
                inputStream = request.getInputStream();
                if (inputStream != null) {
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    while ((line = reader.readLine()) != null) {
                        builder.append(line);
                    }
                } else {
                    return null;
                }
            } catch (Exception e) {
                Logger.e(TAG, e.getMessage());
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                        inputStream = null;
                    } catch (IOException e) {

                        Logger.e(TAG, e.getMessage());
                    }
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        Logger.e(TAG, e.getMessage());
                    }
                }
            }

            return builder.toString();
        }
    }

    public static int compareTimestampWithinTolerance(long unixTimestamp) {
        long tolerance = 120;// 2 mins
        long unixCurrent = System.currentTimeMillis() / 1000L;
        if (unixTimestamp > unixCurrent + tolerance) {
            return 1;
        } else if (unixTimestamp < unixCurrent - tolerance) {
            return -1;
        } else {
            return 0;
        }
    }

    public static void verifySignature(HttpServletRequest request, HttpServletResponse response,
        String timestamp, String jsonData, IServiceFinder serviceFinder) throws IOException, SettingsNotFoundException {
        ArrayList<String> keys = new ArrayList<>();
        AdminSettings adminSettings = serviceFinder.getAdminSettingsProvider().load();
        keys.add(adminSettings.getUsername() + adminSettings.getPassword());
        String signature = request.getHeader("X-Signature");
        if (signature != null) {
            try {
                List<String> signs = Util.calculateSignature(keys, timestamp,
                    request.getMethod(), AppConstants.PRINTIX_SCAN_CONFIG, jsonData);
                if (!signs.get(0).equals(signature)) {
                    String message = "Error: The HMAC signature is not matched.";
                    Logger.e(TAG, message);
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, message);
                }
            } catch (NoSuchAlgorithmException | InvalidKeyException | IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            String message = "Error: X-Signature is required.";
            Logger.e(TAG, message);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, message);
        }
    }

    @Getter
    public enum PostContentType {
        TEXT("text/plain"),
        JSON("application/json; charset=utf-8"),
        URL_ENCODED("application/x-www-form-urlencoded");

        private final String contentText;

        PostContentType(String contentText) {
            this.contentText = contentText;
        }

    }

    private static String getLocalizationText(String key, Locale currentLocale) {
        // Load the appropriate properties file (e.g., lang_en.properties)
        ResourceBundle bundle = ResourceBundle.getBundle("lang.messages", currentLocale);

        // Retrieve and return the value associated with the key
        return bundle.getString(key);
    }

    public static String getLocalizedText(String key, Locale currentLocale) {
        try {
            return getLocalizationText(key, currentLocale);
        } catch (Exception e) {
            Logger.e(TAG, "getLocalizationText error: " + e.getMessage());
        }
        // Handle the case where the key doesn't exist
        //Return to default language - English
        try {
            return getLocalizationText(key, Locale.ENGLISH);
        } catch (Exception e) {
            Logger.e(TAG, "get defaultText error: " + e.getMessage());
            return key;
        }
    }

}
