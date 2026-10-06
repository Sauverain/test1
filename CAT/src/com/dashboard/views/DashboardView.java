package com.dashboard.views;

import com.dashboard.App;
import com.dashboard.models.StudentReportModel;
import com.dashboard.services.StudentService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class DashboardView {

    private VBox layout;
    private StackPane dynamicContainer;
    private VBox analyticsBox;
    private VBox tableContainerBox;
    private TableView<StudentReportModel> resultsTable;
    
    private Label titleLabel;
    private Label userProfileLabel;
    private VBox cardRegistered;
    private VBox cardPerformance;
    private Label lblRegTitle;
    private Label lblRegValue;
    private Label lblPerfTitle;
    private Label lblPerfValue;
    private Label chartTitle;
    private Label tableTitle;
    
    private VBox schoolBrandingHeaderCard;
    private Label lblSchoolName;
    private Label lblSchoolSlogan;

    private ObservableList<StudentReportModel> backendMasterDataList;
    private FilteredList<StudentReportModel> filteredResultsDataList;
    private ScheduledService<ObservableList<StudentReportModel>> backgroundPollingSyncService;
    private String searchFilterStringQuery = "";

    public DashboardView(String loggedInUserEmail) {
        layout = new VBox(25);
        layout.setPadding(new Insets(10, 0, 0, 0));

        schoolBrandingHeaderCard = new VBox(5);
        schoolBrandingHeaderCard.setAlignment(Pos.CENTER);
        schoolBrandingHeaderCard.setPadding(new Insets(15));
        lblSchoolName = new Label("UBUMWE SCHOOL");
        lblSchoolSlogan = new Label("LEARN . GROW . INNOVATE . SUCCEED");
        schoolBrandingHeaderCard.getChildren().addAll(lblSchoolName, lblSchoolSlogan);
        layout.getChildren().add(schoolBrandingHeaderCard);

        HBox topHeaderBar = new HBox(15);
        topHeaderBar.setAlignment(Pos.CENTER_LEFT);
        
        titleLabel = new Label("Dashboard Overview");
        HBox topSpacer = new HBox();
        HBox.setHgrow(topSpacer, Priority.ALWAYS);

        userProfileLabel = new Label("Hi, " + App.getSessionUsername());

        TextField searchField = new TextField();
        searchField.setPromptText("Search ID, Name, or Year...");
        searchField.setPrefWidth(240);
        searchField.setStyle("-fx-background-color: white; -fx-border-color: #cbd5e1; -fx-border-radius: 5; -fx-background-radius: 5; -fx-padding: 8; -fx-font-size: 13px;");

        MenuButton btnSettingsDropdown = new MenuButton("⚙ Settings");
        btnSettingsDropdown.setStyle("-fx-background-color: #e2e8f0; -fx-text-fill: #334155; -fx-font-weight: bold; -fx-padding: 5 10; -fx-background-radius: 5; -fx-cursor: hand;");
        
        MenuItem itemToggleTheme = new MenuItem(App.isDarkMode() ? "☀️ Light Mode" : "🌙 Dark Mode");
        btnSettingsDropdown.getItems().add(itemToggleTheme);

        topHeaderBar.getChildren().addAll(titleLabel, topSpacer, userProfileLabel, searchField, btnSettingsDropdown);
        layout.getChildren().add(topHeaderBar);

        int liveTotalStudents = StudentService.getTotalStudentCount();
        double livePerformanceAverage = StudentService.getOverallPerformanceAverage();

        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(20); statsGrid.setVgap(20);
        
        cardRegistered = new VBox(8);
        cardRegistered.setPadding(new Insets(20)); cardRegistered.setPrefWidth(350);
        lblRegTitle = new Label("Total Registered Students"); 
        lblRegValue = new Label(String.valueOf(liveTotalStudents)); 
        Label lblRegSub = new Label("Active Students"); lblRegSub.setStyle("-fx-text-fill: #10b981; -fx-font-size: 12px;");
        cardRegistered.getChildren().addAll(lblRegTitle, lblRegValue, lblRegSub);

        cardPerformance = new VBox(8);
        cardPerformance.setPadding(new Insets(20)); cardPerformance.setPrefWidth(350);
        lblPerfTitle = new Label("Average Student Performance"); 
        lblPerfValue = new Label(String.format("%.1f%%", livePerformanceAverage)); 
        Label lblPerfSub = new Label("Overall Class Passing Rate"); lblPerfSub.setStyle("-fx-text-fill: #6366f1; -fx-font-size: 12px;");
        cardPerformance.getChildren().addAll(lblPerfTitle, lblPerfValue, lblPerfSub);

        statsGrid.add(cardRegistered, 0, 0); statsGrid.add(cardPerformance, 1, 0);
        layout.getChildren().add(statsGrid);

        dynamicContainer = new StackPane();
        VBox.setVgrow(dynamicContainer, Priority.ALWAYS);

        analyticsBox = new VBox(10);
        analyticsBox.setPadding(new Insets(20));
        
        chartTitle = new Label("Performance Data Analytics (Live Grade Distribution)");
        CategoryAxis xAxis = new CategoryAxis(); xAxis.setLabel("Grade Groups");
        NumberAxis yAxis = new NumberAxis(); yAxis.setLabel("Number of Students");
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setLegendVisible(false); barChart.setAnimated(false);
        XYChart.Series<String, Number> dataSeries = new XYChart.Series<>();
        dataSeries.getData().add(new XYChart.Data<>("Grade A (85-100)", StudentService.getGradeCount("A")));
        dataSeries.getData().add(new XYChart.Data<>("Grade B (70-84)", StudentService.getGradeCount("B")));
        dataSeries.getData().add(new XYChart.Data<>("Grade C (55-69)", StudentService.getGradeCount("C")));
        dataSeries.getData().add(new XYChart.Data<>("Grade D (40-54)", StudentService.getGradeCount("D")));
        dataSeries.getData().add(new XYChart.Data<>("Fail (<40)", StudentService.getGradeCount("Fail")));
        barChart.getData().add(dataSeries);
        analyticsBox.getChildren().addAll(chartTitle, barChart);

        tableContainerBox = new VBox(12);
        tableContainerBox.setPadding(new Insets(20));
        tableContainerBox.setVisible(false); 
        
        tableTitle = new Label("Search Filters Results: Connected Live Data Matrix");
        
        resultsTable = new TableView<>();
        resultsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        resultsTable.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cbd5e1; -fx-border-radius: 4;");
        String hiddenLinesCss = ".table-row-cell:empty { -fx-background-color: transparent; -fx-border-color: transparent; } .table-row-cell:empty .table-cell { -fx-border-color: transparent; }";
        resultsTable.getStylesheets().add("data:text/css," + hiddenLinesCss);

        TableColumn<StudentReportModel, String> colId = new TableColumn<>("Student ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        colId.setMinWidth(100);

        TableColumn<StudentReportModel, String> colName = new TableColumn<>("Student Name");
        colName.setCellValueFactory(new PropertyValueFactory<>("fullName"));

        TableColumn<StudentReportModel, String> colClass = new TableColumn<>("Class Year");
        colClass.setCellValueFactory(new PropertyValueFactory<>("classYear"));

        TableColumn<StudentReportModel, String> colAvg = new TableColumn<>("Average Score");
        colAvg.setCellValueFactory(new PropertyValueFactory<>("average"));
        colAvg.setStyle("-fx-alignment: CENTER; -fx-font-weight: bold;");

        TableColumn<StudentReportModel, String> colGrade = new TableColumn<>("Final Grade");
        colGrade.setCellValueFactory(new PropertyValueFactory<>("grade"));
        colGrade.setStyle("-fx-alignment: CENTER; -fx-font-weight: bold; -fx-text-fill: #ea580c;");

        resultsTable.getColumns().addAll(colId, colName, colClass, colAvg, colGrade);
        tableContainerBox.getChildren().addAll(tableTitle, resultsTable);

        dynamicContainer.getChildren().addAll(analyticsBox, tableContainerBox);
        layout.getChildren().add(dynamicContainer);

        backendMasterDataList = FXCollections.observableArrayList();
        filteredResultsDataList = new FilteredList<>(backendMasterDataList, p -> true);
        resultsTable.setItems(filteredResultsDataList);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            searchFilterStringQuery = newValue.trim().toLowerCase();
            if (searchFilterStringQuery.isEmpty()) {
                tableContainerBox.setVisible(false);
                analyticsBox.setVisible(true);
            } else {
                analyticsBox.setVisible(false);
                tableContainerBox.setVisible(true);
                runSearchFilterPredicate();
            }
        });

        backgroundPollingSyncService = new ScheduledService<ObservableList<StudentReportModel>>() {
            @Override
            protected Task<ObservableList<StudentReportModel>> createTask() {
                return new Task<ObservableList<StudentReportModel>>() {
                    @Override
                    protected ObservableList<StudentReportModel> call() {
                        return StudentService.getStudentsWithMarksCombinedWithUnmarked();
                    }
                };
            }
        };
        
        backgroundPollingSyncService.setPeriod(Duration.seconds(5));
        backgroundPollingSyncService.setOnSucceeded(event -> {
            ObservableList<StudentReportModel> serverFreshData = backgroundPollingSyncService.getValue();
                    if (serverFreshData != null) {
            backendMasterDataList.setAll(serverFreshData);
            runSearchFilterPredicate();
        }
    });

    backgroundPollingSyncService.start();
    applyThemeStyles();

    itemToggleTheme.setOnAction(e -> {
        App.toggleGlobalTheme(!App.isDarkMode());
        itemToggleTheme.setText(App.isDarkMode() ? "☀️ Light Mode" : "🌙 Dark Mode");
        applyThemeStyles();
    });
}

