package com.dashboard.views;

import com.dashboard.App;
import com.dashboard.services.StudentService;
import com.dashboard.services.EmailService;
import java.util.Random;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class SignupView {

    private VBox layout;
    private String generatedOtp = null;

    public SignupView() {
        layout = new VBox(14);
        layout.setAlignment(Pos.TOP_LEFT);
        layout.setPadding(new Insets(20, 32, 20, 32));
        layout.setStyle("-fx-background-color: #ffffff;");

        Label lblTitle = new Label("Create Admin Account");
        lblTitle.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #0f172a; -fx-font-family: 'Segoe UI', Arial; -fx-padding: 0 0 5 0;");

        VBox fNameBox = new VBox(4);
        Label lblFName = new Label("Full Name *");
        lblFName.setStyle("-fx-text-fill: #475569; -fx-font-size: 13px;");
        TextField txtName = new TextField();
        txtName.setPrefHeight(40);
        txtName.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cbd5e1; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 0 12; -fx-font-size: 14px;");
        fNameBox.getChildren().addAll(lblFName, txtName);

        VBox emailBox = new VBox(4);
        Label lblEmail = new Label("Email Address *");
        lblEmail.setStyle("-fx-text-fill: #475569; -fx-font-size: 13px;");
        TextField txtEmail = new TextField();
        txtEmail.setPromptText("Enter your email address");
        txtEmail.setPrefHeight(40);
        txtEmail.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cbd5e1; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 0 12; -fx-font-size: 14px;");
        
        Label lblEmailError = new Label("✕ Invalid email format structure");
        lblEmailError.setStyle("-fx-text-fill: #ef4444; -fx-font-size: 11px; -fx-font-weight: bold;");
        lblEmailError.setVisible(false);
        lblEmailError.setManaged(false);
        emailBox.getChildren().addAll(lblEmail, txtEmail, lblEmailError);

        VBox passBox = new VBox(4);
        Label lblPass = new Label("Password *");
        lblPass.setStyle("-fx-text-fill: #475569; -fx-font-size: 13px;");
        StackPane passStack = new StackPane();
        PasswordField txtPassHidden = new PasswordField();
        txtPassHidden.setPromptText("Create password");
        txtPassHidden.setPrefHeight(40);
        txtPassHidden.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cbd5e1; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 0 40 0 12; -fx-font-size: 14px;");
        TextField txtPassShown = new TextField();
        txtPassShown.setPromptText("Create password");
        txtPassShown.setPrefHeight(40);
        txtPassShown.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cbd5e1; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 0 40 0 12; -fx-font-size: 14px;");
        txtPassShown.setVisible(false);
        Button btnEyePass = new Button("👁");
        btnEyePass.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-text-fill: #64748b; -fx-font-size: 14px;");
        StackPane.setAlignment(btnEyePass, Pos.CENTER_RIGHT);
        passStack.getChildren().addAll(txtPassHidden, txtPassShown, btnEyePass);
        passBox.getChildren().addAll(lblPass, passStack);

        VBox confirmBox = new VBox(4);
        Label lblConfirm = new Label("Confirm Password *");
        lblConfirm.setStyle("-fx-text-fill: #475569; -fx-font-size: 13px;");
        StackPane confirmStack = new StackPane();
        PasswordField txtConfirmHidden = new PasswordField();
        txtConfirmHidden.setPromptText("Confirm password");
        txtConfirmHidden.setPrefHeight(40);
        txtConfirmHidden.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cbd5e1; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 0 40 0 12; -fx-font-size: 14px;");
        TextField txtConfirmShown = new TextField();
        txtConfirmShown.setPromptText("Confirm password");
        txtConfirmShown.setPrefHeight(40);
        txtConfirmShown.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cbd5e1; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 0 40 0 12; -fx-font-size: 14px;");
        txtConfirmShown.setVisible(false);
        Button btnEyeConfirm = new Button("👁");
        btnEyeConfirm.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-text-fill: #64748b; -fx-font-size: 14px;");
        StackPane.setAlignment(btnEyeConfirm, Pos.CENTER_RIGHT);
        confirmStack.getChildren().addAll(txtConfirmHidden, txtConfirmShown, btnEyeConfirm);
        confirmBox.getChildren().addAll(lblConfirm, confirmStack);

        VBox otpVerificationBox = new VBox(4);
        Label lblOtpField = new Label("Enter 6-Digit Email Verification Code *");
        lblOtpField.setStyle("-fx-text-fill: #2563eb; -fx-font-size: 13px; -fx-font-weight: bold;");
        TextField txtOtpVerification = new TextField();
        txtOtpVerification.setPromptText("Check your inbox for the activation code");
        txtOtpVerification.setPrefHeight(40);
        txtOtpVerification.setStyle("-fx-background-color: #f8fafc; -fx-border-color: #2563eb; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 0 12; -fx-font-size: 14px;");
        otpVerificationBox.getChildren().addAll(lblOtpField, txtOtpVerification);
        otpVerificationBox.setVisible(false);
        otpVerificationBox.setManaged(false);

        VBox criteriaBox = new VBox(3);
        Label c1 = new Label("✕ Minimum 8 characters");
        Label c2 = new Label("✕ At least one uppercase letter (A-Z)");
        Label c3 = new Label("✕ At least one numeric digit (0-9)");
        Label c4 = new Label("✕ At least one special character (@#$%^&*+=!)");
        String initialStyle = "-fx-text-fill: #ef4444; -fx-font-size: 12px; -fx-font-family: 'Segoe UI', Arial; -fx-font-weight: bold;";
        c1.setStyle(initialStyle); c2.setStyle(initialStyle); c3.setStyle(initialStyle); c4.setStyle(initialStyle);
        criteriaBox.getChildren().addAll(c1, c2, c3, c4);

        Button btnSignup = new Button("Verify Email & Register Account");
        btnSignup.setMaxWidth(Double.MAX_VALUE);
        btnSignup.setPrefHeight(44);
        btnSignup.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-background-radius: 6; -fx-cursor: hand;");

        HBox footerRow = new HBox(4);
        footerRow.setAlignment(Pos.CENTER);
        Label lblHasAccount = new Label("Already have an account?");
        lblHasAccount.setStyle("-fx-text-fill: #475569; -fx-font-size: 13px;");
        Hyperlink linkLogin = new Hyperlink("Sign in here");
        linkLogin.setStyle("-fx-text-fill: #3b82f6; -fx-underline: false; -fx-font-size: 13px; -fx-font-weight: bold;");
        linkLogin.setOnAction(e -> App.navigateToLogin());
        footerRow.getChildren().addAll(lblHasAccount, linkLogin);

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
            if (pass.length() >= 8) { c1.setStyle("-fx-text-fill: #10b981; -fx-font-size: 12px;"); c1.setText("✓ Minimum 8 characters"); }
            else { c1.setStyle("-fx-text-fill: #ef4444; -fx-font-size: 12px;"); c1.setText("✕ Minimum 8 characters"); }
            if (pass.matches(".*[A-Z].*")) { c2.setStyle("-fx-text-fill: #10b981; -fx-font-size: 12px;"); c2.setText("✓ At least one uppercase letter (A-Z)"); }
            else { c2.setStyle("-fx-text-fill: #ef4444; -fx-font-size: 12px;"); c2.setText("✕ At least one uppercase letter (A-Z)"); }
            if (pass.matches(".*[0-9].*")) { c3.setStyle("-fx-text-fill: #10b981; -fx-font-size: 12px;"); c3.setText("✓ At least one numeric digit (0-9)"); }
            else { c3.setStyle("-fx-text-fill: #ef4444; -fx-font-size: 12px;"); c3.setText("✕ At least one numeric digit (0-9)"); }
            if (pass.matches(".*[@#$%^&*+=!].*")) { c4.setStyle("-fx-text-fill: #10b981; -fx-font-size: 12px;"); c4.setText("✓ At least one special character (@#$%^&*+=!)"); }
            else { c4.setStyle("-fx-text-fill: #ef4444; -fx-font-size: 12px;"); c4.setText("✕ At least one special character (@#$%^&*+=!)"); }
        };

        txtPassHidden.textProperty().addListener((o, old, nw) -> checkPasswordCriteria.run());
        txtPassShown.textProperty().addListener((o, old, nw) -> checkPasswordCriteria.run());

        txtEmail.textProperty().addListener((observable, oldValue, newValue) -> {
            String email = newValue.trim();
            if (email.isEmpty()) {
                lblEmailError.setVisible(false);
                lblEmailError.setManaged(false);
                    txtEmail.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cbd5e1; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 0 12; -fx-font-size: 14px;");
} else if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")) {
    lblEmailError.setVisible(true);
    lblEmailError.setManaged(true);
    txtEmail.setStyle("-fx-background-color: #ffffff; -fx-border-color: #ef4444; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 0 12; -fx-font-size: 14px;");
} else {
    lblEmailError.setVisible(false);
    lblEmailError.setManaged(false);
    txtEmail.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cbd5e1; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 0 12; -fx-font-size: 14px;");
}
});

