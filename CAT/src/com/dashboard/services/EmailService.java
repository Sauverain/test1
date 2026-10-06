package com.dashboard.services;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class EmailService {

    public static void sendSystemEmailAsync(String recipientEmail, String subject, String messageHtml) {
        if (recipientEmail == null || recipientEmail.trim().isEmpty() || recipientEmail.equalsIgnoreCase("Unknown")) {
            return;
        }
        new Thread(() -> {
            try {
                String webAppUrl = "https://script.google.com/macros/s/AKfycbxQabVEBnM_RZw0vKPHf_l_JleDylXgyKXb4VM1eVCpLw6lN2DuuRdlOguV-CF_WYlG/exec";
                
                String cleanHtml = messageHtml.replace("\"", "\\\"").replace("\n", " ").replace("\r", " ");
                String jsonPayload = "{"
                        + "\"recipient\":\"" + recipientEmail.trim() + "\","
                        + "\"subject\":\"" + subject.replace("\"", "\\\"") + "\","
                        + "\"body\":\"" + cleanHtml + "\""
                        + "}";

                URL url = new URL(webAppUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setInstanceFollowRedirects(true);
                conn.setDoOutput(true);

                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    System.out.println("🚀 Email dispatched successfully via web app gateway to: " + recipientEmail);
                } else {
                    System.out.println("⚠️ API Connection code status response: " + responseCode);
                }
                conn.disconnect();
            } catch (Exception e) {
                System.err.println("⚠️ Web Gateway Transmission Error: " + e.getMessage());
            }
        }).start();
    }
}