private void runSearchFilterPredicate() {
    filteredResultsDataList.setPredicate(item -> {
        if (searchFilterStringQuery.isEmpty()) {
            return true;
        }
        if (item.getStudentId() != null && item.getStudentId().toLowerCase().contains(searchFilterStringQuery)) return true;
        if (item.getFullName() != null && item.getFullName().toLowerCase().contains(searchFilterStringQuery)) return true;
        if (item.getClassYear() != null && item.getClassYear().toLowerCase().contains(searchFilterStringQuery)) return true;
        return false;
    });
}

private void applyThemeStyles() {
    boolean dk = App.isDarkMode();
    if (dk) {
        layout.setStyle("-fx-background-color: #0f172a; -fx-padding: 20;");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #ffffff;");
        userProfileLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: 600; -fx-text-fill: #94a3b8; -fx-padding: 0 10 0 0;");
        cardRegistered.setStyle("-fx-background-color: #1e293b; -fx-background-radius: 8; -fx-border-color: #334155; -fx-border-radius: 8;");
        lblRegTitle.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 14px; -fx-font-weight: bold;");
        lblRegValue.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 28px; -fx-font-weight: bold;");
        cardPerformance.setStyle("-fx-background-color: #1e293b; -fx-background-radius: 8; -fx-border-color: #334155; -fx-border-radius: 8;");
        lblPerfTitle.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 14px; -fx-font-weight: bold;");
        lblPerfValue.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 28px; -fx-font-weight: bold;");
        schoolBrandingHeaderCard.setStyle("-fx-background-color: #1e293b; -fx-background-radius: 6; -fx-border-color: #334155; -fx-border-width: 1;");
        lblSchoolName.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #38bdf8; -fx-font-family: 'Arial';");
        lblSchoolSlogan.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #94a3b8; -fx-letter-spacing: 2px;");
        analyticsBox.setStyle("-fx-background-color: #1e293b; -fx-background-radius: 8; -fx-border-color: #334155; -fx-border-radius: 8;");
        chartTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #ffffff;");
        tableContainerBox.setStyle("-fx-background-color: #1e293b; -fx-background-radius: 8; -fx-border-color: #334155; -fx-border-radius: 8;");
        tableTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #ffffff;");
    } else {
        layout.setStyle("-fx-background-color: transparent;");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");
        userProfileLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: 600; -fx-text-fill: #475569; -fx-padding: 0 10 0 0;");
        cardRegistered.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-border-color: #e2e8f0; -fx-border-radius: 8;");
        lblRegTitle.setStyle("-fx-text-fill: #64748b; -fx-font-size: 14px; -fx-font-weight: bold;");
        lblRegValue.setStyle("-fx-text-fill: #0f172a; -fx-font-size: 28px; -fx-font-weight: bold;");
        cardPerformance.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-border-color: #e2e8f0; -fx-border-radius: 8;");
        lblPerfTitle.setStyle("-fx-text-fill: #64748b; -fx-font-size: 14px; -fx-font-weight: bold;");
        lblPerfValue.setStyle("-fx-text-fill: #0f172a; -fx-font-size: 28px; -fx-font-weight: bold;");
        boxStyleLight();
    }
}

private void boxStyleLight() {
    schoolBrandingHeaderCard.setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 6; -fx-border-color: #e2e8f0; -fx-border-width: 1;");
    lblSchoolName.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #1e3a8a; -fx-font-family: 'Arial';");
    lblSchoolSlogan.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #64748b; -fx-letter-spacing: 2px;");
    analyticsBox.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-border-color: #e2e8f0; -fx-border-radius: 8;");
    chartTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");
    tableContainerBox.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-border-color: #e2e8f0; -fx-border-radius: 8;");
    tableTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #ea580c;");
}

public VBox getLayout() {
    return layout;
}
}
