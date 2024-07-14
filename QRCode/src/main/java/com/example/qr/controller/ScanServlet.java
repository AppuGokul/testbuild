package com.example.qr.controller;

import com.example.qr.model.Scan;
import com.example.qr.util.AnalyticsUtil;
import com.example.qr.util.DBConnection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

@WebServlet("/scan")
public class ScanServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idParam = request.getParameter("id");
        if (idParam == null || idParam.isEmpty()) {
            response.sendRedirect("index.jsp?error=Invalid QR code");
            return;
        }

        int qrCodeId = Integer.parseInt(idParam);
        String ipAddress = AnalyticsUtil.getIpAddress(request);
        String location = AnalyticsUtil.getLocation(ipAddress);
        String userAgent = request.getHeader("User-Agent");
        String deviceType = AnalyticsUtil.getDeviceType(userAgent);
        boolean isProxy = AnalyticsUtil.isUsingProxy(request);

        try (Connection connection = DBConnection.getConnection()) {
            String insertScanSQL = "INSERT INTO scans (qr_code_id, ip_address, location, device_type, proxy_status) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(insertScanSQL);
            preparedStatement.setInt(1, qrCodeId);
            preparedStatement.setString(2, ipAddress);
            preparedStatement.setString(3, location);
            preparedStatement.setString(4, deviceType);
            preparedStatement.setBoolean(5, isProxy);
            preparedStatement.executeUpdate();

            // Fetch the original URL associated with the QR code
            String fetchUrlSQL = "SELECT content FROM qr_codes WHERE id = ?";
            PreparedStatement fetchUrlStatement = connection.prepareStatement(fetchUrlSQL);
            fetchUrlStatement.setInt(1, qrCodeId);
            ResultSet resultSet = fetchUrlStatement.executeQuery();
            if (resultSet.next()) {
                String originalUrl = resultSet.getString("content");
                response.sendRedirect(originalUrl);
            } else {
                response.sendRedirect("index.jsp?error=QR code not found");
            }
        } catch (SQLException e) {
            throw new ServletException("Error recording scan", e);
        }
    }
}