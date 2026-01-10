package com.roktim.controller;

import com.roktim.dao.RequestDAO;
import com.roktim.model.BloodGroup;
import com.roktim.model.Request;
import com.roktim.model.RequestStatus;
import com.roktim.model.User;
import com.roktim.service.SessionManager;
import com.roktim.util.NavigationUtil;
import com.roktim.util.ValidationUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;

public class EmergencyRequestController {

    @FXML private TextField patientNameField;
    @FXML private ComboBox<BloodGroup> bloodGroupCombo;
    @FXML private TextField locationField;
    @FXML private TextField contactField;
    @FXML private ComboBox<String> urgencyCombo;
    @FXML private TextArea messageArea;
    @FXML private Label messageLabel;
    @FXML private TableView<Request> requestTable;
    @FXML private TableColumn<Request, Integer> idColumn;
    @FXML private TableColumn<Request, String> patientColumn;
    @FXML private TableColumn<Request, String> bloodGroupColumn;
    @FXML private TableColumn<Request, String> locationColumn;
    @FXML private TableColumn<Request, String> urgencyColumn;
    @FXML private TableColumn<Request, String> statusColumn;
    @FXML private TableColumn<Request, String> dateColumn;

    private final RequestDAO requestDAO = new RequestDAO();
    private User currentUser;

    @FXML
    public void initialize() {
        currentUser = SessionManager.getInstance().getCurrentUser();

        bloodGroupCombo.getItems().addAll(BloodGroup.values());
        urgencyCombo.getItems().addAll("Low", "Medium", "High", "Critical");

        if (currentUser != null) {
            contactField.setText(currentUser.getPhone());
        }

        setupTable();
        loadRequestHistory();
    }

    private void setupTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        patientColumn.setCellValueFactory(new PropertyValueFactory<>("patientName"));
        bloodGroupColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getBloodGroup().getDisplayName()));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        urgencyColumn.setCellValueFactory(new PropertyValueFactory<>("urgencyLevel"));
        statusColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStatus().getDisplayName()));
        dateColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDate().toString()));
    }

    @FXML
    private void handleSubmit() {
        String patientName = patientNameField.getText().trim();
        BloodGroup bloodGroup = bloodGroupCombo.getValue();
        String location = locationField.getText().trim();
        String contact = contactField.getText().trim();
        String urgency = urgencyCombo.getValue();
        String message = messageArea.getText().trim();

        if (!validateInputs(patientName, bloodGroup, location, contact, urgency)) {
            return;
        }

        Request request = new Request(
                currentUser.getId(),
                patientName,
                bloodGroup,
                location,
                contact,
                message,
                urgency,
                RequestStatus.PENDING,
                LocalDate.now()
        );

        boolean success = requestDAO.createRequest(request);

        if (success) {
            showSuccess("Request submitted successfully!");
            clearForm();
            loadRequestHistory();
        } else {
            showError("Failed to submit request");
        }
    }

    @FXML
    private void handleClear() {
        clearForm();
    }

    @FXML
    private void handleRefresh() {
        loadRequestHistory();
    }

    @FXML
    private void handleBackToDashboard() {
        Stage stage = (Stage) patientNameField.getScene().getWindow();
        NavigationUtil.navigateTo(NavigationUtil.getViewPath("userDashboard.fxml"), stage);
    }

    private void loadRequestHistory() {
        if (currentUser != null) {
            List<Request> requests = requestDAO.getRequestsByUserId(currentUser.getId());
            ObservableList<Request> requestList = FXCollections.observableArrayList(requests);
            requestTable.setItems(requestList);
        }
    }

    private boolean validateInputs(String patientName, BloodGroup bloodGroup, String location,
                                   String contact, String urgency) {

        if (ValidationUtil.isEmpty(patientName)) {
            showError("Please enter patient name");
            return false;
        }

        if (bloodGroup == null) {
            showError("Please select blood group");
            return false;
        }

        if (ValidationUtil.isEmpty(location)) {
            showError("Please enter location");
            return false;
        }

        if (!ValidationUtil.isValidPhone(contact)) {
            showError(ValidationUtil.getPhoneErrorMessage());
            return false;
        }

        if (urgency == null) {
            showError("Please select urgency level");
            return false;
        }

        return true;
    }

    private void clearForm() {
        patientNameField.clear();
        bloodGroupCombo.setValue(null);
        locationField.clear();
        urgencyCombo.setValue(null);
        messageArea.clear();
        messageLabel.setVisible(false);

        if (currentUser != null) {
            contactField.setText(currentUser.getPhone());
        }
    }

    private void showError(String message) {
        messageLabel.setText("❌ " + message);
        messageLabel.setStyle("-fx-text-fill: #DC143C; -fx-background-color: #FFE5E5;");
        messageLabel.setVisible(true);
    }

    private void showSuccess(String message) {
        messageLabel.setText("✅ " + message);
        messageLabel.setStyle("-fx-text-fill: #28a745; -fx-background-color: #d4edda;");
        messageLabel.setVisible(true);
    }
}