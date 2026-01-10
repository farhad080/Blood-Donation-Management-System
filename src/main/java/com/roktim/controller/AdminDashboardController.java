package com.roktim.controller;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import com.roktim.dao.DonationDAO;
import com.roktim.dao.InventoryDAO;
import com.roktim.dao.RequestDAO;
import com.roktim.dao.UserDAO;
import com.roktim.model.BloodGroup;
import com.roktim.model.Donation;
import com.roktim.model.Inventory;
import com.roktim.model.Request;
import com.roktim.model.User;
import com.roktim.model.UserRole;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;

public class AdminDashboardController implements Initializable {

    private final UserDAO userDAO = new UserDAO();
    private final InventoryDAO inventoryDAO = new InventoryDAO();
    private final RequestDAO requestDAO = new RequestDAO();
    private final DonationDAO donationDAO = new DonationDAO();

    @FXML private ToggleButton navDashboard;
    @FXML private ToggleButton navDonors;
    @FXML private ToggleButton navInventory;
    @FXML private ToggleButton navDonations;
    @FXML private ToggleButton navEmergency;
    @FXML private ToggleGroup navGroup;

    @FXML private TabPane mainTabPane;
    @FXML private Tab dashboardTab;
    @FXML private Tab donorsTab;
    @FXML private Tab inventoryTab;
    @FXML private Tab donationsTab;
    @FXML private Tab emergencyTab;

    @FXML private Label totalUsersLabel;
    @FXML private Label totalDonorsLabel;
    @FXML private Label totalUnitsLabel;
    @FXML private Label pendingRequestsLabel;

    @FXML private TableView<Request> dashboardEmergencyTable;
    @FXML private TableView<User> donorTable;
    @FXML private TableView<Inventory> inventoryTable;
    @FXML private TableView<Donation> donationTable;
    @FXML private TableView<Request> emergencyTable;

