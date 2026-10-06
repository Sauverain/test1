package com.dashboard.views;

import com.dashboard.services.StudentService;
import com.dashboard.services.EmailService;
import java.util.Random;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ForgotPasswordModal {

    public static void display() {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.setTitle("Account Security Recovery");

        VBox root = new VBox(15);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.TOP_LEFT);
        root.setStyle("-fx-background-color: #ffffff;");

        Label lblTitle = new Label("Reset Account Password");
        lblTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #0f172a; -fx-padding: 0 0 10 0;");

        VBox step1Box = new VBox(10);
        Label lblEmail = new Label("Enter Registered Administrative Email *");
        lblEmail.setStyle("-fx-text-fill: #475569; -fx-font-size: 13px;");
        TextField txtEmail = new TextField();
        txtEmail.setPrefHeight(45);
        txtEmail.setStyle("-fx-border-color: #cbd5e1; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 0 12;");
        Button btnSendOtp = new Button("Send Secure Access Token");
        btnSendOtp.setMaxWidth(Double.MAX_VALUE);
        btnSendOtp.setPrefHeight(45);
        btnSendOtp.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-cursor: hand;");
        step1Box.getChildren().addAll(lblEmail, txtEmail, btnSendOtp);

        VBox step2Box = new VBox(12);
        step2Box.setVisible(false);
        step2Box.setManaged(false);

        Label lblOtp = new Label("6-Digit Verification Token (OTP) *");
        lblOtp.setStyle("-fx-text-fill: #475569; -fx-font-size: 13px;");
        TextField txtOtp = new TextField();
        txtOtp.setPrefHeight(45);
        txtOtp.setStyle("-fx-border-color: #cbd5e1; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 0 12;");

        Label lblNewPass = new Label("New Secure Password *");
        lblNewPass.setStyle("-fx-text-fill: #475569; -fx-font-size: 13px;");
        StackPane passStack = new StackPane();
        PasswordField txtPassHidden = new PasswordField();
        txtPassHidden.setPrefHeight(45);
        txtPassHidden.setStyle("-fx-border-color: #cbd5e1; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 0 40 0 12;");
        TextField txtPassShown = new TextField();
        txtPassShown.setPrefHeight(45);
        txtPassShown.setStyle("-fx-border-color: #cbd5e1; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 0 40 0 12;");
        txtPassShown.setVisible(false);
        Button btnEyePass = new Button("👁");
        btnEyePass.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-text-fill: #64748b;");
        StackPane.setAlignment(btnEyePass, Pos.CENTER_RIGHT);
        passStack.getChildren().addAll(txtPassHidden, txtPassShown, btnEyePass);

        Label lblConfirm = new Label("Confirm New Password *");
        lblConfirm.setStyle("-fx-text-fill: #475569; -fx-font-size: 13px;");
        StackPane confirmStack = new StackPane();
        PasswordField txtConfirmHidden = new PasswordField();
        txtConfirmHidden.setPrefHeight(45);
        txtConfirmHidden.setStyle("-fx-border-color: #cbd5e1; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 0 40 0 12;");
        TextField txtConfirmShown = new TextField();
        txtConfirmShown.setPrefHeight(45);
        txtConfirmShown.setStyle("-fx-border-color: #cbd5e1; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 0 40 0 12;");
        txtConfirmShown.setVisible(false);
        Button btnEyeConfirm = new Button("👁");
        btnEyeConfirm.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-text-fill: #64748b;");
        StackPane.setAlignment(btnEyeConfirm, Pos.CENTER_RIGHT);
        confirmStack.getChildren().addAll(txtConfirmHidden, txtConfirmShown, btnEyeConfirm);

        VBox criteriaBox = new VBox(4);
        Label c1 = new Label("✕ Minimum 8 characters");
        Label c2 = new Label("✕ At least one uppercase letter (A-Z)");
        Label c3 = new Label("✕ At least one numeric digit (0-9)");
        Label c4 = new Label("✕ At least one special character (@#$%^&*+=!)");
        String cStyle = "-fx-text-fill: #ef4444; -fx-font-size: 12px; -fx-font-weight: bold;";
        c1.setStyle(cStyle); c2.setStyle(cStyle); c3.setStyle(cStyle); c4.setStyle(cStyle);
        criteriaBox.getChildren().addAll(c1, c2, c3, c4);

        Button btnReset = new Button("Update Password Account");
        btnReset.setMaxWidth(Double.MAX_VALUE);
        btnReset.setPrefHeight(46);
        btnReset.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-cursor: hand;");

        step2Box.getChildren().addAll(lblOtp, txtOtp, lblNewPass, passStack, lblConfirm, confirmStack, criteriaBox, btnReset);
        root.getChildren().addAll(lblTitle, step1Box, step2Box);

        btnEyePass.setOnAction(e -> {
            if (txtPassHidden.isVisible()) {
                txtPassShown.setText(txtPassHidden.getText());
                txtPassHidden.setVisible(false);
                txtPassShown.setVisible(true);
            } else {
                txtPassHidden.setText(txtPassShown.getText());
                txtPassShown.setVisible(false);
                txtPassHidden.setVisible(true);
            }
        });

        btnEyeConfirm.setOnAction(e -> {
            if (txtConfirmHidden.isVisible()) {
                txtConfirmShown.setText(txtConfirmHidden.getText());
                txtConfirmHidden.setVisible(false);
                txtConfirmShown.setVisible(true);
            } else {
                txtConfirmHidden.setText(txtConfirmShown.getText());
                txtConfirmShown.setVisible(false);
                txtConfirmHidden.setVisible(true);
            }
        });

        Runnable checkPasswordCriteria = () -> {
            String pass = txtPassHidden.isVisible() ? txtPassHidden.getText() : txtPassShown.getText();
            if (pass.length() >= 8) { c1.setStyle("-fx-text-fill: #10b981; -fx-font-size: 12px; -fx-font-weight: bold;"); c1.setText("✓ Minimum 8 characters"); }
            else { c1.setStyle("-fx-text-fill: #ef4444; -fx-font-size: 12px; -fx-font-weight: bold;"); c1.setText("✕ Minimum 8 characters"); }
            if (pass.matches(".*[A-Z].*")) { c2.setStyle("-fx-text-fill: #10b981; -fx-font-size: 12px; -fx-font-weight: bold;"); c2.setText("✓ At least one uppercase letter (A-Z)"); }
            else { c2.setStyle("-fx-text-fill: #ef4444; -fx-font-size: 12px; -fx-font-weight: bold;"); c2.setText("✕ At least one uppercase letter (A-Z)"); }
            if (pass.matches(".*[0-9].*")) { c3.setStyle("-fx-text-fill: #10b981; -fx-font-size: 12px; -fx-font-weight: bold;"); c3.setText("✓ At least one numeric digit (0-9)"); }
            else { c3.setStyle("-fx-text-fill: #ef4444; -fx-font-size: 12px; -fx-font-weight: bold;"); c3.setText("✕ At least one numeric digit (0-9)"); }
            if (pass.matches(".*[@#$%^&*+=!].*")) { c4.setStyle("-fx-text-fill: #10b981; -fx-font-size: 12px; -fx-font-weight: bold;"); c4.setText("✓ At least one special character (@#$%^&*+=!)"); }
            else { c4.setStyle("-fx-text-fill: #ef4444; -fx-font-size: 12px; -fx-font-weight: bold;"); c4.setText("✕ At least one special character (@#$%^&*+=!)"); }
        };

        txtPassHidden.textProperty().addListener((o, old, nw) -> checkPasswordCriteria.run());
        txtPassShown.textProperty().addListener((o, old, nw) -> checkPasswordCriteria.run());

        btnSendOtp.setOnAction(e -> {
            String email = txtEmail.getText().trim();
            if (email.isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "Email target input field cannot be blank.").showAndWait();
                return;
            }
            if (!StudentService.checkEmailExists(email)) {
                new Alert(Alert.AlertType.ERROR, "The specified email address was not found in our administrator logs.").showAndWait();
                return;
            }

            String randomOtp = String.format("%06d", new Random().nextInt(999999));
            StudentService.setTemporaryRecoveryToken(email, randomOtp);

            String htmlBody = "<html><body>"
                    + "<div style='font-family:Arial,sans-serif;padding:25px;border:1px solid #e2e8f0;max-width:500px;background-color:#ffffff;line-height:1.6;color:#1e293b;'>"
                    + "<h2 style='color:#2563eb;margin-top:0;'>Password Recovery Request</h2>"
                    + "<p>Use the following 6-digit verification code to complete your reset action:</p>"
                    + "<p style='font-size:28px;font-weight:bold;color:#2563eb !important;letter-spacing:4px;margin:20px 0;font-family:Courier New,Courier,monospace;background-color:#f1f5f9;padding:10px 15px;border-radius:4px;display:inline-block;'>" + randomOtp + "</p>"
                    + "<p style='font-size:13px;color:#64748b;'>This code is short-lived. If you did not make this request, ignore this message safely.</p>"
                    + "</div>"
                    + "</body></html>";

            EmailService.sendSystemEmailAsync(email, "Security Password Reset OTP Token", htmlBody);
            new Alert(Alert.AlertType.INFORMATION, "A secure 6-digit access code was sent to: " + email + "\n Check your email inbox.").showAndWait();

            step1Box.setVisible(false);
            step1Box.setManaged(false);
            step2Box.setVisible(true);
            step2Box.setManaged(true);
                modal.setHeight(560);
});

