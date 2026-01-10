package com.roktim.controller;

import com.roktim.dao.UserDAO;
import com.roktim.model.BloodGroup;
import com.roktim.model.User;
import com.roktim.model.UserRole;
import com.roktim.service.AuthService;
import com.roktim.util.ImageUtil;
import com.roktim.util.NavigationUtil;
import com.roktim.util.ValidationUtil;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.File;

public class RegistrationController {

    @FXML private TextField nameField;
    @FXML private ComboBox<BloodGroup> bloodGroupCombo;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private TextArea addressArea;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Button uploadButton;
    @FXML private Label imageLabel;
    @FXML private Label errorLabel;

    private final AuthService authService = new AuthService();
    private final UserDAO userDAO = new UserDAO();
    private File selectedImageFile;

    @FXML
    public void initialize() {
        bloodGroupCombo.getItems().addAll(BloodGroup.values());
    }

    @FXML
    private void handleImageUpload() {
        Stage stage = (Stage) uploadButton.getScene().getWindow();
        File file = ImageUtil.selectImage(stage);

        if (file != null && ImageUtil.isValidImageFile(file)) {
            selectedImageFile = file;
            imageLabel.setText(file.getName());
        } else if (file != null) {
            showError("Please select a valid image file (JPG, PNG, GIF)");
        }
    }

    @FXML
    private void handleRegister() {
        String name = nameField.getText().trim();
        BloodGroup bloodGroup = bloodGroupCombo.getValue();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();
        String address = addressArea.getText().trim();
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (!validateInputs(name, bloodGroup, phone, email, address, username, password, confirmPassword)) {
            return;
        }

        if (userDAO.usernameExists(username)) {
            showError("Username already exists");
            return;
        }

        if (userDAO.emailExists(email)) {
            showError("Email already registered");
            return;
        }

        String imagePath = null;
        if (selectedImageFile != null) {
            try {
                imagePath = ImageUtil.saveProfileImage(selectedImageFile);
            } catch (Exception e) {
                System.err.println("✗ Error saving image: " + e.getMessage());
            }
        }

        User user = new User(name, bloodGroup, phone, email, address, username, password, UserRole.USER);
        user.setProfileImage(imagePath);

        boolean success = authService.register(user);

        if (success) {
            showSuccess("Registration successful! Redirecting to login...");

            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                    javafx.application.Platform.runLater(this::handleBackToLogin);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        } else {
            showError("Registration failed. Please try again.");
        }
    }

    @FXML
    private void handleBackToLogin() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        NavigationUtil.navigateTo(NavigationUtil.getViewPath("login.fxml"), stage);
    }

    private boolean validateInputs(String name, BloodGroup bloodGroup, String phone,
                                   String email, String address, String username,
                                   String password, String confirmPassword) {

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

        if (!ValidationUtil.isValidUsername(username)) {
            showError(ValidationUtil.getUsernameErrorMessage());
            return false;
        }

        if (!ValidationUtil.isValidPassword(password)) {
            showError(ValidationUtil.getPasswordErrorMessage());
            return false;
        }

        if (!ValidationUtil.doPasswordsMatch(password, confirmPassword)) {
            showError("Passwords do not match");
            return false;
        }

        return true;
    }

    private void showError(String message) {
        errorLabel.setText("❌ " + message);
        errorLabel.setStyle("-fx-text-fill: #DC143C;");
        errorLabel.setVisible(true);
    }

    private void showSuccess(String message) {
        errorLabel.setText("✅ " + message);
        errorLabel.setStyle("-fx-text-fill: #28a745;");
        errorLabel.setVisible(true);
    }
}