    @FXML private ComboBox<String> donorBloodGroupFilter;
    @FXML private TextField donorLocationField;
    @FXML private ComboBox<String> emergencyGroupFilter;
    @FXML private TextField emergencyLocationField;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        selectTab(dashboardTab, navDashboard);
        populateBloodGroups();
        setupTables();
        loadDashboardMetrics();
        loadTables();
    }

    private void populateBloodGroups() {
        if (donorBloodGroupFilter != null) {
            donorBloodGroupFilter.getItems().setAll(
                "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"
            );
        }
        if (emergencyGroupFilter != null) {
            emergencyGroupFilter.getItems().setAll(
                "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"
            );
        }
    }

    @FXML
    private void showDashboard() {
        selectTab(dashboardTab, navDashboard);
    }

    @FXML
    private void showDonors() {
        selectTab(donorsTab, navDonors);
    }

    @FXML
    private void showInventory() {
        selectTab(inventoryTab, navInventory);
    }

    @FXML
    private void showDonations() {
        selectTab(donationsTab, navDonations);
    }

    @FXML
    private void showEmergency() {
        selectTab(emergencyTab, navEmergency);
    }

    @FXML
    private void handleLogout() {
        // TODO: wire to existing logout/navigation logic
    }

    @FXML
    private void handleSearchDonors() {
        String bloodGroup = donorBloodGroupFilter != null ? donorBloodGroupFilter.getValue() : null;
        String location = donorLocationField != null ? donorLocationField.getText() : null;
        
        List<User> searchResults = userDAO.searchDonors(bloodGroup, location);
        if (donorTable != null) {
            donorTable.getItems().setAll(searchResults);
        }
    }

    @FXML
    private void handleResetDonorFilters() {
        if (donorBloodGroupFilter != null) {
            donorBloodGroupFilter.getSelectionModel().clearSelection();
        }
        if (donorLocationField != null) {
            donorLocationField.clear();
        }
        loadTables();
    }

    @FXML
    private void handleAddDonor() {
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Add New Donor");
        dialog.setHeaderText("Enter donor details");

        // Create the dialog content
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        javafx.scene.control.TextField nameField = new javafx.scene.control.TextField();
        nameField.setPromptText("Full Name");
        
        ComboBox<String> bloodGroupCombo = new ComboBox<>();
        bloodGroupCombo.setItems(javafx.collections.FXCollections.observableArrayList(
            "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"
        ));
        bloodGroupCombo.setPromptText("Select Blood Group");
        
        javafx.scene.control.TextField phoneField = new javafx.scene.control.TextField();
        phoneField.setPromptText("Phone Number");
        
        javafx.scene.control.TextField emailField = new javafx.scene.control.TextField();
        emailField.setPromptText("Email Address");
        
        javafx.scene.control.TextField addressField = new javafx.scene.control.TextField();
        addressField.setPromptText("Address / City");

        DatePicker lastDonationPicker = new DatePicker();
        lastDonationPicker.setPromptText("Select Date");

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Blood Group:"), 0, 1);
        grid.add(bloodGroupCombo, 1, 1);
        grid.add(new Label("Phone:"), 0, 2);
        grid.add(phoneField, 1, 2);
        grid.add(new Label("Email:"), 0, 3);
        grid.add(emailField, 1, 3);
        grid.add(new Label("Address:"), 0, 4);
        grid.add(addressField, 1, 4);
        grid.add(new Label("Last Donation:"), 0, 5);
        grid.add(lastDonationPicker, 1, 5);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                String name = nameField.getText().trim();
                String bloodGroup = bloodGroupCombo.getValue();
                String phone = phoneField.getText().trim();
                String email = emailField.getText().trim();
                String address = addressField.getText().trim();

                if (name.isEmpty() || bloodGroup == null || phone.isEmpty() || 
                    email.isEmpty() || address.isEmpty()) {
                    showAlert(Alert.AlertType.WARNING, "Incomplete Form", "Please fill in all fields.");
                    return null;
                }

                if (userDAO.emailExists(email)) {
                    showAlert(Alert.AlertType.WARNING, "Email Exists", "This email is already registered.");
                    return null;
                }

                // Auto-generate username from email
                String username = email.substring(0, email.indexOf('@'));
                int counter = 1;
                String originalUsername = username;
                while (userDAO.usernameExists(username)) {
                    username = originalUsername + counter;
                    counter++;
                }

                // Generate a default password
                String password = generateDefaultPassword();

                User newDonor = new User();
                newDonor.setName(name);
                newDonor.setBloodGroup(BloodGroup.fromString(bloodGroup));
                newDonor.setPhone(phone);
                newDonor.setEmail(email);
                newDonor.setLastDonation(lastDonationPicker.getValue());
                newDonor.setAddress(address);
                newDonor.setUsername(username);
                newDonor.setPassword(password);
                newDonor.setRole(UserRole.USER);
                newDonor.setProfileImage("");

                return newDonor;
            }
            return null;
        });

        Optional<User> result = dialog.showAndWait();
        if (result.isPresent()) {
            User newDonor = result.get();
            if (newDonor != null && userDAO.registerUser(newDonor)) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Donor added successfully.");
                loadTables();
                handleResetDonorFilters();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to add donor.");
            }
        }
    }

    @FXML
    private void handleEditDonor() {
        if (donorTable == null) return;
        User selectedDonor = donorTable.getSelectionModel().getSelectedItem();
        if (selectedDonor == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a donor to edit.");
            return;
        }
        
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Edit Donor");
        dialog.setHeaderText("Update donor details");

        // Create the dialog content
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        javafx.scene.control.TextField nameField = new javafx.scene.control.TextField();
        nameField.setText(selectedDonor.getName());
        
        ComboBox<String> bloodGroupCombo = new ComboBox<>();
        bloodGroupCombo.setItems(javafx.collections.FXCollections.observableArrayList(
            "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"
        ));
        bloodGroupCombo.setValue(selectedDonor.getBloodGroup() != null ? 
            selectedDonor.getBloodGroup().getDisplayName() : null);
        
        javafx.scene.control.TextField phoneField = new javafx.scene.control.TextField();
        phoneField.setText(selectedDonor.getPhone());
        
        javafx.scene.control.TextField emailField = new javafx.scene.control.TextField();
        emailField.setText(selectedDonor.getEmail());
        
        javafx.scene.control.TextField addressField = new javafx.scene.control.TextField();
        addressField.setText(selectedDonor.getAddress());
        
        DatePicker lastDonationPicker = new DatePicker();
        lastDonationPicker.setValue(selectedDonor.getLastDonation());

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Blood Group:"), 0, 1);
        grid.add(bloodGroupCombo, 1, 1);
        grid.add(new Label("Phone:"), 0, 2);
        grid.add(phoneField, 1, 2);
        grid.add(new Label("Email:"), 0, 3);
        grid.add(emailField, 1, 3);
        grid.add(new Label("Address:"), 0, 4);
        grid.add(addressField, 1, 4);
        grid.add(new Label("Last Donation:"), 0, 5);
        grid.add(lastDonationPicker, 1, 5);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                String name = nameField.getText().trim();
                String bloodGroup = bloodGroupCombo.getValue();
                String phone = phoneField.getText().trim();
                String email = emailField.getText().trim();
                String address = addressField.getText().trim();

                if (name.isEmpty() || bloodGroup == null || phone.isEmpty() || 
                    email.isEmpty() || address.isEmpty()) {
                    showAlert(Alert.AlertType.WARNING, "Incomplete Form", "Please fill in all fields.");
                    return null;
                }

                selectedDonor.setLastDonation(lastDonationPicker.getValue());
                // Check if email changed and if new email already exists
                if (!email.equals(selectedDonor.getEmail()) && userDAO.emailExists(email)) {
                    showAlert(Alert.AlertType.WARNING, "Email Exists", "This email is already registered.");
                    return null;
                }

                selectedDonor.setName(name);
                selectedDonor.setBloodGroup(BloodGroup.fromString(bloodGroup));
                selectedDonor.setPhone(phone);
                selectedDonor.setEmail(email);
                selectedDonor.setAddress(address);

                return selectedDonor;
            }
            return null;
        });

        Optional<User> result = dialog.showAndWait();
        if (result.isPresent()) {
            User updatedDonor = result.get();
            if (updatedDonor != null && userDAO.updateUser(updatedDonor)) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Donor updated successfully.");
                loadTables();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update donor.");
            }
        }
    }

    @FXML
    private void handleDeleteDonor() {
        if (donorTable == null) return;
        User selectedDonor = donorTable.getSelectionModel().getSelectedItem();
        if (selectedDonor == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a donor to delete.");
            return;
        }
        
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setHeaderText("Delete Donor");
        confirmAlert.setContentText("Are you sure you want to delete " + selectedDonor.getName() + "?");
        
        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (userDAO.deleteUser(selectedDonor.getId())) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Donor deleted successfully.");
                loadTables();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete donor.");
            }
        }
    }

    @FXML
    private void handleAddUnits() {
        Dialog<Inventory> dialog = new Dialog<>();
        dialog.setTitle("Add Blood Units");
        dialog.setHeaderText("Add units to blood inventory");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        ComboBox<String> bloodGroupCombo = new ComboBox<>();
        bloodGroupCombo.setItems(javafx.collections.FXCollections.observableArrayList(
            "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"
        ));
        bloodGroupCombo.setPromptText("Select Blood Group");

        javafx.scene.control.TextField unitsField = new javafx.scene.control.TextField();
        unitsField.setPromptText("Units to Add");

        grid.add(new Label("Blood Group:"), 0, 0);
        grid.add(bloodGroupCombo, 1, 0);
        grid.add(new Label("Units:"), 0, 1);
        grid.add(unitsField, 1, 1);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                String bloodGroup = bloodGroupCombo.getValue();
                String unitsText = unitsField.getText().trim();

                if (bloodGroup == null || unitsText.isEmpty()) {
                    showAlert(Alert.AlertType.WARNING, "Incomplete Form", "Please fill in all fields.");
                    return null;
                }

                try {
                    int units = Integer.parseInt(unitsText);
                    if (units <= 0) {
                        showAlert(Alert.AlertType.WARNING, "Invalid Input", "Units must be a positive number.");
                        return null;
                    }

                    BloodGroup bg = BloodGroup.fromString(bloodGroup);
                    if (inventoryDAO.addUnits(bg, units)) {
                        showAlert(Alert.AlertType.INFORMATION, "Success", units + " units added to " + bloodGroup + ".");
                        loadTables();
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Error", "Failed to add units.");
                    }
                } catch (NumberFormatException e) {
                    showAlert(Alert.AlertType.WARNING, "Invalid Input", "Units must be a valid number.");
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    @FXML
    private void handleUpdateUnits() {
        if (inventoryTable == null) return;
        Inventory selectedInventory = inventoryTable.getSelectionModel().getSelectedItem();
        if (selectedInventory == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a blood group to update.");
            return;
        }

        Dialog<Inventory> dialog = new Dialog<>();
        dialog.setTitle("Update Inventory");
        dialog.setHeaderText("Update blood inventory for " + selectedInventory.getBloodGroup().getDisplayName());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        javafx.scene.control.TextField unitsField = new javafx.scene.control.TextField();
        unitsField.setText(String.valueOf(selectedInventory.getUnits()));
        unitsField.setPromptText("Total Units");

        grid.add(new Label("Blood Group:"), 0, 0);
        grid.add(new Label(selectedInventory.getBloodGroup().getDisplayName()), 1, 0);
        grid.add(new Label("Units:"), 0, 1);
        grid.add(unitsField, 1, 1);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                String unitsText = unitsField.getText().trim();

                if (unitsText.isEmpty()) {
                    showAlert(Alert.AlertType.WARNING, "Incomplete Form", "Please enter units.");
                    return null;
                }

                try {
                    int units = Integer.parseInt(unitsText);
                    if (units < 0) {
                        showAlert(Alert.AlertType.WARNING, "Invalid Input", "Units cannot be negative.");
                        return null;
                    }

                    if (inventoryDAO.updateInventory(selectedInventory.getBloodGroup(), units)) {
                        showAlert(Alert.AlertType.INFORMATION, "Success", "Inventory updated successfully.");
                        loadTables();
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Error", "Failed to update inventory.");
                    }
                } catch (NumberFormatException e) {
                    showAlert(Alert.AlertType.WARNING, "Invalid Input", "Units must be a valid number.");
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    @FXML
    private void handleAddDonation() {
        // TODO: log donation and update inventory
    }

    @FXML
    private void handleEditDonation() {
        // TODO: edit selected donation
    }

    @FXML
    private void handleDeleteDonation() {
        // TODO: delete selected donation
    }

    @FXML
    private void handleSearchEmergency() {
        // TODO: search emergency requests
    }

    @FXML
    private void handleResetEmergencyFilters() {
        if (emergencyGroupFilter != null) {
            emergencyGroupFilter.getSelectionModel().clearSelection();
        }
        if (emergencyLocationField != null) {
            emergencyLocationField.clear();
        }
    }

    @FXML
    private void handleMarkFulfilled() {
        // TODO: mark selected request as fulfilled
    }

    @FXML
    private void handleFindMatchingDonors() {
        // TODO: find donors matching selected request
    }

    private void setupTables() {
        setupRequestTable(dashboardEmergencyTable);
        setupRequestTable(emergencyTable);

        if (donorTable != null) {
            donorTable.getColumns().clear();
            TableColumn<User, String> nameCol = new TableColumn<>("Name");
            nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
            TableColumn<User, String> bgCol = new TableColumn<>("Blood Group");
            bgCol.setCellValueFactory(cell -> javafx.beans.binding.Bindings.createStringBinding(
                () -> cell.getValue().getBloodGroup() != null ? cell.getValue().getBloodGroup().getDisplayName() : ""
            ));
            TableColumn<User, String> phoneCol = new TableColumn<>("Phone");
            phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
            TableColumn<User, String> emailCol = new TableColumn<>("Email");
            emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
            TableColumn<User, String> addrCol = new TableColumn<>("Address");
            addrCol.setCellValueFactory(new PropertyValueFactory<>("address"));
            TableColumn<User, String> lastDonCol = new TableColumn<>("Last Donation");
            lastDonCol.setCellValueFactory(cell -> javafx.beans.binding.Bindings.createStringBinding(() -> {
                return cell.getValue().getLastDonation() != null ? cell.getValue().getLastDonation().toString() : "Never";
            }));
            donorTable.getColumns().addAll(nameCol, bgCol, phoneCol, emailCol, addrCol, lastDonCol);
        }

        if (inventoryTable != null) {
            inventoryTable.getColumns().clear();
            TableColumn<Inventory, String> bgCol = new TableColumn<>("Blood Group");
            bgCol.setCellValueFactory(cell -> javafx.beans.binding.Bindings.createStringBinding(
                () -> cell.getValue().getBloodGroup() != null ? cell.getValue().getBloodGroup().getDisplayName() : ""
            ));
            TableColumn<Inventory, Integer> unitsCol = new TableColumn<>("Available Units");
            unitsCol.setCellValueFactory(new PropertyValueFactory<>("units"));
            TableColumn<Inventory, String> statusCol = new TableColumn<>("Status");
            statusCol.setCellValueFactory(cell -> javafx.beans.binding.Bindings.createStringBinding(
                () -> cell.getValue().getStockStatus()
            ));
            inventoryTable.getColumns().addAll(bgCol, unitsCol, statusCol);
        }

        if (donationTable != null) {
            donationTable.getColumns().clear();
            TableColumn<Donation, Integer> userIdCol = new TableColumn<>("Donor Id");
            userIdCol.setCellValueFactory(new PropertyValueFactory<>("userId"));
            TableColumn<Donation, String> bgCol = new TableColumn<>("Blood Group");
            bgCol.setCellValueFactory(cell -> javafx.beans.binding.Bindings.createStringBinding(
                () -> cell.getValue().getBloodGroup() != null ? cell.getValue().getBloodGroup().getDisplayName() : ""
            ));
            TableColumn<Donation, Integer> unitsCol = new TableColumn<>("Units Donated");
            unitsCol.setCellValueFactory(new PropertyValueFactory<>("units"));
            TableColumn<Donation, String> dateCol = new TableColumn<>("Donation Date");
            dateCol.setCellValueFactory(cell -> javafx.beans.binding.Bindings.createStringBinding(() -> {
                return cell.getValue().getDate() != null ? cell.getValue().getDate().toString() : "";
            }));
            donationTable.getColumns().addAll(userIdCol, bgCol, unitsCol, dateCol);
        }
    }

    private void setupRequestTable(TableView<Request> table) {
        if (table == null) return;
        table.getColumns().clear();
        TableColumn<Request, String> bgCol = new TableColumn<>("Blood Group");
        bgCol.setCellValueFactory(cell -> javafx.beans.binding.Bindings.createStringBinding(
            () -> cell.getValue().getBloodGroup() != null ? cell.getValue().getBloodGroup().getDisplayName() : ""
        ));
        TableColumn<Request, String> locCol = new TableColumn<>("Location");
        locCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        TableColumn<Request, String> msgCol = new TableColumn<>("Message");
        msgCol.setCellValueFactory(new PropertyValueFactory<>("message"));
        TableColumn<Request, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cell -> javafx.beans.binding.Bindings.createStringBinding(
            () -> cell.getValue().getStatus() != null ? cell.getValue().getStatus().name() : ""
        ));
        table.getColumns().addAll(bgCol, locCol, msgCol, statusCol);
    }

    private void loadDashboardMetrics() {
        totalUsersLabel.setText(String.valueOf(userDAO.countAllUsers()));
        totalDonorsLabel.setText(String.valueOf(userDAO.countDonors()));
        totalUnitsLabel.setText(String.valueOf(inventoryDAO.getTotalBloodUnits()));
        pendingRequestsLabel.setText(String.valueOf(requestDAO.getPendingRequestCount()));
    }

    private void loadTables() {
        if (donorTable != null) {
            donorTable.getItems().setAll(userDAO.getAllDonors());
        }
        if (inventoryTable != null) {
            inventoryTable.getItems().setAll(inventoryDAO.getAllInventory());
        }
        if (donationTable != null) {
            donationTable.getItems().setAll(donationDAO.getAllDonations());
        }
        if (dashboardEmergencyTable != null) {
            dashboardEmergencyTable.getItems().setAll(requestDAO.getAllRequests());
        }
        if (emergencyTable != null) {
            emergencyTable.getItems().setAll(requestDAO.getAllRequests());
        }
    }

    private void selectTab(Tab tab, ToggleButton toggle) {
        if (mainTabPane != null && tab != null) {
            mainTabPane.getSelectionModel().select(tab);
        }
        if (toggle != null) {
            toggle.setSelected(true);
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private String generateDefaultPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%";
        StringBuilder password = new StringBuilder();
        java.util.Random random = new java.util.Random();
        for (int i = 0; i < 8; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        return password.toString();
    }
}
