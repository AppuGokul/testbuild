package com.example.qr.controller;

import com.example.qr.util.DBConnection;
import com.example.qr.util.QRCodeUtil;
import com.google.zxing.WriterException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/generate")
public class QRCodeServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String url = request.getParameter("url");

        if (url == null || url.isEmpty()) {
            response.sendRedirect("index.jsp?error=URL is required");
            return;
        }

        // Validate URL (basic validation)
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            response.sendRedirect("index.jsp?error=Invalid URL format");
            return;
        }

        try (Connection connection = DBConnection.getConnection()) {
            String insertQRCodeSQL = "INSERT INTO qr_codes (content) VALUES (?)";
            PreparedStatement preparedStatement = connection.prepareStatement(insertQRCodeSQL, PreparedStatement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, url);
            preparedStatement.executeUpdate();

            int qrCodeId = 0;
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                qrCodeId = generatedKeys.getInt(1);
            }

            String qrContent = "http://yourdomain.com/scan?id=" + qrCodeId;

            byte[] qrCodeImage = QRCodeUtil.generateQRCodeImage(qrContent, 350, 350);

            response.setContentType("image/png");
            try (OutputStream outputStream = response.getOutputStream()) {
                outputStream.write(qrCodeImage);
                outputStream.flush();
            }

        } catch (SQLException | WriterException e) {
            throw new ServletException("Error generating QR code: " + e.getMessage(), e);
        }
    }
}