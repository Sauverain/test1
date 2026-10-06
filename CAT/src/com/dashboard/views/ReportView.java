package com.dashboard.views;

import com.dashboard.App;
import com.dashboard.models.StudentReportModel;
import com.dashboard.services.StudentService;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.print.PageLayout;
import javafx.print.PageOrientation;
import javafx.print.Paper;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import java.io.File;

public class ReportView {

    private VBox layout;
    private TableView<StudentReportModel> table;
    private ObservableList<StudentReportModel> masterDataList;
    private FilteredList<StudentReportModel> filteredDataList;

    public ReportView() {
        layout = new VBox(20);
        layout.setStyle("-fx-background-color: transparent;");

        HBox topHeaderBar = new HBox(15);
        topHeaderBar.setAlignment(Pos.CENTER_LEFT);

        Label titleLabel = new Label("Academic Performance Reports & Grading Control");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; " + (App.isDarkMode() ? "-fx-text-fill: #ffffff;" : "-fx-text-fill: #0f172a;"));

        HBox barSpacer = new HBox();
        HBox.setHgrow(barSpacer, Priority.ALWAYS);

        TextField searchEngineField = new TextField();
        searchEngineField.setPromptText("Search ID, Name, or Year...");
        searchEngineField.setPrefWidth(260);
        searchEngineField.setStyle("-fx-background-color: white; -fx-border-color: #cbd5e1; -fx-border-radius: 5; -fx-background-radius: 5; -fx-padding: 8; -fx-font-size: 13px;");

        topHeaderBar.getChildren().addAll(titleLabel, barSpacer, searchEngineField);

        table = new TableView<>();
        VBox.setVgrow(table, Priority.ALWAYS);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        applyTableTheme();

        TableColumn<StudentReportModel, Number> colNumbering = new TableColumn<>("#");
        colNumbering.setMaxWidth(1000);
        colNumbering.setStyle("-fx-alignment: CENTER; -fx-font-weight: bold;");
        colNumbering.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(table.getItems().indexOf(p.getValue()) + 1));

        TableColumn<StudentReportModel, String> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        colId.setMaxWidth(1500);

        TableColumn<StudentReportModel, String> colName = new TableColumn<>("Student Full Name");
        colName.setCellValueFactory(new PropertyValueFactory<>("fullName"));

        TableColumn<StudentReportModel, String> colClass = new TableColumn<>("Class");
        colClass.setCellValueFactory(new PropertyValueFactory<>("classYear"));
        colClass.setMaxWidth(1500);

        TableColumn<StudentReportModel, String> colAvg = new TableColumn<>("Average Score");
        colAvg.setCellValueFactory(new PropertyValueFactory<>("average"));
        colAvg.setStyle("-fx-alignment: CENTER; -fx-font-weight: bold;");

        TableColumn<StudentReportModel, String> colGrade = new TableColumn<>("Final Grade");
        colGrade.setCellValueFactory(new PropertyValueFactory<>("grade"));
        colGrade.setStyle("-fx-alignment: CENTER; -fx-font-weight: bold; -fx-text-fill: #10b981;");

        TableColumn<StudentReportModel, Void> colActions = new TableColumn<>("Performance Controls");
        colActions.setMinWidth(260);

