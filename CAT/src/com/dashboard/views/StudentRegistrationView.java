package com.dashboard.views;

import com.dashboard.App;
import com.dashboard.services.StudentService;
import com.dashboard.services.EmailService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class StudentRegistrationView {

    private VBox layout;
    
    private TextField txtFirstName;
    private TextField txtLastName;
    private TextField txtEmail;
    private ComboBox<String> comboClassYear;
    private TextField txtAddress;
    
    private ComboBox<String> comboMonth;
    private ComboBox<String> comboDay;
    private ComboBox<String> comboYear;
    
    private ToggleGroup genderGroup;
    private RadioButton rbMale;
    private RadioButton rbFemale;
    private RadioButton rbOther;
    
    private TextField txtPhone;

    public StudentRegistrationView() {
                layout = new VBox(20);
        layout.setPadding(new Insets(20, 40, 20, 40));
        layout.setStyle("-fx-background-color: transparent;");

        HBox bannerContainer = new HBox();
        bannerContainer.setAlignment(Pos.CENTER);
        bannerContainer.setPrefHeight(60);
        bannerContainer.setStyle("-fx-background-color: #f0fdf4; -fx-background-radius: 6; -fx-border-color: #bbf7d0; -fx-border-width: 1;");
        Label lblBanner = new Label("Online Student Registration Form");
        lblBanner.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #166534; -fx-font-family: 'Arial';");
        bannerContainer.getChildren().add(lblBanner);
        layout.getChildren().add(bannerContainer);
        
        // Form Grid
        GridPane formGrid = new GridPane();
        formGrid.setHgap(30);
        formGrid.setVgap(14);

        // Created a white container card wrapper with round corners and soft drop shadow
        VBox formCardWrapper = new VBox(formGrid);
        formCardWrapper.setPadding(new Insets(30));
        
        // FIX IS HERE: Stretches the form card width dynamically to match the banner box above it perfectly
        formCardWrapper.setMaxWidth(Double.MAX_VALUE);
        
        formCardWrapper.setStyle(
            "-fx-background-color: #ffffff; " +
            "-fx-background-radius: 8; " +
            "-fx-border-color: #cbd5e1; " +
            "-fx-border-width: 1; " +
            "-fx-border-radius: 8; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.08), 12, 0, 0, 5);"
        );
        VBox.setVgrow(formCardWrapper, Priority.ALWAYS);
        // Updated text styles to provide sharp visibility on a solid white surface
        String labelColorStyle = "-fx-text-fill: #334155; -fx-font-size: 13px; -fx-font-weight: bold;";

        VBox boxFN = new VBox(5);
        Label lblFN = new Label("First Name *");
        lblFN.setStyle(labelColorStyle);
        txtFirstName = new TextField();
        txtFirstName.setPromptText("First Name");
        txtFirstName.setStyle("-fx-padding: 10; -fx-border-color: #cbd5e1; -fx-border-radius: 4; -fx-background-color: white;");
        boxFN.getChildren().addAll(lblFN, txtFirstName);
        GridPane.setHgrow(boxFN, Priority.ALWAYS);

        VBox boxLN = new VBox(5);
        Label lblLN = new Label("Last Name (Optional)");
        lblLN.setStyle(labelColorStyle);
        txtLastName = new TextField();
        txtLastName.setPromptText("Last Name");
        txtLastName.setStyle("-fx-padding: 10; -fx-border-color: #cbd5e1; -fx-border-radius: 4; -fx-background-color: white;");
        boxLN.getChildren().addAll(lblLN, txtLastName);
        GridPane.setHgrow(boxLN, Priority.ALWAYS);

        formGrid.add(boxFN, 0, 0);
        formGrid.add(boxLN, 1, 0);

        VBox boxEmail = new VBox(5);
        Label lblEmail = new Label("Email Address *");
        lblEmail.setStyle(labelColorStyle);
        txtEmail = new TextField();
        txtEmail.setPromptText("Email Address");
        txtEmail.setStyle("-fx-padding: 10; -fx-border-color: #cbd5e1; -fx-border-radius: 4; -fx-background-color: white;");
        boxEmail.getChildren().addAll(lblEmail, txtEmail);
        formGrid.add(boxEmail, 0, 1, 2, 1);

        VBox boxClass = new VBox(5);
        Label lblClass = new Label("Class *");
        lblClass.setStyle(labelColorStyle);
        comboClassYear = new ComboBox<>();
        comboClassYear.setPromptText("Select Class Year");
        comboClassYear.setMaxWidth(Double.MAX_VALUE);
        comboClassYear.getItems().addAll("Year 1", "Year 2", "Year 3", "Year 4", "Year 5");
        comboClassYear.setStyle("-fx-padding: 4; -fx-border-color: #cbd5e1; -fx-border-radius: 4; -fx-background-color: white;");
        boxClass.getChildren().addAll(lblClass, comboClassYear);
        formGrid.add(boxClass, 0, 2, 2, 1);

        VBox boxAddress = new VBox(5);
        Label lblAddress = new Label("Physical Address *");
        lblAddress.setStyle(labelColorStyle);
        txtAddress = new TextField();
        txtAddress.setPromptText("Physical Address");
        txtAddress.setStyle("-fx-padding: 10; -fx-border-color: #cbd5e1; -fx-border-radius: 4; -fx-background-color: white;");
        boxAddress.getChildren().addAll(lblAddress, txtAddress);
        formGrid.add(boxAddress, 0, 3, 2, 1);

        VBox boxDob = new VBox(5);
        Label lblDob = new Label("Date of Birth *");
        lblDob.setStyle(labelColorStyle);
        HBox dobRow = new HBox(10);
        comboMonth = new ComboBox<>(); comboMonth.setPromptText("Month"); comboMonth.setPrefWidth(120);
        comboMonth.getItems().addAll("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December");
        comboDay = new ComboBox<>(); comboDay.setPromptText("Day"); comboDay.setPrefWidth(100);
        for(int i=1; i<=31; i++) comboDay.getItems().add(String.valueOf(i));
        comboYear = new ComboBox<>(); comboYear.setPromptText("Year"); comboYear.setPrefWidth(100);
        for(int i=1990; i<=2026; i++) comboYear.getItems().add(String.valueOf(i));
        
        String comboStyle = "-fx-padding: 4; -fx-border-color: #cbd5e1; -fx-border-radius: 4; -fx-background-color: white;";
        comboMonth.setStyle(comboStyle); comboDay.setStyle(comboStyle); comboYear.setStyle(comboStyle);
        
        Button btnCalendar = new Button("📅");
        btnCalendar.setStyle("-fx-background-color: #e2e8f0; -fx-border-color: #cbd5e1; -fx-border-radius: 4; -fx-padding: 8 12;");
        
        dobRow.getChildren().addAll(comboMonth, comboDay, comboYear, btnCalendar);
        boxDob.getChildren().addAll(lblDob, dobRow);
        formGrid.add(boxDob, 0, 4, 2, 1);

        VBox boxGender = new VBox(5);
        Label lblGender = new Label("Gender *");
        lblGender.setStyle(labelColorStyle);
        HBox genderRow = new HBox(20);
        genderGroup = new ToggleGroup();
        rbMale = new RadioButton("Male"); rbMale.setToggleGroup(genderGroup);
        rbFemale = new RadioButton("Female"); rbFemale.setToggleGroup(genderGroup);
        rbOther = new RadioButton("Other"); rbOther.setToggleGroup(genderGroup);
        genderRow.getChildren().addAll(rbMale, rbFemale, rbOther);
        boxGender.getChildren().addAll(lblGender, genderRow);
        formGrid.add(boxGender, 0, 5, 2, 1);

        // Updated radio button text elements to be clearly readable inside the card container panel
        rbMale.setStyle("-fx-text-fill: #334155; -fx-font-weight: 600;");
        rbFemale.setStyle("-fx-text-fill: #334155; -fx-font-weight: 600;");
        rbOther.setStyle("-fx-text-fill: #334155; -fx-font-weight: 600;");

                VBox boxPhone = new VBox(5);
        Label lblPhone = new Label("Phone Number *");
        lblPhone.setStyle(labelColorStyle);
        
        // Horizontal container row separating the country code selector and phone field
        HBox phoneInputRow = new HBox(10);
        phoneInputRow.setAlignment(Pos.CENTER_LEFT);
        
        // Country prefix selector locked to Rwanda by default
        ComboBox<String> comboCountryCode = new ComboBox<>();
        comboCountryCode.getItems().addAll("+250 (RW)");
        comboCountryCode.setValue("+250 (RW)");
        comboCountryCode.setDisable(true); // Locked by default as requested
        comboCountryCode.setStyle("-fx-padding: 4; -fx-border-color: #cbd5e1; -fx-border-radius: 4; -fx-background-color: #f8fafc; -fx-font-weight: bold;");
        comboCountryCode.setPrefWidth(120);

        txtPhone = new TextField();
        txtPhone.setPromptText("e.g., 788123456");
        txtPhone.setStyle("-fx-padding: 10; -fx-border-color: #cbd5e1; -fx-border-radius: 4; -fx-background-color: white;");
        HBox.setHgrow(txtPhone, Priority.ALWAYS);

        // REAL-TIME INPUT VALIDATION: Blocks non-numeric keys and cuts off input strictly at 10 digits maximum
        txtPhone.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                txtPhone.setText(newValue.replaceAll("[^\\d]", ""));
            }
            if (txtPhone.getText().length() > 10) {
                String s = txtPhone.getText().substring(0, 10);
                txtPhone.setText(s);
            }
        });

        phoneInputRow.getChildren().addAll(comboCountryCode, txtPhone);
        boxPhone.getChildren().addAll(lblPhone, phoneInputRow);
        formGrid.add(boxPhone, 0, 6, 2, 1);
        // Appended the white sheet wrapper instead of adding the raw translucent grid layout
        layout.getChildren().add(formCardWrapper);

        HBox footerControls = new HBox(15);
        footerControls.setAlignment(Pos.CENTER_RIGHT);
        footerControls.setPadding(new Insets(10, 0, 10, 0));

        Button btnCancel = new Button("Cancel");
        btnCancel.setStyle("-fx-background-color: #e2e8f0; -fx-text-fill: #334155; -fx-font-weight: bold; -fx-padding: 10 24; -fx-background-radius: 6; -fx-cursor: hand;");
        btnCancel.setOnAction(e -> App.navigateToDashboardCore());
        Button btnRegister = new Button("Register");
        //generatedStudentId, fName, lName, email, classYear, address
        btnRegister.setStyle("-fx-background-color: #0cbc87; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 28; -fx-background-radius: 6; -fx-cursor: hand;");
                btnRegister.setOnAction(e -> {
            String fName = txtFirstName.getText().trim();
            String lName = txtLastName.getText().trim();
            String email = txtEmail.getText().trim();
            String classYear = comboClassYear.getValue();
            String address = txtAddress.getText().trim();
            
            String m = comboMonth.getValue();
            String d = comboDay.getValue();
            String y = comboYear.getValue();
            String dob = (m != null && d != null && y != null) ? (y + "-" + m + "-" + d) : "";
            
            RadioButton selectedGen = (RadioButton) genderGroup.getSelectedToggle();
            String gender = (selectedGen != null) ? selectedGen.getText() : "";
            String phone = txtPhone.getText().trim();
            
            String regDate = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String registeredBy = App.getSessionUsername();

            if (fName.isEmpty() || email.isEmpty() || classYear == null || address.isEmpty() || dob.isEmpty() || gender.isEmpty() || phone.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "All fields marked with an asterisk (*) are mandatory.");
                alert.showAndWait();
                return;
            }

            int intakeValue = 1; 
            String generatedStudentId = StudentService.generateNextStudentId(intakeValue);

            boolean success = StudentService.registerStudentInDb(generatedStudentId, fName, lName, email, classYear, address, dob, gender, phone, regDate, registeredBy);
            if (success) {
                String studentFullName = fName + (lName.isEmpty() ? "" : " " + lName);
                
                // 1. STUDENT EMAIL MESSAGE STYLE
                String studentEmailBody = "<html><body>"
                        + "<div style='font-family:\"Segoe UI\",Arial,sans-serif;padding:30px;background-color:#ffffff;color:#1e293b;line-height:1.6;max-width:550px;border:1px solid #e2e8f0;border-radius:8px;'>"
                        + "<h2 style='color:#0cbc87;background-color:#e6fcf5;padding:12px;border-radius:6px;margin-top:0;font-size:18px;'>STUDENT REGISTRATION CONFIRMATION</h2>"
                        + "<p>Dear <strong>" + studentFullName + "</strong>,</p>"
                        + "<p>Congratulations! Your registration has been completed successfully.</p>"
                        + "<p><strong>Registration Details:</strong></p>"
                        + "<div style='background-color:#f8fafc;padding:15px;border-radius:6px;border-left:4px solid #0cbc87;font-family:monospace;font-size:14px;line-height:1.8;'>"
                        + "Student Name: " + studentFullName + "<br/>"
                        + "Registration Number: " + generatedStudentId + "<br/>"
                        + "Date Registered: " + regDate + ""
                        + "</div>"
                        + "<p>Your information has been successfully saved in our system. Please keep your registration number for future reference.</p>"
                        + "<p>We wish you success in your academic journey.</p>"
                        + "<p>Thank you.</p>"
                        + "<p style='margin-bottom:0;'>Best regards,</p>"
                        + "<p style='margin:0;'><strong>Administration</strong></p>"
                        + "</div>"
                        + "</body></html>";

                // 2. ADMIN SECURE LOG ALERT MESSAGE STYLE
                String adminEmailBody = "<html><body>"
                        + "<div style='font-family:\"Segoe UI\",Arial,sans-serif;padding:30px;background-color:#ffffff;color:#1e293b;line-height:1.6;max-width:550px;border:1px solid #e2e8f0;border-radius:8px;'>"
                        + "<h2 style='color:#0c6291;background-color:#dbeafe;padding:12px;border-radius:6px;margin-top:0;font-size:18px;'>NEW ENROLLMENT TRANSACTION ACTION LOG</h2>"
                        + "<p>Hello <strong>System Control Log Panel</strong>,</p>"
                        + "<p>This is an automated system security notice confirming that a new student account profile record has been successfully added to the system database logs.</p>"
                        + "<p><strong>Transaction Audit Summary Details:</strong></p>"
                        + "<div style='background-color:#f8fafc;padding:15px;border-radius:6px;border-left:4px solid #0c6291;font-family:monospace;font-size:14px;line-height:1.8;'>"
                        + "Enrolled Name: " + studentFullName + "<br/>"
                        + "System Serial Key ID: " + generatedStudentId + "<br/>"
                        + "Registered Email: " + email + "<br/>"
                        + "Date Timestamp: " + regDate + "<br/>"
                        + "Processed By Admin Account Profile: <strong>" + registeredBy + "</strong>"
                        + "</div>"
                        + "<p>Please ensure this data matches the physical records filed in the central administrative office archives.</p>"
                        + "<p style='margin-bottom:0;'>Best regards,</p>"
                        + "<p style='margin:0;'><strong>Ubumwe School Security Portal</strong></p>"
                        + "</div>"
                        + "</body></html>";

                // Dispatching the custom tailored message packages to each distinct target
                EmailService.sendSystemEmailAsync(email, "Official Student Registration Confirmation", studentEmailBody);
                EmailService.sendSystemEmailAsync("nzerneste250@gmail.com", "System Audit Action Notification Log: Student Registered", adminEmailBody);

                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Student Registered Successfully! Confirmation emails dispatched.");
                alert.showAndWait();
                clearAllFields();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Registration failed. Please try again.");
                alert.showAndWait();
            }
        });
footerControls.getChildren().addAll(btnCancel, btnRegister);
layout.getChildren().add(footerControls);
}

private void clearAllFields() {
    txtFirstName.clear(); 
    txtLastName.clear(); 
    txtEmail.clear();
    comboClassYear.setValue(null); 
    txtAddress.clear();
    comboMonth.setValue(null); 
    comboDay.setValue(null); 
    comboYear.setValue(null);
    genderGroup.selectToggle(null); 
    txtPhone.clear();
}

public VBox getLayout() {
    return layout;
}
}
