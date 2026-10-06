package com.dashboard.views;

import com.dashboard.App;
import com.dashboard.models.Student;
import com.dashboard.services.StudentService;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;

public class StudentTableView {

    private ScrollPane scrollWrapper;
    private VBox layout;
    private TableView<Student> table;
    private ObservableList<Student> masterDataList;
    private FilteredList<Student> filteredDataList;
    private ScheduledService<ObservableList<Student>> syncServiceLoop;
    private String currentSearchQuery = "";

    public StudentTableView() {
        scrollWrapper = new ScrollPane();
        scrollWrapper.setFitToWidth(true);
        scrollWrapper.setFitToHeight(true);
        scrollWrapper.setPannable(true);
        scrollWrapper.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollWrapper.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollWrapper.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");

        layout = new VBox(20);
        layout.setStyle("-fx-background-color: transparent;");
        layout.setPadding(new Insets(0, 10, 10, 10));

        HBox topHeaderBar = new HBox(15);
        topHeaderBar.setAlignment(Pos.CENTER_LEFT);
        
        Label titleLabel = new Label("Registered Students Masterlist");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; " + (App.isDarkMode() ? "-fx-text-fill: #ffffff;" : "-fx-text-fill: #0f172a;"));
        
        Label lblLiveIndicator = new Label("● Live Sync");
        lblLiveIndicator.setStyle("-fx-text-fill: #10b981; -fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 0 0 0 10;");
        
        HBox barSpacer = new HBox();
        HBox.setHgrow(barSpacer, Priority.ALWAYS);

        TextField searchEngineField = new TextField();
        searchEngineField.setPromptText("Search ID, Name, or Year...");
        searchEngineField.setPrefWidth(260);
        searchEngineField.setStyle("-fx-background-color: white; -fx-border-color: #cbd5e1; -fx-border-radius: 5; -fx-background-radius: 5; -fx-padding: 8; -fx-font-size: 13px;");

        topHeaderBar.getChildren().addAll(titleLabel, lblLiveIndicator, barSpacer, searchEngineField);

        table = new TableView<>();
        VBox.setVgrow(table, Priority.ALWAYS);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        applyTableTheme();
                TableColumn<Student, Number> colNumbering = new TableColumn<>("#");
        colNumbering.setMaxWidth(1000);
        colNumbering.setStyle("-fx-alignment: CENTER; -fx-font-weight: bold;");
        colNumbering.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(table.getItems().indexOf(p.getValue()) + 1));

