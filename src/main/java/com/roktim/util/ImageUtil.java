package com.roktim.util;

import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

public class ImageUtil {

    private static final String PROFILE_IMAGES_DIR = "profile_images/";

    public static File selectImage(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Profile Picture");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.jpeg", "*.png", "*.gif")
        );
        return fileChooser.showOpenDialog(stage);
    }

    public static String saveProfileImage(File sourceFile) throws IOException {
        Path profileDir = Paths.get(PROFILE_IMAGES_DIR);
        if (!Files.exists(profileDir)) {
            Files.createDirectories(profileDir);
        }

        String extension = getFileExtension(sourceFile.getName());
        String fileName = UUID.randomUUID().toString() + "." + extension;
        Path destinationPath = profileDir.resolve(fileName);

        Files.copy(sourceFile.toPath(), destinationPath, StandardCopyOption.REPLACE_EXISTING);

        return PROFILE_IMAGES_DIR + fileName;
    }

    public static Image loadImage(String imagePath) {
        try {
            if (imagePath == null || imagePath.isEmpty()) {
                return getDefaultProfileImage();
            }

            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                return new Image(imageFile.toURI().toString());
            } else {
                return getDefaultProfileImage();
            }
        } catch (Exception e) {
            System.err.println("✗ Error loading image: " + imagePath);
            return getDefaultProfileImage();
        }
    }

    public static Image getDefaultProfileImage() {
        try {
            return new Image(ImageUtil.class.getResourceAsStream("/images/default-profile.png"));
        } catch (Exception e) {
            // Create a simple colored circle if default image not found
            System.err.println("✗ Default profile image not found, using fallback");
            return null;
        }
    }

    public static boolean isValidImageFile(File file) {
        if (file == null || !file.exists()) {
            return false;
        }
        String fileName = file.getName().toLowerCase();
        return fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") ||
                fileName.endsWith(".png") || fileName.endsWith(".gif");
    }

    private static String getFileExtension(String fileName) {
        int lastIndexOf = fileName.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return "";
        }
        return fileName.substring(lastIndexOf + 1);
    }
}