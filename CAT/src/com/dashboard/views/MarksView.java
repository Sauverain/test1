package com.dashboard.views;

import com.dashboard.App;
import com.dashboard.services.StudentService;
import com.dashboard.services.EmailService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class MarksView {

    private VBox layout;

    public MarksView() {
        layout = new VBox(20);
        layout.setStyle("-fx-background-color: transparent;");
        layout.setAlignment(Pos.TOP_CENTER);

        VBox topBanner = new VBox();
        topBanner.setAlignment(Pos.CENTER);
        topBanner.setPadding(new Insets(15));
        topBanner.setMaxWidth(1000);
        topBanner.setStyle("-fx-background-color: #f0fdf4; -fx-background-radius: 4;");
        Label lblBannerTitle = new Label("Academic Performance Marking Sheet");
        lblBannerTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #14532d; -fx-font-family: 'Arial';");
        topBanner.getChildren().add(lblBannerTitle);

        VBox mainCard = new VBox(20);
        mainCard.setPadding(new Insets(35));
        mainCard.setMaxWidth(1000);
        boolean dk = App.isDarkMode();
        mainCard.setStyle(dk ? "-fx-background-color: #1e293b; -fx-border-color: #334155; -fx-border-radius: 6;" : "-fx-background-color: #ffffff; -fx-border-color: #e2e8f0; -fx-border-radius: 6;");

        HBox searchRow = new HBox(15);
        searchRow.setAlignment(Pos.CENTER_LEFT);
        Label lblSearchTitle = new Label("Enter Student ID to Mark:");
        lblSearchTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; " + (dk ? "-fx-text-fill: #cbd5e1;" : "-fx-text-fill: #1e293b;"));
        TextField txtStudentId = new TextField();
        txtStudentId.setPromptText("e.g., 2601001");
        txtStudentId.setPrefWidth(180);
        txtStudentId.setStyle("-fx-padding: 8; -fx-border-color: #cbd5e1; -fx-border-radius: 4; -fx-background-color: #ffffff;");
        Button btnLoad = new Button("Load Student");
        btnLoad.setStyle("-fx-background-color: #1e293b; -fx-text-fill: #ffffff; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 4; -fx-cursor: hand;");
        searchRow.getChildren().addAll(lblSearchTitle, txtStudentId, btnLoad);

        Label lblStatus = new Label("Status: Waiting for input student query ID resolution code...");
        lblStatus.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: #2563eb;");

        GridPane gradesGrid = new GridPane();
        gradesGrid.setHgap(20); gradesGrid.setVgap(18);
        gradesGrid.setAlignment(Pos.CENTER_LEFT);

        Label lblJava = new Label("Java Programming (Marks /100) *");
        TextField txtJava = new TextField();
        Label lblDbms = new Label("Database Management System (/100) *");
        TextField txtDbms = new TextField();
        Label lblWeb = new Label("Web Design (/100) *");
        TextField txtWeb = new TextField();
        Label lblEnt = new Label("Entrepreneurship (/100) *");
        TextField txtEnt = new TextField();
        Label lblLang = new Label("Language (/100) *");
        TextField txtLang = new TextField();

        String fieldStyle = "-fx-padding: 8; -fx-border-color: #e2e8f0; -fx-border-radius: 6; -fx-background-color: #ffffff; -fx-pref-width: 160;";
        txtJava.setStyle(fieldStyle); txtDbms.setStyle(fieldStyle); txtWeb.setStyle(fieldStyle);
        txtEnt.setStyle(fieldStyle); txtLang.setStyle(fieldStyle);

        gradesGrid.add(lblJava, 0, 0); gradesGrid.add(txtJava, 1, 0);
        gradesGrid.add(lblDbms, 0, 1); gradesGrid.add(txtDbms, 1, 1);
        gradesGrid.add(lblWeb, 0, 2); gradesGrid.add(txtWeb, 1, 2);
        gradesGrid.add(lblEnt, 0, 3); gradesGrid.add(txtEnt, 1, 3);
        gradesGrid.add(lblLang, 0, 4); gradesGrid.add(txtLang, 1, 4);

        String labelStyle = "-fx-font-size: 13px; -fx-text-fill: #64748b;";
        lblJava.setStyle(labelStyle); lblDbms.setStyle(labelStyle); lblWeb.setStyle(labelStyle);
        lblEnt.setStyle(labelStyle); lblLang.setStyle(labelStyle);

        if (dk) {
            lblJava.setStyle("-fx-text-fill: #94a3b8;"); lblDbms.setStyle("-fx-text-fill: #94a3b8;");
            lblWeb.setStyle("-fx-text-fill: #94a3b8;"); lblEnt.setStyle("-fx-text-fill: #94a3b8;");
            lblLang.setStyle("-fx-text-fill: #94a3b8;");
        }

        HBox footerActionRow = new HBox(15);
        footerActionRow.setAlignment(Pos.CENTER_RIGHT);
        Button btnBack = new Button("Back");
        btnBack.setStyle("-fx-background-color: #cbd5e1; -fx-text-fill: #1e3a8b; -fx-font-weight: bold; -fx-padding: 10 24; -fx-background-radius: 4; -fx-cursor: hand;");
        Button btnCalculate = new Button("Calculate & Save Grades");
        btnCalculate.setStyle("-fx-background-color: #e2e8f0; -fx-text-fill: #94a3b8; -fx-font-weight: bold; -fx-padding: 10 24; -fx-background-radius: 4; -fx-cursor: hand;");

        txtJava.textProperty().addListener((o, old, nw) -> evaluateFormCompleteness(txtJava, txtDbms, txtWeb, txtEnt, txtLang, btnCalculate));
        txtDbms.textProperty().addListener((o, old, nw) -> evaluateFormCompleteness(txtJava, txtDbms, txtWeb, txtEnt, txtLang, btnCalculate));
        txtWeb.textProperty().addListener((o, old, nw) -> evaluateFormCompleteness(txtJava, txtDbms, txtWeb, txtEnt, txtLang, btnCalculate));
        txtEnt.textProperty().addListener((o, old, nw) -> evaluateFormCompleteness(txtJava, txtDbms, txtWeb, txtEnt, txtLang, btnCalculate));
        txtLang.textProperty().addListener((o, old, nw) -> evaluateFormCompleteness(txtJava, txtDbms, txtWeb, txtEnt, txtLang, btnCalculate));

        footerActionRow.getChildren().addAll(btnBack, btnCalculate);
        mainCard.getChildren().addAll(searchRow, lblStatus, gradesGrid, footerActionRow);
        layout.getChildren().addAll(topBanner, mainCard);

        btnBack.setOnAction(e -> App.navigateToDashboardCore());

        btnLoad.setOnAction(e -> {
            String studentId = txtStudentId.getText().trim();
            if (studentId.isEmpty()) return;

            String studentName = StudentService.getStudentNameById(studentId);
            if (studentName == null || studentName.equals("Unknown Student")) {
                lblStatus.setText("Status: Error! No student profile found for ID " + studentId);
                lblStatus.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: #ef4444;");
                return;
            }

            boolean alreadyMarked = StudentService.hasStudentBeenMarked(studentId);
            if (alreadyMarked) {
                Alert confirmationBox = new Alert(Alert.AlertType.INFORMATION);
                confirmationBox.setTitle("Evaluation Exists");
                confirmationBox.setHeaderText("Student Already Evaluated: " + studentName);
                confirmationBox.setContentText("Click OK to automatically redirect to the Reports interface and look up this script row configuration.");
                
                if (confirmationBox.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                    App.navigateToReportPageWithHighlight(studentId);
                }
            } else {
                lblStatus.setText("Status: Student found (" + studentName + "). Ready for grading input metrics.");
                lblStatus.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: #10b981;");
            }
        });

        btnCalculate.setOnAction(e -> {
            String studentId = txtStudentId.getText().trim();
            if (studentId.isEmpty()) return;

            String studentFullName = StudentService.getStudentNameById(studentId);
            if (studentFullName == null || studentFullName.equals("Unknown Student")) return;

            try {
                double j = Double.parseDouble(txtJava.getText().trim());
                double d = Double.parseDouble(txtDbms.getText().trim());
                double w = Double.parseDouble(txtWeb.getText().trim());
                double ent = Double.parseDouble(txtEnt.getText().trim());
                double l = Double.parseDouble(txtLang.getText().trim());

                if (j < 0 || j > 100 || d < 0 || d > 100 || w < 0 || w > 100 || ent < 0 || ent > 100 || l < 0 || l > 100) {
                    new Alert(Alert.AlertType.WARNING, "Values must remain strictly bound between 0 and 100.").showAndWait();
                    return;
                }

                double average = (j + d + w + ent + l) / 5.0;
                String grade = (average >= 85) ? "A" : (average >= 70) ? "B" : (average >= 55) ? "C" : (average >= 40) ? "D" : "Fail";

                boolean success = StudentService.saveStudentMarks(studentId, j, d, w, ent, l, average, grade);
                if (success) {
                    String targetStudentEmail = "Unknown";
                    String findEmailSql = "SELECT email FROM students WHERE student_id = ?";
                    try (java.sql.Connection conn = java.sql.DriverManager.getConnection("jdbc:mysql://10.240.97.36:3306/school", "cat", "Kigali@1234");
                         java.sql.PreparedStatement pstmt = conn.prepareStatement(findEmailSql)) {
                        pstmt.setString(1, studentId);
                        try (java.sql.ResultSet rs = pstmt.executeQuery()) {
                            if (rs.next()) targetStudentEmail = rs.getString("email");
                        }
                    } catch (Exception ex) {}

                    if (!targetStudentEmail.equals("Unknown")) {
                        // FIX: Added explicit paragraph tags <p> and list rows <br/> so it formats beautifully inside email clients
                        String emailBody = "<html><body>"
                 + "<div style='font-family:\"Segoe UI\",Arial,sans-serif;padding:30px;background-color:#ffffff;color:#1e293b;line-height:1.6;max-width:550px;border:1px solid #cbd5e1;border-radius:8px;'>"
                                + "<h2 style='color:#10b981;background-color:#e6fcf5;padding:12px;border-radius:6px;margin-top:0;font-size:18px;'>REPORT CARD TRANSCRIPT RELEASE</h2>"
                                + "<p>Dear <strong>" + studentFullName + "</strong>,</p>"
                                + "<p>Your official academic performance grading results for this term have been compiled and published.</p>"
                                + "<div style='background-color:#f8fafc;padding:15px;border-radius:6px;border-left:4px solid #10b981;font-family:monospace;font-size:14px;line-height:1.8;'>"
                                + "Student ID: " + studentId+ "<br/>"
                                + "Full Name: " + studentFullName + "<br/>"
                                + "Java Programming: " + String.format("%.1f", j) + "%<br/>"
                                + "Database Management: " + String.format("%.1f", d) + "%<br/>"
                                + "Web Architecture: " + String.format("%.1f", w) + "%<br/>"
                                + "Entrepreneurship: " + String.format("%.1f", ent) + "%<br/>"
                                + "Language Course: " + String.format("%.1f", l) + "%<br/>"
                                + "OVERALL AVERAGE: <strong>" + String.format("%.1f", average) + "%</strong><br/>"
                                + "FINAL GRADE: <strong style='color:#10b981;'>" + grade + "</strong>"
                                + "</div>"
                                + "<p>Please contact the administration office if you require a physically stamped certificate sheet.</p>"
                                + "<p>Best regards,<br/><strong>Ubumwe School Administration</strong></p>"
                                + "</div>"
                                + "</body></html>";
                
            EmailService.sendSystemEmailAsync(targetStudentEmail, "Official Academic Report Card Release Notification", emailBody);
        }
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Marks recorded successfully! Notification email dispatched.");
        alert.showAndWait();
        
        clearFormFields(txtStudentId, txtJava, txtDbms, txtWeb, txtEnt, txtLang, lblStatus);
        App.navigateToDashboardCore();
    } else {
        new Alert(Alert.AlertType.ERROR, "Database transaction dropped. Verify database connectivity properties.").showAndWait();
    }
    } catch (NumberFormatException ex) {
        new Alert(Alert.AlertType.ERROR, "Input violation. Ensure fields contain numeric expressions.").showAndWait();
    }
});
}

private void evaluateFormCompleteness(TextField j, TextField d, TextField w, TextField e, TextField l, Button calcBtn) {
    boolean populated = !j.getText().trim().isEmpty() 
            && !d.getText().trim().isEmpty()
            && !w.getText().trim().isEmpty() 
            && !e.getText().trim().isEmpty()
            && !l.getText().trim().isEmpty();
            
    if (populated) {
        calcBtn.setDisable(false);
        calcBtn.setStyle("-fx-background-color: #10b981; -fx-text-fill: #ffffff; -fx-font-weight: bold; -fx-padding: 10 24; -fx-background-radius: 4; -fx-cursor: hand;");
    } else {
        calcBtn.setDisable(true);
        calcBtn.setStyle("-fx-background-color: #e2e8f0; -fx-text-fill: #94a3b8; -fx-font-weight: bold; -fx-padding: 10 24; -fx-background-radius: 4; -fx-cursor: hand;");
    }
}

private void clearFormFields(TextField id, TextField j, TextField d, TextField w, TextField e, TextField l, Label status) {
    id.clear(); 
    j.clear(); 
    d.clear(); 
    w.clear(); 
    e.clear(); 
    l.clear();
    status.setText("Status: Waiting for input student query ID resolution code...");
    status.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: #2563eb;");
}

public VBox getLayout() {
    return layout;
}
}