        TableColumn<Student, String> colId = new TableColumn<>("Student ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        colId.setMaxWidth(1500);

        TableColumn<Student, String> colFirstName = new TableColumn<>("First Name");
        colFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));

        TableColumn<Student, String> colLastName = new TableColumn<>("Last Name");
        colLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        TableColumn<Student, String> colGender = new TableColumn<>("Gender");
        colGender.setCellValueFactory(new PropertyValueFactory<>("gender")); // Reads 'gender' property
        colGender.setMaxWidth(1200);

        TableColumn<Student, String> colClass = new TableColumn<>("Class Year");
        colClass.setCellValueFactory(new PropertyValueFactory<>("classYear")); // Reads 'classYear' property
        colClass.setMaxWidth(1500);

        TableColumn<Student, String> colEmail = new TableColumn<>("Email Address");
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email")); // Reads 'email' property

        TableColumn<Student, String> colPhone = new TableColumn<>("Phone Number");
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone")); // Reads 'phone' property

        TableColumn<Student, String> colAddress = new TableColumn<>("Physical Address");
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address")); // Reads 'address' property

        TableColumn<Student, Void> colActions = new TableColumn<>("Actions Matrix");
        colActions.setMinWidth(300);
        Callback<TableColumn<Student, Void>, TableCell<Student, Void>> cellFactory = param -> new TableCell<>() {
            private final Button btnMark = new Button("Mark");
            private final Button btnEdit = new Button("Edit");
            private final Button btnDelete = new Button("Delete");
            private final HBox pane = new HBox(8, btnMark, btnEdit, btnDelete);

            {
                pane.setAlignment(Pos.CENTER);
                btnMark.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 4 10; -fx-background-radius: 4; -fx-font-size: 12px;");
                btnEdit.setStyle("-fx-background-color: #0284c7; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 4 10; -fx-background-radius: 4; -fx-font-size: 12px;");
                btnDelete.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 4 10; -fx-background-radius: 4; -fx-font-size: 12px;");

                btnMark.setOnAction(e -> {
                    Student selected = getTableView().getItems().get(getIndex());
                    handleMarkAction(selected);
                });

                btnEdit.setOnAction(e -> {
                    Student selected = getTableView().getItems().get(getIndex());
                    handleEditAction(selected);
                });

                btnDelete.setOnAction(e -> {
                    Student selected = getTableView().getItems().get(getIndex());
                    handleDeleteAction(selected);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        };

        colActions.setCellFactory(cellFactory);
        table.getColumns().addAll(colNumbering, colId, colFirstName, colLastName, colGender, colClass, colEmail, colPhone, colAddress, colActions);

        masterDataList = FXCollections.observableArrayList();
        filteredDataList = new FilteredList<>(masterDataList, p -> true);
        table.setItems(filteredDataList);

        table.setRowFactory(tv -> {
            TableRow<Student> row = new TableRow<>();
            ContextMenu selectionMenu = new ContextMenu();
            
            MenuItem copyIdItem = new MenuItem("📋 Copy Student ID");
            copyIdItem.setOnAction(event -> {
                Student item = row.getItem();
                if (item != null) {
                    javafx.scene.input.ClipboardContent content = new javafx.scene.input.ClipboardContent();
                    content.putString(item.getStudentId());
                    javafx.scene.input.Clipboard.getSystemClipboard().setContent(content);
                }
            });

            MenuItem copyNameItem = new MenuItem("📋 Copy Full Name");
            copyNameItem.setOnAction(event -> {
                Student item = row.getItem();
                if (item != null) {
                    javafx.scene.input.ClipboardContent content = new javafx.scene.input.ClipboardContent();
                    content.putString(item.getFirstName() + " " + item.getLastName());
                    javafx.scene.input.Clipboard.getSystemClipboard().setContent(content);
                }
            });

            MenuItem copyEmailItem = new MenuItem("📋 Copy Email Address");
            copyEmailItem.setOnAction(event -> {
                Student item = row.getItem();
                if (item != null) {
                    javafx.scene.input.ClipboardContent content = new javafx.scene.input.ClipboardContent();
                    content.putString(item.getEmail());
                    javafx.scene.input.Clipboard.getSystemClipboard().setContent(content);
                }
            });

            MenuItem copyPhoneItem = new MenuItem("📋 Copy Phone Number");
            copyPhoneItem.setOnAction(event -> {
                Student item = row.getItem();
                if (item != null) {
                    javafx.scene.input.ClipboardContent content = new javafx.scene.input.ClipboardContent();
                    content.putString(item.getPhone());
                    javafx.scene.input.Clipboard.getSystemClipboard().setContent(content);
                }
            });

            selectionMenu.getItems().addAll(copyIdItem, copyNameItem, copyEmailItem, copyPhoneItem);

            row.emptyProperty().addListener((obs, wasEmpty, isEmpty) -> {
                if (isEmpty) {
                    row.setContextMenu(null);
                } else {
                    row.setContextMenu(selectionMenu);
                }
            });
            return row;
        });

        searchEngineField.textProperty().addListener((observable, oldValue, newValue) -> {
            currentSearchQuery = newValue.trim().toLowerCase();
            runFilterPredicate();
        });
        // FIXED LOGIC LINE: Hard-assigned exact matching nested ScheduledService explicit parameters
        syncServiceLoop = new ScheduledService<ObservableList<Student>>() {
    @Override
    protected Task<ObservableList<Student>> createTask() {
        return new Task<ObservableList<Student>>() {
            @Override
            protected ObservableList<Student> call() {
                return StudentService.getAllStudents();
            }
        };
    }
};

syncServiceLoop.setPeriod(Duration.seconds(5));

syncServiceLoop.setOnSucceeded(event -> {
    ObservableList<Student> activeFreshData = syncServiceLoop.getValue();
    if (activeFreshData != null) {
        int selectedIndex = table.getSelectionModel().getSelectedIndex();
        masterDataList.setAll(activeFreshData);
        runFilterPredicate();
        table.refresh();
        
        if (selectedIndex >= 0 && selectedIndex < table.getItems().size()) {
            table.getSelectionModel().select(selectedIndex);
        }
    }
});

syncServiceLoop.start();
layout.getChildren().addAll(topHeaderBar, table);
scrollWrapper.setContent(layout);
}

private void runFilterPredicate() {
    filteredDataList.setPredicate(student -> {
        if (currentSearchQuery.isEmpty()) {
            return true;
        }
        if (student.getStudentId() != null && student.getStudentId().toLowerCase().contains(currentSearchQuery)) return true;
        if (student.getFirstName() != null && student.getFirstName().toLowerCase().contains(currentSearchQuery)) return true;
        if (student.getLastName() != null && student.getLastName().toLowerCase().contains(currentSearchQuery)) return true;
        if (student.getClassYear() != null && student.getClassYear().toLowerCase().contains(currentSearchQuery)) return true;
        return false;
    });
}

private void handleMarkAction(Student student) {
    boolean alreadyMarked = StudentService.hasStudentBeenMarked(student.getStudentId());
    
    if (alreadyMarked) {
        Alert confirmationBox = new Alert(Alert.AlertType.INFORMATION);
        confirmationBox.setTitle("Evaluation Exists");
        confirmationBox.setHeaderText("Student Already Evaluated");
        confirmationBox.setContentText("Click OK to automatically redirect to Report View panel to look up academic scripts.");
        
        if (confirmationBox.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            if (syncServiceLoop != null) syncServiceLoop.cancel();
            App.navigateToReportPageWithHighlight(student.getStudentId());
        }
    } else {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.setTitle("Insert Performance Metrics Matrix");
        
        VBox box = new VBox(15);
        box.setPadding(new Insets(25));
        box.setStyle("-fx-background-color: white;");
        
        Label lblTarget = new Label("Grading Metrics Entry Form: " + student.getFirstName() + " " + student.getLastName());
        lblTarget.setStyle("-fx-font-weight: bold; -fx-text-fill: #10b981;");
        
        GridPane grid = new GridPane();
        grid.setHgap(15); 
        grid.setVgap(12);
        
        TextField txtJ = new TextField(); 
        TextField txtD = new TextField();
        TextField txtW = new TextField(); 
        TextField txtE = new TextField();
        TextField txtL = new TextField();
        
        grid.add(new Label("Java Programming:"), 0, 0);     grid.add(txtJ, 1, 0);
        grid.add(new Label("Database Management:"), 0, 1);  grid.add(txtD, 1, 1);
        grid.add(new Label("Web Design:"), 0, 2);           grid.add(txtW, 1, 2);
        grid.add(new Label("Entrepreneurship:"), 0, 3);     grid.add(txtE, 1, 3);
        grid.add(new Label("Language Course:"), 0, 4);       grid.add(txtL, 1, 4);
        
        Button btnSubmit = new Button("Calculate & Save");
        btnSubmit.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 15; -fx-background-radius: 4; -fx-cursor: hand;");
        
        btnSubmit.setOnAction(ev -> {
            try {
                double j = Double.parseDouble(txtJ.getText().trim());
                double d = Double.parseDouble(txtD.getText().trim());
                double w = Double.parseDouble(txtW.getText().trim());
                double ent = Double.parseDouble(txtE.getText().trim());
                double l = Double.parseDouble(txtL.getText().trim());
                
                if (j < 0 || j > 100 || d < 0 || d > 100 || w < 0 || w > 100 || ent < 0 || ent > 100 || l < 0 || l > 100) return;
                
                double average = (j + d + w + ent + l) / 5.0;
                String grade = (average >= 85) ? "A" : (average >= 70) ? "B" : (average >= 55) ? "C" : (average >= 40) ? "D" : "Fail";
                
                boolean success = StudentService.saveStudentMarks(student.getStudentId(), j, d, w, ent, l, average, grade);
                
                if (success) {
                    String studentFullName = student.getFirstName() + " " + student.getLastName();
                    String emailBody = ""
                            + ""
                            + "🎓 REPORT CARD TRANSCRIPT RELEASE"
                            + "Dear " + studentFullName + ","
                            + "Your official academic performance grading results for this term have been compiled and published."
                            + ""
                            + "Student ID: " + student.getStudentId()
                            + "Full Name: " + studentFullName
                            + "Java Programming: " + String.format("%.1f", j) + "%"
                            + "Database Management: " + String.format("%.1f", d) + "%"
                            + "Web Architecture: " + String.format("%.1f", w) + "%"
                            + "Entrepreneurship: " + String.format("%.1f", ent) + "%"
                            + "Language Course: " + String.format("%.1f", l) + "%"
                            + "OVERALL AVERAGE: " + String.format("%.1f", average) + "%"
                            + "FINAL GRADE: " + grade
                            + ""
                            + ""
                            + "Please contact the administration office if you require a physically stamped certificate sheet."
                            + "Best regards,"
                            + "Ubumwe School Administration"
                            + ""
                            + "";
                            
                    com.dashboard.services.EmailService.sendSystemEmailAsync(student.getEmail(), "Official Academic Report Transcript Release", emailBody);
                    modal.close();
                    
                    if (syncServiceLoop != null) syncServiceLoop.cancel();
                    App.navigateToReportPageWithHighlight(student.getStudentId());
                }
            }catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        box.getChildren().addAll(lblTarget, grid, btnSubmit);
        modal.setScene(new Scene(box, 380, 320));
        modal.showAndWait();
    }
}
private void handleEditAction(Student student) {
    // FORCE LOCALHOST CHECK FOR SECURITY
    try {
        java.net.InetAddress localHost = java.net.InetAddress.getLocalHost();
        boolean isLocal = localHost.isLoopbackAddress() || localHost.getHostAddress().equals("localhost");
        
        if (!isLocal) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Access Denied");
            alert.setHeaderText("Unauthorized Database Operation");
            alert.setContentText("You are not authorized to access this accessing ");
            alert.showAndWait();
            return;
        }
    } catch (Exception e) {
        return; // Safely block if network state cannot be verified
    }

    Stage modal = new Stage();
    modal.initModality(Modality.APPLICATION_MODAL);
    modal.setTitle("Update Student Profile Details");
    
    VBox box = new VBox(15);
    box.setPadding(new Insets(25));
    box.setStyle("-fx-background-color: white;");
    
    Label lblTarget = new Label("Edit Mode: Modifying Records for ID " + student.getStudentId());
    lblTarget.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #0284c7;");
    
    GridPane grid = new GridPane();
    grid.setHgap(15); 
    grid.setVgap(12);
    
    TextField txtFName = new TextField(student.getFirstName());
    TextField txtLName = new TextField(student.getLastName());
    TextField txtEmail = new TextField(student.getEmail());
    TextField txtClass = new TextField(student.getClassYear());
    TextField txtPhone = new TextField(student.getPhone());
    TextField txtAddress = new TextField(student.getAddress());
    TextField txtGender = new TextField(student.getGender());
    
    grid.add(new Label("First Name:"), 0, 0);       grid.add(txtFName, 1, 0);
    grid.add(new Label("Last Name:"), 0, 1);        grid.add(txtLName, 1, 1);
    grid.add(new Label("Gender Identity:"), 0, 2);   grid.add(txtGender, 1, 2);
    grid.add(new Label("Class Year:"), 0, 3);        grid.add(txtClass, 1, 3);
    grid.add(new Label("Email Address:"), 0, 4);     grid.add(txtEmail, 1, 4);
    grid.add(new Label("Phone Number:"), 0, 5);      grid.add(txtPhone, 1, 5);
    grid.add(new Label("Home Address:"), 0, 6);      grid.add(txtAddress, 1, 6);
    
    HBox controls = new HBox(12);
    controls.setAlignment(Pos.CENTER_RIGHT);
    
    Button btnCancel = new Button("Cancel");
    btnCancel.setStyle("-fx-background-color: #e2e8f0; -fx-text-fill: #334155; -fx-font-weight: bold; -fx-padding: 8 15; -fx-background-radius: 4; -fx-cursor: hand;");
    btnCancel.setOnAction(ev -> modal.close());
    
    Button btnSave = new Button("Save Changes");
    btnSave.setStyle("-fx-background-color: #0284c7; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 15; -fx-background-radius: 4; -fx-cursor: hand;");
    
    btnSave.setOnAction(ev -> {
        String fName = txtFName.getText().trim();
        String lName = txtLName.getText().trim();
        String email = txtEmail.getText().trim();
        String cls = txtClass.getText().trim();
        String phone = txtPhone.getText().trim();
        String address = txtAddress.getText().trim();
        String gen = txtGender.getText().trim();
        
        if (fName.isEmpty() || email.isEmpty() || cls.isEmpty() || phone.isEmpty() || address.isEmpty() || gen.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "All mandatory fields must be completed.");
            alert.showAndWait();
            return;
        }
        
        boolean updateSuccess = StudentService.modifyStudentProfile(student.getStudentId(), fName, lName, email, cls, address, "2000-01-01", gen, phone);
        if (updateSuccess) {
            modal.close();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Profile modification failed.");
            alert.showAndWait();
        }
    });
    
    controls.getChildren().addAll(btnCancel, btnSave);
    box.getChildren().addAll(lblTarget, grid, controls);
    modal.setScene(new Scene(box, 400, 390));
    modal.showAndWait();
}
private void handleDeleteAction(Student student) {
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

    Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
    confirmation.setTitle("De-register Student");
    confirmation.setHeaderText("Soft Deleting Student Entry Record");
    confirmation.setContentText("Are you sure you want to flag student " + student.getFirstName() + " as inactive inside XAMPP database tables?");
    
    if (confirmation.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
        StudentService.deleteStudentFromDb(student.getStudentId());
    }
}
private void applyTableTheme() {
    table.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cbd5e1; -fx-border-radius: 6;");
    
    String customStyle = ".table-view .column-header { -fx-background-color: #f1f5f9; -fx-border-color: #cbd5e1; } "
            + ".table-view .column-header .label { -fx-text-fill: #1e293b; -fx-font-weight: bold; } "
            + ".table-row-cell { -fx-background-color: #ffffff !important; -fx-text-fill: #0f172a !important; } "
            + ".table-row-cell:odd { -fx-background-color: #f8fafc !important; } "
            + ".table-row-cell:hover { -fx-background-color: #e2e8f0 !important; -fx-cursor: hand; } "
            + ".table-row-cell:filled:selected { -fx-background-color: #cbd5e1 !important; } "
            + ".table-view .table-row-cell:empty { -fx-background-color: transparent; -fx-border-color: transparent; } "
            + ".table-view .table-row-cell:empty .table-cell { -fx-border-color: transparent; }";
            
    table.getStylesheets().add("data:text/css," + customStyle);
}

public ScrollPane getLayout() {
    return scrollWrapper;
}
}
