package com.roktim;

import com.roktim.util.DatabaseManager;
import com.roktim.util.NavigationUtil;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            System.out.println("========================================");
            System.out.println("  🩸 ROKTIM - Blood Donation System");
            System.out.println("========================================");

            // Initialize database
            DatabaseManager.initializeDatabase();

            // Set stage properties
            primaryStage.setTitle("Roktim - Blood Donation Management System");
            primaryStage.setWidth(1200);
            primaryStage.setHeight(800);
            primaryStage.setMinWidth(800);
            primaryStage.setMinHeight(600);

            // Load login screen
            NavigationUtil.navigateTo(NavigationUtil.getViewPath("login.fxml"), primaryStage);

            // Handle close
            primaryStage.setOnCloseRequest(event -> {
                DatabaseManager.closeConnection();
                System.out.println("✓ Application closed successfully");
            });

        } catch (Exception e) {
            System.err.println("✗ Error starting application: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        DatabaseManager.closeConnection();
    }

    public static void main(String[] args) {
        launch(args);
    }
}