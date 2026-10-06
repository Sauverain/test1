package com.dashboard.views;

import com.dashboard.App;
import com.dashboard.services.StudentService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class LoginView {

    private VBox layout;

    public LoginView() {
        layout = new VBox(18);
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setPadding(new Insets(40, 50, 40, 50));
        layout.setStyle("-fx-background-color: #ffffff;");

        Label lblTitle = new Label("Sign In to Portal");
        lblTitle.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #0f172a; -fx-padding: 0 0 10 0; -fx-font-family: 'Segoe UI', Arial;");

        VBox emailBox = new VBox(8);
        Label lblEmail = new Label("Email Address *");
        lblEmail.setStyle("-fx-text-fill: #475569; -fx-font-size: 14px; -fx-font-weight: 600; -fx-font-family: 'Segoe UI', Arial;");
        TextField txtEmail = new TextField();
        txtEmail.setPromptText("Enter your email address");
        txtEmail.setPrefHeight(48);
        txtEmail.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cbd5e1; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 0 14; -fx-font-size: 14px;");
        emailBox.getChildren().addAll(lblEmail, txtEmail);

        VBox passBox = new VBox(8);
        HBox passHeaderRow = new HBox();
        passHeaderRow.setAlignment(Pos.CENTER_LEFT);
        Label lblPass = new Label("Password *");
        lblPass.setStyle("-fx-text-fill: #475569; -fx-font-size: 14px; -fx-font-weight: 600; -fx-font-family: 'Segoe UI', Arial;");
        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Hyperlink linkForgotPass = new Hyperlink("Forgot Password?");
        linkForgotPass.setStyle("-fx-text-fill: #ef4444; -fx-font-size: 13px; -fx-font-weight: 600; -fx-underline: false; -fx-font-family: 'Segoe UI', Arial; -fx-padding: 0;");
        linkForgotPass.setOnAction(e -> ForgotPasswordModal.display());
        passHeaderRow.getChildren().addAll(lblPass, spacer, linkForgotPass);

        StackPane passStack = new StackPane();
        PasswordField txtPassHidden = new PasswordField();
        txtPassHidden.setPromptText("Enter password");
        txtPassHidden.setPrefHeight(48);
        txtPassHidden.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cbd5e1; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 0 45 0 14; -fx-font-size: 14px;");
        TextField txtPassShown = new TextField();
        txtPassShown.setPromptText("Enter password");
        txtPassShown.setPrefHeight(48);
        txtPassShown.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cbd5e1; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 0 45 0 14; -fx-font-size: 14px;");
        txtPassShown.setVisible(false);
        Button btnEyePass = new Button("👁");
        btnEyePass.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-text-fill: #64748b; -fx-font-size: 16px; -fx-padding: 0 15 0 0;");
        StackPane.setAlignment(btnEyePass, Pos.CENTER_RIGHT);
        passStack.getChildren().addAll(txtPassHidden, txtPassShown, btnEyePass);
        passBox.getChildren().addAll(passHeaderRow, passStack);

        Button btnLogin = new Button("Sign In");
        btnLogin.setMaxWidth(Double.MAX_VALUE);
        btnLogin.setPrefHeight(50);
        btnLogin.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 15px; -fx-background-radius: 6; -fx-cursor: hand; -fx-font-family: 'Segoe UI', Arial;");

        HBox footerRow = new HBox(4);
        footerRow.setAlignment(Pos.CENTER);
        Label lblNoAccount = new Label("Don't have an account?");
        lblNoAccount.setStyle("-fx-text-fill: #475569; -fx-font-size: 14px; -fx-font-family: 'Segoe UI', Arial;");
        Hyperlink linkSignup = new Hyperlink("Sign up here");
        linkSignup.setStyle("-fx-text-fill: #3b82f6; -fx-underline: false; -fx-font-size: 14px; -fx-font-weight: 600; -fx-font-family: 'Segoe UI', Arial; -fx-padding: 0;");
        linkSignup.setOnAction(e -> App.navigateToSignup());
        footerRow.getChildren().addAll(lblNoAccount, linkSignup);

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

        btnLogin.setOnAction(e -> {
            String email = txtEmail.getText().trim();
            String password = txtPassHidden.isVisible() ? txtPassHidden.getText() : txtPassShown.getText();

            if (email.isEmpty() || password.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "All login input form fields are required.");
                alert.showAndWait();
                return;
            }

            String fullName = StudentService.validateLoginAndGetFullName(email, password);
            if (fullName != null) {
                App.setSessionUsername(fullName);
                App.navigateToDashboardCore();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid email address or matching security password profile.");
                alert.showAndWait();
            }
        });

        layout.getChildren().addAll(lblTitle, emailBox, passBox, btnLogin, footerRow);
    }

    public VBox getLayout() {
        return layout;
    }
}
