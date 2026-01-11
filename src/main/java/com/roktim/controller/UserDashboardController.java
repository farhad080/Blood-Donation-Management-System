package com.roktim.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

import com.roktim.dao.DonationDAO;
import com.roktim.dao.RequestDAO;
import com.roktim.model.Donation;
import com.roktim.model.Request;
import com.roktim.model.User;
import com.roktim.service.AuthService;
import com.roktim.service.SessionManager;
import com.roktim.util.ImageUtil;
import com.roktim.util.NavigationUtil;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class UserDashboardController {

    @FXML private ImageView profileImageView;
    @FXML private Text welcomeText;
    @FXML private Text bloodGroupText;
    @FXML private Label lastDonationLabel;
    @FXML private Label nextEligibleLabel;
    @FXML private Label totalDonationsLabel;
    @FXML private Text myRequestsText;
    @FXML private Text donationsText;
    @FXML private Text unitsText;
    @FXML private ListView<String> activityListView;

    private final AuthService authService = new AuthService();
    private final DonationDAO donationDAO = new DonationDAO();
    private final RequestDAO requestDAO = new RequestDAO();
    private User currentUser;

    @FXML
    public void initialize() {
        loadUserData();
        loadStatistics();
        loadRecentActivity();
    }

    private void loadUserData() {
        currentUser = SessionManager.getInstance().getCurrentUser();

        if (currentUser != null) {
            welcomeText.setText("Welcome, " + currentUser.getName() + "!");
            bloodGroupText.setText(currentUser.getBloodGroup().getDisplayName());

            if (currentUser.getProfileImage() != null) {
                profileImageView.setImage(ImageUtil.loadImage(currentUser.getProfileImage()));
            } else {
                profileImageView.setImage(ImageUtil.getDefaultProfileImage());
            }

            if (currentUser.getLastDonation() != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
                lastDonationLabel.setText(currentUser.getLastDonation().format(formatter));

                LocalDate nextEligible = currentUser.getLastDonation().plusMonths(3);
                long daysUntilEligible = ChronoUnit.DAYS.between(LocalDate.now(), nextEligible);

                if (daysUntilEligible <= 0) {
                    nextEligibleLabel.setText("Ready to donate now!");
                    nextEligibleLabel.setStyle("-fx-text-fill: #28a745;");
                } else {
                    nextEligibleLabel.setText(nextEligible.format(formatter) + " (" + daysUntilEligible + " days)");
                    nextEligibleLabel.setStyle("-fx-text-fill: #DC143C;");
                }
            } else {
                lastDonationLabel.setText("Never donated");
                nextEligibleLabel.setText("Ready to donate!");
                nextEligibleLabel.setStyle("-fx-text-fill: #28a745;");
            }
        }
    }

    private void loadStatistics() {
        if (currentUser != null) {
            List<Donation> donations = donationDAO.getDonationsByUserId(currentUser.getId());
            int donationCount = donations.size();
            totalDonationsLabel.setText(String.valueOf(donationCount));
            donationsText.setText(donationCount + " Times");

            int totalUnits = donations.stream().mapToInt(Donation::getUnits).sum();
            unitsText.setText(totalUnits + " Units");

            List<Request> requests = requestDAO.getRequestsByUserId(currentUser.getId());
            long activeRequests = requests.stream()
                    .filter(r -> "PENDING".equals(r.getStatus().name()))
                    .count();
            myRequestsText.setText(activeRequests + " Active");
        }
    }

    private void loadRecentActivity() {
        activityListView.getItems().clear();

        if (currentUser != null) {
            List<Donation> recentDonations = donationDAO.getDonationsByUserId(currentUser.getId());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");

            for (int i = 0; i < Math.min(5, recentDonations.size()); i++) {
                Donation donation = recentDonations.get(i);
                String activity = "💉 Donated " + donation.getUnits() + " units on " +
                        donation.getDate().format(formatter);
                activityListView.getItems().add(activity);
            }

            List<Request> recentRequests = requestDAO.getRequestsByUserId(currentUser.getId());
            for (int i = 0; i < Math.min(3, recentRequests.size()); i++) {
                Request request = recentRequests.get(i);
                String activity = "📢 Emergency request for " + request.getBloodGroup().getDisplayName() +
                        " - " + request.getStatus().getDisplayName();
                activityListView.getItems().add(activity);
            }

            if (activityListView.getItems().isEmpty()) {
                activityListView.getItems().add("No recent activity");
            }
        }
    }

    @FXML
    private void handleHome() {
        loadUserData();
        loadStatistics();
        loadRecentActivity();
    }

    @FXML
    private void handleProfile() {
        Stage stage = (Stage) welcomeText.getScene().getWindow();
        NavigationUtil.navigateTo(NavigationUtil.getViewPath("userProfile.fxml"), stage);
    }

    @FXML
    private void handleEmergencyRequest() {
        Stage stage = (Stage) welcomeText.getScene().getWindow();
        NavigationUtil.navigateTo(NavigationUtil.getViewPath("emergencyRequest.fxml"), stage);
    }

    @FXML
    private void handleLogout() {
        authService.logout();
        Stage stage = (Stage) welcomeText.getScene().getWindow();
        NavigationUtil.navigateTo(NavigationUtil.getViewPath("login.fxml"), stage);
    }
}