btnSignup.setOnAction(e -> {
    String name = txtName.getText().trim();
    String email = txtEmail.getText().trim();
    String password = txtPassHidden.isVisible() ? txtPassHidden.getText() : txtPassShown.getText();
    String confirm = txtConfirmHidden.isVisible() ? txtConfirmHidden.getText() : txtConfirmShown.getText();

    if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
        new Alert(Alert.AlertType.WARNING, "All form fields are required.").showAndWait();
        return;
    }

    if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")) {
        new Alert(Alert.AlertType.ERROR, "Please enter a valid email address format.").showAndWait();
        return;
    }

    if (!password.equals(confirm)) {
        new Alert(Alert.AlertType.ERROR, "Passwords do not match.").showAndWait();
        return;
    }

    if (password.length() < 8 || !password.matches(".*[A-Z].*") || !password.matches(".*[0-9].*") || !password.matches(".*[@#$%^&+=!].*")) {
        new Alert(Alert.AlertType.WARNING, "Password fails complexity requirements.").showAndWait();
        return;
    }

    if (generatedOtp == null) {
        if (StudentService.checkEmailExists(email)) {
            new Alert(Alert.AlertType.ERROR, "This email address is already registered inside our portal logs.").showAndWait();
            return;
        }

        generatedOtp = String.format("%06d", new Random().nextInt(999999));
        
        String verificationMessage = "<html><body>"
                + ""
                + "Account Activation Code <br>"
                + "Hello <h3><b>" + name + "</b></h3><br>"
                + "Use the following 6-digit OTP code to verify your mailbox and activate your administrator profile:"
                + "<br><br><h2 style=\"color: #005eff;\">" + generatedOtp + "</h2><br><br>"
                + "If this email account doesn't exist, this message wouldn't be received."
                + ""
                + "";

        EmailService.sendSystemEmailAsync(email, "Portal Activation Security Code", verificationMessage);
        
        new Alert(Alert.AlertType.INFORMATION, "A 6-digit verification code has been sent to: " + email + "\n Check your inbox to retrieve it.").showAndWait();

        txtName.setDisable(true);
        txtEmail.setDisable(true);
        txtPassHidden.setDisable(true);
        txtPassShown.setDisable(true);
        txtConfirmHidden.setDisable(true);
        txtConfirmShown.setDisable(true);
        btnEyePass.setDisable(true);
        btnEyeConfirm.setDisable(true);

        otpVerificationBox.setVisible(true);
        otpVerificationBox.setManaged(true);
        btnSignup.setText("Confirm Code & Create Account");
        return;
    }

    String enteredOtp = txtOtpVerification.getText().trim();
    if (enteredOtp.isEmpty() || !enteredOtp.equals(generatedOtp)) {
        new Alert(Alert.AlertType.ERROR, "The activation token you typed is incorrect. Please verify your code.").showAndWait();
        return;
    }

    boolean success = StudentService.registerAccount(name, email, password);
    if (success) {
        String welcomeMessage = "<html><body>"
                + ""
                + "Dear <b>" + name + "</b>,"
                + "Congratulations! Your administrative access has been successfully activated.<br>"
                + "You can now sign in to the Student Management Portal using the credentials associated with your account.<br>"
                + "Username: <b style=\"color: #005eff;\">" + email + "</b><br>"
                + "This activation was completed by an authorized administrator and grants you access to the administrative functions assigned to your role.<br>"
                + "If you require assistance or have any questions regarding your account, please contact the system support team.<br>"
                + "Best regards,<br>"
                + "<b>UBUMWE SCHOOL STUDENT MANAGEMENT SYSTEM TEAM</b><br>"
                + "Administration & Security Services"
                + ""
                + "";

        EmailService.sendSystemEmailAsync(email, "SMS Portal Account Activated", welcomeMessage);
        
        new Alert(Alert.AlertType.INFORMATION, "Administrative Account Created Successfully!").showAndWait();
        App.navigateToLogin();
    } else {
        new Alert(Alert.AlertType.ERROR, "Registration failed. Verify connection status properties.").showAndWait();
    }
});

layout.getChildren().addAll(lblTitle, fNameBox, emailBox, passBox, confirmBox, otpVerificationBox, criteriaBox, btnSignup, footerRow);
}

public VBox getLayout() {
    return layout;
}
}