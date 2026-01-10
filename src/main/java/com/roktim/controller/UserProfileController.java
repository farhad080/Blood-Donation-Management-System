package com.roktim.controller;

import com.roktim.dao.UserDAO;
import com.roktim.model.BloodGroup;
import com.roktim.model.User;
import com.roktim.service.SessionManager;
import com.roktim.util.ImageUtil;
import com.roktim.util.NavigationUtil;
import com.roktim.util.ValidationUtil;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.File;
import java.time.LocalDate;

public class UserProfileController {

    @FXML private ImageView profileImageView;
    @FXML private TextField nameField;
    @FXML private ComboBox<BloodGroup> bloodGroupCombo;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private TextArea addressArea;
    @FXML private DatePicker lastDonationPicker;
    @FXML private TextField usernameField;
    @FXML private Button editButton;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;
    @FXML private Button changeImageButton;
    @FXML private Label messageLabel;

    private final UserDAO userDAO = new UserDAO();
    private User currentUser;
    private File selectedImageFile;
    private boolean isEditMode = false;

    @FXML
    public void initialize() {
        bloodGroupCombo.getItems().addAll(BloodGroup.values());
        loadUserData();
    }

    private void loadUserData() {
        currentUser = SessionManager.getInstance().getCurrentUser();

        if (currentUser != null) {
            nameField.setText(currentUser.getName());
            bloodGroupCombo.setValue(currentUser.getBloodGroup());
            phoneField.setText(currentUser.getPhone());
            emailField.setText(currentUser.getEmail());
            addressArea.setText(currentUser.getAddress());
            lastDonationPicker.setValue(currentUser.getLastDonation());
            usernameField.setText(currentUser.getUsername());

            if (currentUser.getProfileImage() != null) {
                profileImageView.setImage(ImageUtil.loadImage(currentUser.getProfileImage()));
            } else {
                profileImageView.setImage(ImageUtil.getDefaultProfileImage());
            }
        }
    }

    @FXML
    private void handleEdit() {
        isEditMode = true;
        setEditMode(true);
    }

    @FXML
    private void handleSave() {
        String name = nameField.getText().trim();
        BloodGroup bloodGroup = bloodGroupCombo.getValue();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();
        String address = addressArea.getText().trim();
        LocalDate lastDonation = lastDonationPicker.getValue();

        if (!validateInputs(name, bloodGroup, phone, email, address)) {
            return;
        }

        currentUser.setName(name);
        currentUser.setBloodGroup(bloodGroup);
        currentUser.setPhone(phone);
        currentUser.setEmail(email);
        currentUser.setAddress(address);
        currentUser.setLastDonation(lastDonation);

        if (selectedImageFile != null) {
            try {
                String imagePath = ImageUtil.saveProfileImage(selectedImageFile);
                currentUser.setProfileImage(imagePath);
            } catch (Exception e) {
                System.err.println("✗ Error saving image: " + e.getMessage());
            }
        }

        boolean success = userDAO.updateUser(currentUser);

        if (success) {
            SessionManager.getInstance().setCurrentUser(currentUser);
            showSuccess("Profile updated successfully!");
            setEditMode(false);
            isEditMode = false;
            loadUserData();
        } else {
            showError("Failed to update profile");
        }
    }

    @FXML
    private void handleCancel() {
        isEditMode = false;
        setEditMode(false);
        loadUserData();
        selectedImageFile = null;
    }

    @FXML
    private void handleChangeImage() {
        Stage stage = (Stage) profileImageView.getScene().getWindow();
        File file = ImageUtil.selectImage(stage);

        if (file != null && ImageUtil.isValidImageFile(file)) {
            selectedImageFile = file;
            profileImageView.setImage(ImageUtil.loadImage(file.getAbsolutePath()));
        } else if (file != null) {
            showError("Please select a valid image file");
        }
    }

    @FXML
    private void handleBackToDashboard() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        NavigationUtil.navigateTo(NavigationUtil.getViewPath("userDashboard.fxml"), stage);
    }

    private void setEditMode(boolean editable) {
        nameField.setEditable(editable);
        bloodGroupCombo.setDisable(!editable);
        phoneField.setEditable(editable);
        emailField.setEditable(editable);
        addressArea.setEditable(editable);
        lastDonationPicker.setDisable(!editable);

        editButton.setVisible(!editable);
        saveButton.setVisible(editable);
        cancelButton.setVisible(editable);
        changeImageButton.setVisible(editable);

        messageLabel.setVisible(false);
    }

    private boolean validateInputs(String name, BloodGroup bloodGroup, String phone,
                                   String email, String address) {

        if (ValidationUtil.isEmpty(name)) {
            showError("Please enter your full name");
            return false;
        }

        if (bloodGroup == null) {
            showError("Please select a blood group");
            return false;
        }

        if (!ValidationUtil.isValidPhone(phone)) {
            showError(ValidationUtil.getPhoneErrorMessage());
            return false;
        }

        if (!ValidationUtil.isValidEmail(email)) {
            showError(ValidationUtil.getEmailErrorMessage());
            return false;
        }

        if (ValidationUtil.isEmpty(address)) {
            showError("Please enter your address");
            return false;
        }

        return true;
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