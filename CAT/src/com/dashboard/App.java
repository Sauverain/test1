package com.dashboard;

import com.dashboard.views.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class App extends Application {

    private static Stage primaryStage;
    private static VBox mainContentArea;
    private static HBox rootLayout;
    private static VBox sidebarContainer;
    private static String sessionUsername = "Admin User";
    private static boolean isDarkMode = false;
    
    public static javafx.scene.control.Button globalBtnDashboard;
    public static javafx.scene.control.Button globalBtnRegistration;
    public static javafx.scene.control.Button globalBtnViewStudents;
    public static javafx.scene.control.Button globalBtnMarks;
    public static javafx.scene.control.Button globalBtnReport;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        primaryStage.setTitle("Student Management System");
        
        try {
            System.setProperty("javafx.platform", "win");
            System.setProperty("glass.win.appId", "com.dashboard.studentsystem.v1");
        } catch (Exception e) {}
        
        applyCustomWindowIcon();
        navigateToLogin();
    }

    private static Image locateInstitutionalLogoImage() {
        try {
            java.io.InputStream is = App.class.getResourceAsStream("/com/dashboard/assets/logo.png");
            if (is != null) {
                return new Image(is);
            }
            java.io.File file1 = new java.io.File("src/com/dashboard/assets/logo.png");
            if (file1.exists()) {
                return new Image(file1.toURI().toString());
            }
            java.io.File file2 = new java.io.File("logo.png");
            if (file2.exists()) {
                return new Image(file2.toURI().toString());
            }
        } catch (Exception e) {
            System.err.println("Logo layout stream search exception: " + e.getMessage());
        }
        return null;
    }

    private static void applyCustomWindowIcon() {
        Image img = locateInstitutionalLogoImage();
        if (img != null && !img.isError()) {
            primaryStage.getIcons().clear();
            primaryStage.getIcons().add(img);
        }
    }

    public static void navigateToLogin() {
        isDarkMode = false;
        LoginView loginView = new LoginView();
        Scene scene = new Scene(loginView.getLayout(), 500, 550);
        
        primaryStage.setMaximized(false);
        primaryStage.setWidth(516);
        primaryStage.setHeight(589);
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        applyCustomWindowIcon();
        primaryStage.show();
    }

    public static void navigateToSignup() {
        SignupView signupView = new SignupView();
        Scene scene = new Scene(signupView.getLayout(), 500, 620);
        primaryStage.setScene(scene);
        applyCustomWindowIcon();
        primaryStage.show();
    }

    public static void setSessionUsername(String username) {
        sessionUsername = username;
    }

    public static String getSessionUsername() {
        return sessionUsername;
    }

    public static boolean isDarkMode() {
        return isDarkMode;
    }

    public static void toggleGlobalTheme(boolean enableDark) {
        isDarkMode = enableDark;
        applyGlobalStyles();
    }

    public static void navigateToDashboardCore() {
        sidebarContainer = createSidebarStructure();
        mainContentArea = new VBox(25);
        HBox.setHgrow(mainContentArea, Priority.ALWAYS);

        updateMainContent(new DashboardView(sessionUsername).getLayout());

        rootLayout = new HBox(sidebarContainer, mainContentArea);
        Scene scene = new Scene(rootLayout, 1200, 800);
        
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        applyGlobalStyles();
        highlightActiveSidebarButton(globalBtnDashboard);
        applyCustomWindowIcon();
        primaryStage.show();
    }

    public static void updateMainContent(Region layoutContent) {
        if (mainContentArea != null) {
            mainContentArea.getChildren().clear();
            mainContentArea.getChildren().add(layoutContent);
            VBox.setVgrow(layoutContent, Priority.ALWAYS);
            applyGlobalStyles();
        }
    }

    private static VBox createSidebarStructure() {
        VBox sidebar = new VBox(15);
        sidebar.setPadding(new javafx.geometry.Insets(25, 12, 25, 12));
        sidebar.setPrefWidth(240);
        sidebar.setStyle("-fx-background-color: #0f172a;");

        VBox brandBox = new VBox(12);
        brandBox.setAlignment(Pos.CENTER);
        brandBox.setStyle("-fx-padding: 0 0 20 0;");

        Image imgLogo = locateInstitutionalLogoImage();
        if (imgLogo != null && !imgLogo.isError()) {
            ImageView sidebarLogo = new ImageView(imgLogo);
            sidebarLogo.setFitWidth(180);
            sidebarLogo.setFitHeight(180);
            sidebarLogo.setPreserveRatio(true);
            sidebarLogo.setSmooth(true);
            
            Rectangle borderClipClip = new Rectangle(180, 180);
            borderClipClip.setArcWidth(24);
            borderClipClip.setArcHeight(24);
            sidebarLogo.setClip(borderClipClip);
            
            brandBox.getChildren().add(sidebarLogo);
        }

        javafx.scene.control.Label brandLabel = new javafx.scene.control.Label("UBUMWE SCHOOL SYSTEM");
        brandLabel.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 13px; -fx-font-weight: bold; -fx-font-family: 'Segoe UI', Arial; -fx-text-alignment: center;");
        brandBox.getChildren().add(brandLabel);

        globalBtnDashboard = new javafx.scene.control.Button("Dashboard");
        globalBtnRegistration = new javafx.scene.control.Button("Registration");
        globalBtnViewStudents = new javafx.scene.control.Button("View Students");
        globalBtnMarks = new javafx.scene.control.Button("Marks");
        globalBtnReport = new javafx.scene.control.Button("Report");
        javafx.scene.control.Button btnLogout = new javafx.scene.control.Button("Logout");

        globalBtnDashboard.setMaxWidth(Double.MAX_VALUE);
        globalBtnRegistration.setMaxWidth(Double.MAX_VALUE);
        globalBtnViewStudents.setMaxWidth(Double.MAX_VALUE);
        globalBtnMarks.setMaxWidth(Double.MAX_VALUE);
        globalBtnReport.setMaxWidth(Double.MAX_VALUE);
        btnLogout.setMaxWidth(Double.MAX_VALUE);

        globalBtnDashboard.setOnAction(e -> {
            highlightActiveSidebarButton(globalBtnDashboard);
            updateMainContent(new DashboardView(sessionUsername).getLayout());
        });

        globalBtnRegistration.setOnAction(e -> {
            highlightActiveSidebarButton(globalBtnRegistration);
            updateMainContent(new StudentRegistrationView().getLayout());
        });

        globalBtnViewStudents.setOnAction(e -> {
            highlightActiveSidebarButton(globalBtnViewStudents);
            updateMainContent(new StudentTableView().getLayout());
        });

        globalBtnMarks.setOnAction(e -> {
            highlightActiveSidebarButton(globalBtnMarks);
            updateMainContent(new MarksView().getLayout());
        });

        globalBtnReport.setOnAction(e -> {
            highlightActiveSidebarButton(globalBtnReport);
            updateMainContent(new ReportView().getLayout());
        });

        btnLogout.setOnAction(e -> {
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Logout Session");
            confirmation.setHeaderText("Confirm Administrative Account Logout");
            confirmation.setContentText(sessionUsername + ", are you sure you want to logout?");
            if (confirmation.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                navigateToLogin();
            }
        });

        VBox sidebarSpacer = new VBox();
        VBox.setVgrow(sidebarSpacer, Priority.ALWAYS);

        sidebar.getChildren().addAll(brandBox, globalBtnDashboard, globalBtnRegistration, globalBtnViewStudents, globalBtnMarks, globalBtnReport, sidebarSpacer, btnLogout);
        return sidebar;
    }

    private static void highlightActiveSidebarButton(javafx.scene.control.Button activeButton) {
        String baseBtnStyle = "-fx-background-color: transparent; -fx-text-fill: #94a3b8; -fx-font-size: 14px; -fx-alignment: center-left; -fx-cursor: hand; -fx-padding: 10 15 10 15; -fx-background-radius: 6;";
        
        globalBtnDashboard.setStyle(baseBtnStyle);
        globalBtnRegistration.setStyle(baseBtnStyle);
        globalBtnViewStudents.setStyle(baseBtnStyle);
        globalBtnMarks.setStyle(baseBtnStyle);
        globalBtnReport.setStyle(baseBtnStyle);
        
        activeButton.setStyle("-fx-background-color: #1e293b; -fx-text-fill: #38bdf8; -fx-font-size: 14px; -fx-alignment: center-left; -fx-cursor: hand; -fx-padding: 10 15 10 15; -fx-background-radius: 6; -fx-font-weight: bold; -fx-border-color: #334155; -fx-border-width: 1; -fx-border-radius: 6;");
    }

    public static void navigateToReportPageFromRedirect() {
        if (globalBtnReport != null) {
            highlightActiveSidebarButton(globalBtnReport);
            updateMainContent(new ReportView().getLayout());
        }
    }

    public static void navigateToReportPageWithHighlight(String targetStudentId) {
        if (globalBtnReport != null) {
            highlightActiveSidebarButton(globalBtnReport);
            ReportView reportViewInstance = new ReportView();
                    updateMainContent(reportViewInstance.getLayout());
        reportViewInstance.focusAndHighlightStudentRow(targetStudentId);
    }
}

    private static void applyGlobalStyles() {
        if (rootLayout == null || sidebarContainer == null || mainContentArea == null) return;

        javafx.scene.control.Button logoutBtn = (javafx.scene.control.Button) sidebarContainer.getChildren().get(sidebarContainer.getChildren().size() - 1);
        logoutBtn.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-alignment: center; -fx-cursor: hand; -fx-background-radius: 6; -fx-padding: 10;");

        sidebarContainer.setStyle("-fx-background-color: #0f172a; -fx-border-color: #1e293b; -fx-border-width: 0 1 0 0;");
        
        VBox brandBox = (VBox) sidebarContainer.getChildren().get(0);
        javafx.scene.control.Label brand = (javafx.scene.control.Label) brandBox.getChildren().get(brandBox.getChildren().size() - 1);
        brand.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 13px; -fx-font-weight: bold; -fx-text-alignment: center;");

        if (isDarkMode) {
            rootLayout.setStyle("-fx-background-color: #0f172a;");
            mainContentArea.setStyle("-fx-background-color: #0f172a; -fx-padding: 30;"); // Main area turns dark
        } else {
            rootLayout.setStyle("-fx-background-color: #f1f5f9;");
            mainContentArea.setStyle("-fx-background-color: #f8fafc; -fx-padding: 30;"); // Main area turns light
        }
    }

public static void main(String[] args) {
    launch(args);
}
}
