package com.roktim.controller;

import com.roktim.model.User;
import com.roktim.model.UserRole;
import com.roktim.service.AuthService;
import com.roktim.util.NavigationUtil;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

public class LoginController {

    @FXML private ToggleButton adminToggle;
    @FXML private ToggleButton userToggle;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private Button loginButton;
    @FXML private Hyperlink registerLink;

    private final AuthService authService = new AuthService();
    private final ToggleGroup roleToggleGroup = new ToggleGroup();

    @FXML
    public void initialize() {
        adminToggle.setToggleGroup(roleToggleGroup);
        userToggle.setToggleGroup(roleToggleGroup);
        adminToggle.setSelected(true);

        passwordField.setOnAction(event -> handleLogin());
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter both username and password");
            return;
        }

        UserRole role = adminToggle.isSelected() ? UserRole.ADMIN : UserRole.USER;

        User user = authService.login(username, password, role);

        if (user != null) {
            Stage stage = (Stage) loginButton.getScene().getWindow();

            if (role == UserRole.ADMIN) {
                NavigationUtil.navigateTo(NavigationUtil.getViewPath("adminDashboard.fxml"), stage);
            } else {
                NavigationUtil.navigateTo(NavigationUtil.getViewPath("userDashboard.fxml"), stage);
            }
        } else {
            showError("Invalid username or password");
        }
    }

    @FXML
    private void handleRegister() {
        Stage stage = (Stage) registerLink.getScene().getWindow();
        NavigationUtil.navigateTo(NavigationUtil.getViewPath("registration.fxml"), stage);
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}