        Callback<TableColumn<StudentReportModel, Void>, TableCell<StudentReportModel, Void>> cellFactory = param -> new TableCell<>() {
            private final Button btnTranscript = new Button("View Transcript");
            private final Button btnEdit = new Button("Modify Marks");
            private final HBox pane = new HBox(8, btnTranscript, btnEdit);

            {
                pane.setAlignment(Pos.CENTER);
                btnTranscript.setStyle("-fx-background-color: #0056b3; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 6 12; -fx-background-radius: 4; -fx-font-size: 12px;");
                btnEdit.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 6 12; -fx-background-radius: 4; -fx-font-size: 12px;");
                
                btnTranscript.setOnAction(e -> {
                    StudentReportModel selected = getTableView().getItems().get(getIndex());
                    showTranscriptCardModal(selected);
                });

                btnEdit.setOnAction(e -> {
                    StudentReportModel selected = getTableView().getItems().get(getIndex());
                    handleInlineScoreEdit(selected);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        };

        colActions.setCellFactory(cellFactory);
                table.getColumns().addAll(colNumbering, colId, colName, colClass, colAvg, colGrade, colActions);

        masterDataList = FXCollections.observableArrayList();
        masterDataList.addAll(StudentService.getStudentsWithMarks());
        filteredDataList = new FilteredList<>(masterDataList, p -> true);
        table.setItems(filteredDataList);

        // --- NEW CODE: ENABLE MOUSE RIGHT-CLICK CLICK TO SELECTION & COPY ---
        table.setRowFactory(tv -> {
            TableRow<StudentReportModel> row = new TableRow<>();
            ContextMenu selectionMenu = new ContextMenu();
            
            MenuItem copyIdItem = new MenuItem("📋 Copy Student ID");
            copyIdItem.setOnAction(event -> {
                StudentReportModel item = row.getItem();
                if (item != null) {
                    javafx.scene.input.ClipboardContent content = new javafx.scene.input.ClipboardContent();
                    content.putString(item.getStudentId());
                    javafx.scene.input.Clipboard.getSystemClipboard().setContent(content);
                }
            });

            MenuItem copyNameItem = new MenuItem("📋 Copy Full Name");
            copyNameItem.setOnAction(event -> {
                StudentReportModel item = row.getItem();
                if (item != null) {
                    javafx.scene.input.ClipboardContent content = new javafx.scene.input.ClipboardContent();
                    content.putString(item.getFullName());
                    javafx.scene.input.Clipboard.getSystemClipboard().setContent(content);
                }
            });

            MenuItem copyGradeItem = new MenuItem("📋 Copy Metrics Summary");
            copyGradeItem.setOnAction(event -> {
                StudentReportModel item = row.getItem();
                if (item != null) {
                    javafx.scene.input.ClipboardContent content = new javafx.scene.input.ClipboardContent();
                    content.putString("ID: " + item.getStudentId() + " | Name: " + item.getFullName() + " | Avg: " + item.getAverage() + "% | Grade: " + item.getGrade());
                    javafx.scene.input.Clipboard.getSystemClipboard().setContent(content);
                }
            });

            selectionMenu.getItems().addAll(copyIdItem, copyNameItem, copyGradeItem);

            row.emptyProperty().addListener((obs, wasEmpty, isEmpty) -> {
                if (isEmpty) {
                    row.setContextMenu(null);
                } else {
                    row.setContextMenu(selectionMenu);
                }
            });
            return row;
        });
        // ---------------------------------------------------------------------

        searchEngineField.textProperty().addListener((observable, oldValue, newValue) -> {
            String query = newValue.trim().toLowerCase();
            filteredDataList.setPredicate(student -> {
                if (query.isEmpty()) return true;
                if (student.getStudentId() != null && student.getStudentId().toLowerCase().contains(query)) return true;
                if (student.getFullName() != null && student.getFullName().toLowerCase().contains(query)) return true;
                if (student.getClassYear() != null && student.getClassYear().toLowerCase().contains(query)) return true;
                return false;
            });
            table.refresh();
        });

        layout.getChildren().addAll(topHeaderBar, table);
    }

    private void showTranscriptCardModal(StudentReportModel student) {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.setTitle("Official Academic Transcript - " + student.getFullName());

        ScrollPane outerScroll = new ScrollPane();
        outerScroll.setFitToWidth(true);
        outerScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        outerScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        outerScroll.setStyle("-fx-background-color: #f1f5f9; -fx-background: #f1f5f9; -fx-border-color: transparent;");

        VBox contentWrapper = new VBox(15);
        contentWrapper.setPadding(new Insets(15, 25, 15, 25));
        contentWrapper.setAlignment(Pos.TOP_CENTER);
        contentWrapper.setStyle("-fx-background-color: #f1f5f9;");

        VBox transcriptCard = new VBox(12);
        transcriptCard.setPadding(new Insets(25, 35, 25, 35));
        transcriptCard.setPrefWidth(535);
        transcriptCard.setMaxWidth(535);
        transcriptCard.setPrefHeight(720);
        transcriptCard.setMaxHeight(720);
        transcriptCard.setStyle("-fx-background-color: #ffffff; -fx-border-color: #0f172a; -fx-border-width: 2; -fx-border-radius: 4;");

        HBox topRowHeader = new HBox(20);
        topRowHeader.setAlignment(Pos.CENTER);
        topRowHeader.setStyle("-fx-border-color: #475569; -fx-border-width: 0 0 2 0; -fx-padding: 0 0 10 0;");
        
        File logoFile = new File("F:\\CAT\\src\\com\\dashboard\\assets\\logo.png");
        if (logoFile.exists()) {
            ImageView logoView = new ImageView(new Image(logoFile.toURI().toString()));
            logoView.setFitWidth(60);
            logoView.setFitHeight(60);
            logoView.setPreserveRatio(true);
            topRowHeader.getChildren().add(logoView);
        }

        VBox titleBox = new VBox(2);
        titleBox.setAlignment(Pos.CENTER);
        Label lblSchoolTitle = new Label("UBUMWE SCHOOL");
        lblSchoolTitle.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1e3a8a; -fx-font-family: 'Arial';");
        Label lblTranscriptTitle = new Label("OFFICIAL ACADEMIC TRANSCRIPT");
        lblTranscriptTitle.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #475569; -fx-letter-spacing: 1px;");
        titleBox.getChildren().addAll(lblSchoolTitle, lblTranscriptTitle);
        topRowHeader.getChildren().add(titleBox);
        transcriptCard.getChildren().add(topRowHeader);

        VBox studentSection = new VBox(5);
        Label lblOriginalHeader = new Label("STUDENT INFORMATION");
        lblOriginalHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 11px; -fx-text-fill: #1e3a8a;");
        
        GridPane infoGrid = new GridPane();
        infoGrid.setHgap(30); infoGrid.setVgap(6);
        infoGrid.setStyle("-fx-background-color: #ffffff; -fx-padding: 8 0 8 0; -fx-border-color: #e2e8f0; -fx-border-width: 1 0 1 0;");
        
        infoGrid.add(new Label("Student ID:"), 0, 0);       infoGrid.add(new Label(student.getStudentId()), 1, 0);
        infoGrid.add(new Label("Full Name:"), 0, 1);        infoGrid.add(new Label(student.getFullName()), 1, 1);
        infoGrid.add(new Label("Class Year:"), 0, 2);       
infoGrid.add(new Label(student.getClassYear()), 1, 2);
infoGrid.add(new Label("Academic Term:"), 0, 3);    
infoGrid.add(new Label("2025 - 2026"), 1, 3);
infoGrid.add(new Label("Status:"), 0, 4);           
infoGrid.add(new Label("Active Verified"), 1, 4);

infoGrid.getChildren().stream()
    .filter(n -> GridPane.getColumnIndex(n) == 0)
    .forEach(n -> n.setStyle("-fx-font-weight: bold; -fx-text-fill: #475569; -fx-pref-width: 130;"));

studentSection.getChildren().addAll(lblOriginalHeader, infoGrid);
transcriptCard.getChildren().add(studentSection);

VBox resultsSection = new VBox(5);

Label lblResHeader = new Label("ACADEMIC RESULTS MATRIX");
lblResHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 11px; -fx-text-fill: #1e3a8a;");

GridPane resultsGrid = new GridPane();
resultsGrid.setHgap(35); 
resultsGrid.setVgap(8);
resultsGrid.setStyle("-fx-padding: 2 0 2 0;");

Label thNo = new Label("No"); 
Label thSub = new Label("Subject Course Module");
Label thMarks = new Label("Marks (%)"); 
Label thGrade = new Label("Grade");

String thStyle = "-fx-font-weight: bold; -fx-text-fill: #0f172a; -fx-border-color: #475569; -fx-border-width: 0 0 2 0; -fx-padding: 0 0 4 0;";
thNo.setStyle(thStyle); 
thSub.setStyle(thStyle); 
thMarks.setStyle(thStyle); 
thGrade.setStyle(thStyle);

resultsGrid.add(thNo, 0, 0); 
resultsGrid.add(thSub, 1, 0); 
resultsGrid.add(thMarks, 2, 0); 
resultsGrid.add(thGrade, 3, 0);

double j = safeParse(student.getJava());
double d = safeParse(student.getDbms());
double w = safeParse(student.getWeb());
double eScore = safeParse(student.getEnt());
double l = safeParse(student.getLang());
double sum = j + d + w + eScore + l;

addResultRow(resultsGrid, 1, "Java Software Programming", j);
addResultRow(resultsGrid, 2, "Database Management (DBMS)", d);
addResultRow(resultsGrid, 3, "Web Design Architecture", w);
addResultRow(resultsGrid, 4, "Entrepreneurship Skills", eScore);
addResultRow(resultsGrid, 5, "Language Course Module", l);

resultsSection.getChildren().addAll(lblResHeader, resultsGrid);
transcriptCard.getChildren().add(resultsSection);

VBox summarySection = new VBox(5);

Label lblSumHeader = new Label("ACADEMIC SUMMARY");
lblSumHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 11px; -fx-text-fill: #1e3a8a;");

GridPane summaryGrid = new GridPane();
summaryGrid.setHgap(30); 
summaryGrid.setVgap(6);
summaryGrid.setStyle("-fx-background-color: #ffffff; -fx-padding: 8 0 8 0; -fx-border-color: #cbd5e1; -fx-border-width: 2 0 2 0;");

summaryGrid.add(new Label("Total Subjects:"), 0, 0);     
summaryGrid.add(new Label("5"), 1, 0);
summaryGrid.add(new Label("Total Marks Score:"), 0, 1);  
summaryGrid.add(new Label(String.format("%.1f", sum)), 1, 1);
summaryGrid.add(new Label("Overall Average:"), 2, 0);     
summaryGrid.add(new Label(formatValue(student.getAverage()) + "%"), 3, 0);
summaryGrid.add(new Label("Overall Grade:"), 2, 1);       
summaryGrid.add(new Label(student.getGrade()), 3, 1);

summaryGrid.getChildren().stream()
    .filter(n -> GridPane.getColumnIndex(n) % 2 == 0)
    .forEach(n -> n.setStyle("-fx-font-weight: bold; -fx-text-fill: #0f172a;"));

summaryGrid.getChildren().stream()
    .filter(n -> GridPane.getColumnIndex(n) == 3)
    .forEach(n -> n.setStyle("-fx-font-weight: bold; -fx-text-fill: #10b981;"));

summarySection.getChildren().addAll(lblSumHeader, summaryGrid);
transcriptCard.getChildren().add(summarySection);

HBox authorizationBlockRow = new HBox(20);
authorizationBlockRow.setAlignment(Pos.CENTER_RIGHT);
authorizationBlockRow.setPadding(new Insets(10, 0, 0, 0));

VBox qrPatternPane = new VBox();
qrPatternPane.setAlignment(Pos.CENTER);

StackPane qrStack = new StackPane();
Rectangle qrOutline = new Rectangle(50, 50);
qrOutline.setFill(Color.WHITE); 
qrOutline.setStroke(Color.web("#475569")); 
qrOutline.setStrokeWidth(1);

GridPane mockQrGrid = new GridPane();
mockQrGrid.setAlignment(Pos.CENTER); 
mockQrGrid.setHgap(2); 
mockQrGrid.setVgap(2);

for (int r = 0; r < 5; r++) {
    for (int c = 0; c < 5; c++) {
        Rectangle block = new Rectangle(6, 6);
        block.setFill((r + c) % 2 == 0 ? Color.web("#0f172a") : Color.WHITE);
        mockQrGrid.add(block, c, r);
    }
}

qrStack.getChildren().addAll(qrOutline, mockQrGrid);

Label lblQrDesc = new Label("VERIFIED SCRIPT"); 
lblQrDesc.setStyle("-fx-font-size: 8px; -fx-font-weight: bold; -fx-text-fill: #94a3b8;");

qrPatternPane.getChildren().addAll(qrStack, lblQrDesc);

VBox signatureBox = new VBox(2);
signatureBox.setAlignment(Pos.CENTER_RIGHT);

Label lblSigTitle = new Label("DIGITAL SIGNATURE");
lblSigTitle.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: #94a3b8;");

Label lblSigLine = new Label("_______________________");
lblSigLine.setStyle("-fx-text-fill: #475569;");

Label lblSigRole = new Label("Head Teacher  ");
lblSigRole.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #475569;");

signatureBox.getChildren().addAll(lblSigTitle, lblSigLine, lblSigRole);

HBox authSpacer = new HBox(); 
HBox.setHgrow(authSpacer, Priority.ALWAYS);

authorizationBlockRow.getChildren().addAll(qrPatternPane, authSpacer, signatureBox);
transcriptCard.getChildren().add(authorizationBlockRow);

VBox footerDisclaimerBox = new VBox(2);
footerDisclaimerBox.setAlignment(Pos.CENTER);
footerDisclaimerBox.setPadding(new Insets(6));
footerDisclaimerBox.setStyle("-fx-border-color: #cbd5e1; -fx-border-style: dashed; -fx-border-width: 1; -fx-background-color: #fafafa;");

Label lblDisclaimerText = new Label("This transcript is electronically generated and does not require");
Label lblDisclaimerText2 = new Label("a physical signature when verified through the school system.");

String discStyle = "-fx-font-size: 10px; -fx-text-fill: #64748b; -fx-font-style: italic;";
lblDisclaimerText.setStyle(discStyle); 
lblDisclaimerText2.setStyle(discStyle);

footerDisclaimerBox.getChildren().addAll(lblDisclaimerText, lblDisclaimerText2);
transcriptCard.getChildren().add(footerDisclaimerBox);

HBox actionsRowRow = new HBox(12);
actionsRowRow.setAlignment(Pos.CENTER);
actionsRowRow.setMaxWidth(535);

Button btnDownload = new Button("Download Document");
btnDownload.setStyle("-fx-background-color: #0284c7; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 5; -fx-cursor: hand;");

btnDownload.setOnAction(evt -> {
    try {
        String cleanFileName = student.getFullName().replaceAll("[\\/:*?\"<>|\\s]", "") + "_" + student.getStudentId() + ".pdf";
        String userHome = System.getProperty("user.home");
        File downloadDir = new File(userHome, "Downloads");
        if (!downloadDir.exists()) downloadDir.mkdirs();
        File targetFile = new File(downloadDir, cleanFileName);
        
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null) {
            Printer defaultPrinter = Printer.getDefaultPrinter();
            PageLayout layoutA4 = defaultPrinter.createPageLayout(Paper.A4, PageOrientation.PORTRAIT, Printer.MarginType.HARDWARE_MINIMUM);
            job.getJobSettings().setPageLayout(layoutA4);
            
            boolean ok = job.printPage(transcriptCard);
            if (ok) {
                job.endJob();
                Alert successBox = new Alert(Alert.AlertType.INFORMATION, "Transcript processed and exported to default Downloads directory:\n" + targetFile.getAbsolutePath());
                successBox.setHeaderText("Download Completed");
                successBox.showAndWait();
            }
        }
    } catch (Exception ex) {
        new Alert(Alert.AlertType.ERROR, "PDF Driver generation failure: " + ex.getMessage()).showAndWait();
    }
});
Button btnPrint = new Button("Print Transcript");
btnPrint.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 5; -fx-cursor: hand;");

btnPrint.setOnAction(evt -> {
    PrinterJob job = PrinterJob.createPrinterJob();
    if (job != null && job.showPrintDialog(modal)) {
        Printer activePrinter = job.getPrinter();
        PageLayout layoutA4 = activePrinter.createPageLayout(Paper.A4, PageOrientation.PORTRAIT, Printer.MarginType.DEFAULT);
        job.getJobSettings().setPageLayout(layoutA4);
        
        boolean success = job.printPage(transcriptCard);
        if (success) job.endJob();
    }
});

Button btnClose = new Button("Back to List");
btnClose.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 5; -fx-cursor: hand;");
btnClose.setOnAction(evt -> modal.close());

actionsRowRow.getChildren().addAll(btnDownload, btnClose);
contentWrapper.getChildren().addAll(transcriptCard, actionsRowRow);
outerScroll.setContent(contentWrapper);

modal.setScene(new Scene(outerScroll, 610, 680));
modal.showAndWait();
}

private void addResultRow(GridPane grid, int rowIdx, String subjectName, double score) {
    String grade = (score >= 85) ? "A" : (score >= 70) ? "B" : (score >= 55) ? "C" : (score >= 40) ? "D" : "Fail";
    
    Label lblNo = new Label(String.valueOf(rowIdx));
    Label lblSub = new Label(subjectName);
    Label lblScore = new Label(String.format("%.1f", score));
    Label lblGrade = new Label(grade);
    
    String rowStyle = "-fx-background-color: transparent; -fx-padding: 4 0 4 0;";
    lblNo.setStyle(rowStyle + " -fx-alignment: CENTER_LEFT;");
    lblSub.setStyle(rowStyle);
    lblScore.setStyle(rowStyle + " -fx-alignment: CENTER_LEFT; -fx-font-weight: bold;");
    lblGrade.setStyle(rowStyle + " -fx-alignment: CENTER_LEFT; -fx-font-weight: bold; -fx-text-fill: #1e3a8a;");
    
    grid.add(lblNo, 0, rowIdx);
    grid.add(lblSub, 1, rowIdx);
    grid.add(lblScore, 2, rowIdx);
    grid.add(lblGrade, 3, rowIdx);
}
    private void handleInlineScoreEdit(StudentReportModel student) {
        // FORCE LOCALHOST CHECK FOR SECURITY
        try {
            java.net.InetAddress localHost = java.net.InetAddress.getLocalHost();
            boolean isLocal = localHost.isLoopbackAddress() || localHost.getHostAddress().equals("127.0.0.1");
            
            if (!isLocal) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Access Denied");
                alert.setHeaderText("Unauthorized Database Operation");
                alert.setContentText("You are not authorized to access this accessing");
                alert.showAndWait();
                return;
            }
        } catch (Exception e) {
            return; // Safely block if network state cannot be verified
        }

        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.setTitle("UPDATE STUDENT MARKS");

        VBox box = new VBox(15);
        box.setPadding(new Insets(25));
        box.setStyle("-fx-background-color: white;");

        Label lblTarget = new Label("Edit Grading Marks Script for: " + student.getFullName());
        lblTarget.setStyle("-fx-font-weight: bold; -fx-text-fill: #0284c7;");

        GridPane grid = new GridPane();
        grid.setHgap(15); grid.setVgap(12);

        TextField txtResultJava = new TextField(formatValue(student.getJava()));
        TextField txtResultDbms = new TextField(formatValue(student.getDbms()));
        TextField txtResultWeb = new TextField(formatValue(student.getWeb()));
        TextField txtResultEnt = new TextField(formatValue(student.getEnt()));
        TextField txtResultLang = new TextField(formatValue(student.getLang()));

        grid.add(new Label("Java Programming:"), 0, 0);     grid.add(txtResultJava, 1, 0);
        grid.add(new Label("Database Management:"), 0, 1);  grid.add(txtResultDbms, 1, 1);
        grid.add(new Label("Web Design:"), 0, 2);           grid.add(txtResultWeb, 1, 2);
        grid.add(new Label("Entrepreneurship:"), 0, 3);     grid.add(txtResultEnt, 1, 3);
        grid.add(new Label("Language Course:"), 0, 4);       grid.add(txtResultLang, 1, 4);

        Button btnSubmit = new Button("Update Database Records");
        btnSubmit.setStyle("-fx-background-color: #0284c7; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 15; -fx-background-radius: 4; -fx-cursor: hand;");

        btnSubmit.setOnAction(ev -> {
            try {
                double javaValue = Double.parseDouble(txtResultJava.getText().trim());
                double dbmsValue = Double.parseDouble(txtResultDbms.getText().trim());
                double webValue = Double.parseDouble(txtResultWeb.getText().trim());
                double entValue = Double.parseDouble(txtResultEnt.getText().trim());
                double langValue = Double.parseDouble(txtResultLang.getText().trim());

                if (javaValue < 0 || javaValue > 100 || dbmsValue < 0 || dbmsValue > 100 || webValue < 0 || webValue > 100 || entValue < 0 || entValue > 100 || langValue < 0 || langValue > 100) return;

                double average = (javaValue + dbmsValue + webValue + entValue + langValue) / 5.0;
                String grade = (average >= 85) ? "A" : (average >= 70) ? "B" : (average >= 55) ? "C" : (average >= 40) ? "D" : "Fail";

                boolean success = StudentService.updateStudentMarks(student.getStudentId(), javaValue, dbmsValue, webValue, entValue, langValue, average, grade);
                if (success) {
                    String studentEmail = "Unknown";
                    String pullEmailSql = "SELECT email FROM students WHERE student_id = ?";
                    try (java.sql.Connection conn = java.sql.DriverManager.getConnection("jdbc:mysql://10.240.97:3306/school", "cat", "Kigali@1234");
                         java.sql.PreparedStatement pstmt = conn.prepareStatement(pullEmailSql)) {
                        pstmt.setString(1, student.getStudentId());
                        try (java.sql.ResultSet rs = pstmt.executeQuery()) {
                            if (rs.next()) studentEmail = rs.getString("email");
                        }
                    } catch (Exception ex) {}

                    if (!studentEmail.equals("Unknown")) {
                        String emailBody = "<html><body>"
                                + "<div style='font-family:\"Segoe UI\",Arial,sans-serif;padding:30px;background-color:#ffffff;color:#1e293b;line-height:1.6;max-width:550px;border:1px solid #cbd5e1;border-radius:8px;'>"
                                + "<h2 style='color:#0284c7;background-color:#e0f2fe;padding:12px;border-radius:6px;margin-top:0;font-size:18px;'>REVISED ACADEMIC REPORT TRANSCRIPT</h2>"
                                + "<p>Dear <strong>" + student.getFullName() + "</strong>,</p>"
                                + "<p>Your academic grading marks card profile record details have been corrected and updated by the supervisor.</p>"
                                + "<div style='background-color:#f8fafc;padding:15px;border-radius:6px;border-left:4px solid #0284c7;font-family:monospace;font-size:14px;line-height:1.8;'>"
                                + "Student ID: " + student.getStudentId() + "<br/>"
                                + "Full Name: " + student.getFullName() + "<br/>"
                                + "Java Programming: " + String.format("%.1f", javaValue) + "%<br/>"
                                + "Database Management: " + String.format("%.1f", dbmsValue) + "%<br/>"
                                + "Web Architecture: " + String.format("%.1f", webValue) + "%<br/>"
                                + "Entrepreneurship: " + String.format("%.1f", entValue) + "%<br/>"
                                + "Language Course: " + String.format("%.1f", langValue) + "%<br/>"
                                + "REVISED AVERAGE: <strong>" + String.format("%.1f", average) + "%</strong><br/>"
                                + "REVISED GRADE: <strong style='color:#0284c7;'>" + grade + "</strong>"
                                + "</div>"
                                + "<p>Please dismiss any previous scorecard printouts received for this timeline.</p>"
                                + "<p>Best regards,<br/><strong>Ubumwe School Administration</strong></p>"
                                + "</div>"
                                + "</body></html>";
                        
                        com.dashboard.services.EmailService.sendSystemEmailAsync(studentEmail, "AMENDED Academic Report Transcript Release", emailBody);
                    }

                    // Native popup notice verification modal trigger
                    javax.swing.JOptionPane.showMessageDialog(
                        null, 
                        "Student academic marks updated successfully!\nAmended report card notification sent to " + studentEmail, 
                        "Records Modified", 
                        javax.swing.JOptionPane.INFORMATION_MESSAGE
                    );

                    modal.close();
                    masterDataList.clear();
                    masterDataList.addAll(StudentService.getStudentsWithMarks());
                    table.refresh();
                }
            } catch (Exception ex) {}
        });

        box.getChildren().addAll(lblTarget, grid, btnSubmit);
        modal.setScene(new Scene(box, 380, 320));
        modal.showAndWait();
    }
private double safeParse(String scoreStr) {
    if (scoreStr == null || scoreStr.trim().isEmpty()) return 0.0;
    try { 
        return Double.parseDouble(scoreStr.trim()); 
    } catch (Exception e) { 
        return 0.0; 
    }
}

private String formatValue(String scoreStr) {
    if (scoreStr == null || scoreStr.trim().isEmpty()) return "0.0";
    try { 
        return String.format("%.1f", Double.parseDouble(scoreStr.trim())); 
    } catch (Exception e) { 
        return "0.0"; 
    }
}

public void focusAndHighlightStudentRow(String studentId) {
    if (table == null || studentId == null) return;
    for (int i = 0; i < table.getItems().size(); i++) {
        StudentReportModel row = table.getItems().get(i);
        if (row.getStudentId() != null && row.getStudentId().equals(studentId)) {
            table.getSelectionModel().select(i);
            table.scrollTo(i);
            break;
        }
    }
}

    private void applyTableTheme() {
        table.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cbd5e1; -fx-border-radius: 6;");
        String customStyle = ".table-view .column-header { -fx-background-color: #f1f5f9; -fx-border-color: #cbd5e1; } " +
                             ".table-view .column-header .label { -fx-text-fill: #1e293b; -fx-font-weight: bold; } " +
                             ".table-row-cell { -fx-background-color: #ffffff !important; -fx-text-fill: #0f172a !important; } " +
                             ".table-row-cell:odd { -fx-background-color: #f8fafc !important; } " +
                             ".table-row-cell:hover { -fx-background-color: #e2e8f0 !important; -fx-cursor: hand; } " +
                             ".table-row-cell:filled:selected { -fx-background-color: #cbd5e1 !important; } " +
                             ".table-row-cell:empty { -fx-background-color: #ffffff !important; -fx-border-color: transparent; } " +
                             ".table-row-cell:empty .table-cell { -fx-border-color: transparent; }";
        table.getStylesheets().add("data:text/css," + customStyle);
    }
public VBox getLayout() {
    return layout;
}
}