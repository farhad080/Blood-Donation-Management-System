package com.roktim.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.roktim.model.BloodGroup;
import com.roktim.model.Donation;
import com.roktim.util.DatabaseManager;

public class DonationDAO {

    public List<Donation> getDonationsByUserId(int userId) {
        List<Donation> donations = new ArrayList<>();
        String query = "SELECT * FROM donations WHERE user_id = ? ORDER BY date DESC";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                donations.add(extractDonationFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("✗ Error getting donations: " + e.getMessage());
        }

        return donations;
    }

    public List<Donation> getAllDonations() {
        List<Donation> donations = new ArrayList<>();
        String query = "SELECT d.*, u.name as donor_name FROM donations d " +
                      "LEFT JOIN users u ON d.user_id = u.id " +
                      "ORDER BY d.date DESC";

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Donation donation = extractDonationFromResultSet(rs);
                donation.setDonorName(rs.getString("donor_name"));
                donations.add(donation);
            }
        } catch (SQLException e) {
            System.err.println("✗ Error getting donations: " + e.getMessage());
        }

        return donations;
    }

    public boolean addDonation(Donation donation) {
        String query = "INSERT INTO donations (user_id, blood_group, units, date) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, donation.getUserId());
            pstmt.setString(2, donation.getBloodGroup().getDisplayName());
            pstmt.setInt(3, donation.getUnits());
            pstmt.setDate(4, Date.valueOf(donation.getDate()));
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✓ Donation added successfully");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("✗ Error adding donation: " + e.getMessage());
        }
        
        return false;
    }

    public boolean updateDonation(Donation donation) {
        String query = "UPDATE donations SET user_id = ?, blood_group = ?, units = ?, date = ? WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, donation.getUserId());
            pstmt.setString(2, donation.getBloodGroup().getDisplayName());
            pstmt.setInt(3, donation.getUnits());
            pstmt.setDate(4, Date.valueOf(donation.getDate()));
            pstmt.setInt(5, donation.getId());
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✓ Donation updated successfully");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("✗ Error updating donation: " + e.getMessage());
        }
        
        return false;
    }

    public boolean deleteDonation(int donationId) {
        String query = "DELETE FROM donations WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, donationId);
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✓ Donation deleted successfully");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("✗ Error deleting donation: " + e.getMessage());
        }
        
        return false;
    }

    private Donation extractDonationFromResultSet(ResultSet rs) throws SQLException {
        Donation donation = new Donation();
        donation.setId(rs.getInt("id"));
        donation.setUserId(rs.getInt("user_id"));
        donation.setBloodGroup(BloodGroup.fromString(rs.getString("blood_group")));
        donation.setUnits(rs.getInt("units"));

        Date date = rs.getDate("date");
        if (date != null) {
            donation.setDate(date.toLocalDate());
        }

        return donation;
    }
}