btnReset.setOnAction(e -> {
    String email = txtEmail.getText().trim();
    String otp = txtOtp.getText().trim();
    String pass = txtPassHidden.isVisible() ? txtPassHidden.getText() : txtPassShown.getText();
    String conf = txtConfirmHidden.isVisible() ? txtConfirmHidden.getText() : txtConfirmShown.getText();

    if (otp.isEmpty() || pass.isEmpty() || conf.isEmpty()) {
        new Alert(Alert.AlertType.WARNING, "All form fields are mandatory fields.").showAndWait();
        return;
    }

    if (!pass.equals(conf)) {
        new Alert(Alert.AlertType.ERROR, "Password verification mismatch. Confirm passwords carefully.").showAndWait();
        return;
    }

    // Fixed 'password.matches' to 'pass.matches' to match your variable name
    if (pass.length() < 8 || !pass.matches(".*[A-Z].*") || !pass.matches(".*[0-9].*") || !pass.matches(".*[@#$%^&+=!].*")) {
        new Alert(Alert.AlertType.WARNING, "Password does not meet validation strength configurations.").showAndWait();
        return;
    }

    if (StudentService.verifyCurrentPassword(email, pass)) {
        new Alert(Alert.AlertType.ERROR, "Security Error! You cannot reuse your old password. Please choose a new unique password.").showAndWait();
        return;
    }

    boolean ok = StudentService.verifyOtpAndChangePassword(email, otp, pass);
    if (ok) {
        new Alert(Alert.AlertType.INFORMATION, "Your password record has been modified! You can now log in.").showAndWait();
        modal.close();
    } else {
        new Alert(Alert.AlertType.ERROR, "Verification failed! The 6-digit token is invalid or expired.").showAndWait();
    }
});

modal.setScene(new Scene(root, 440, 240));
modal.showAndWait();
}
}
