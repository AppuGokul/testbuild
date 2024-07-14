package com.example.qr.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import eu.bitwalker.useragentutils.UserAgent;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AnalyticsUtil {
    private static final String API_KEY = System.getenv("IPSTACK_API_KEY");
    private static final String IPSTACK_URL = "http://api.ipstack.com/";

    public static String getDeviceType(String userAgentString) {
        UserAgent userAgent = UserAgent.parseUserAgentString(userAgentString);
        return userAgent.getOperatingSystem().getDeviceType().getName();
    }

    public static String getIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null || ipAddress.isEmpty()) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }

    public static String getLocation(String ipAddress) throws IOException {
        URL url = new URL(IPSTACK_URL + ipAddress + "?access_key=" + API_KEY);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        try (InputStreamReader reader = new InputStreamReader(connection.getInputStream())) {
            JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
            return jsonObject.get("city").getAsString() + ", " +
                    jsonObject.get("region_name").getAsString() + ", " +
                    jsonObject.get("country_name").getAsString();
        } catch (Exception e) {
            // Log error or handle exception as needed
            e.printStackTrace();
            return "Unknown Location";
        }
    }

    public static boolean isUsingProxy(HttpServletRequest request) {
        String via = request.getHeader("Via");
        String forwarded = request.getHeader("Forwarded");
        return (via != null && !via.isEmpty()) || (forwarded != null && !forwarded.isEmpty());
    }
}