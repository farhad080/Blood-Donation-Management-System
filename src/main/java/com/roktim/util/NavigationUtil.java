package com.roktim.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class NavigationUtil {

    public static void navigateTo(String fxmlPath, Stage stage) {
        try {
            Parent root = loadFXML(fxmlPath);
            Scene scene = new Scene(root);

            // Add CSS
            java.net.URL cssResource = NavigationUtil.class.getResource("/css/styles.css");
            if (cssResource != null) {
                scene.getStylesheets().add(cssResource.toExternalForm());
            }

            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("✗ Error loading FXML: " + fxmlPath);
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("✗ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Parent loadFXML(String fxmlPath) throws IOException {
        java.net.URL resource = null;
        
        // Try multiple approaches to find the resource
        // 1. Try with Main class
        resource = com.roktim.Main.class.getResource(fxmlPath);
        
        // 2. Try with NavigationUtil class
        if (resource == null) {
            resource = NavigationUtil.class.getResource(fxmlPath);
        }
        
        // 3. Try with ClassLoader
        if (resource == null) {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            resource = classLoader.getResource(fxmlPath.substring(1)); // Remove leading /
        }
        
        // 4. Try with system class loader
        if (resource == null) {
            resource = ClassLoader.getSystemClassLoader().getResource(fxmlPath.substring(1));
        }
        
        if (resource == null) {
            throw new IOException("FXML file not found: " + fxmlPath + 
                ". Make sure the file exists in src/main/resources" + fxmlPath);
        }
        
        FXMLLoader loader = new FXMLLoader(resource);
        return loader.load();
    }

    public static String getViewPath(String fileName) {
        return "/com.roktim/view/" + fileName;
    }
}