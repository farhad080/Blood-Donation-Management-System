package com.roktim.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.roktim.model.BloodGroup;
import com.roktim.model.User;
import com.roktim.model.UserRole;
import com.roktim.service.AuthService;
import com.roktim.util.DatabaseManager;

public class UserDAO {

    public User authenticateUser(String username, String password, UserRole role) {
        String query = "SELECT * FROM users WHERE username = ? AND role = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, username);
            pstmt.setString(2, role.name());

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String hashedPassword = rs.getString("password");

                if (AuthService.verifyPassword(password, hashedPassword)) {
                    return extractUserFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("✗ Error authenticating user: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    public User getUserById(int id) {
        String query = "SELECT * FROM users WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractUserFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("✗ Error getting user: " + e.getMessage());
        }

        return null;
    }

    public List<User> getAllDonors() {
        List<User> donors = new ArrayList<>();
        String query = "SELECT * FROM users WHERE role = 'USER'";

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                donors.add(extractUserFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("✗ Error getting donors: " + e.getMessage());
        }

        return donors;
    }

    public boolean registerUser(User user) {
        String query = """
            INSERT INTO users (name, blood_group, phone, email, address, username, password, role, profile_image)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getBloodGroup().getDisplayName());
            pstmt.setString(3, user.getPhone());
            pstmt.setString(4, user.getEmail());
            pstmt.setString(5, user.getAddress());
            pstmt.setString(6, user.getUsername());
            pstmt.setString(7, user.getPassword());
            pstmt.setString(8, user.getRole().name());
            pstmt.setString(9, user.getProfileImage());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("✗ Error registering user: " + e.getMessage());
            return false;
        }
    }

    public boolean updateUser(User user) {
        String query = """
            UPDATE users SET name = ?, blood_group = ?, phone = ?, email = ?, 
            address = ?, profile_image = ?, last_donation = ?
            WHERE id = ?
        """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getBloodGroup().getDisplayName());
            pstmt.setString(3, user.getPhone());
            pstmt.setString(4, user.getEmail());
            pstmt.setString(5, user.getAddress());
            pstmt.setString(6, user.getProfileImage());

            if (user.getLastDonation() != null) {
                pstmt.setDate(7, Date.valueOf(user.getLastDonation()));
            } else {
                pstmt.setNull(7, Types.DATE);
            }

            pstmt.setInt(8, user.getId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("✗ Error updating user: " + e.getMessage());
            return false;
        }
    }

    public boolean usernameExists(String username) {
        String query = "SELECT COUNT(*) FROM users WHERE username = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("✗ Error checking username: " + e.getMessage());
        }

        return false;
    }

    public boolean emailExists(String email) {
        String query = "SELECT COUNT(*) FROM users WHERE email = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("✗ Error checking email: " + e.getMessage());
        }

        return false;
    }

    public int countAllUsers() {
        return countByQuery("SELECT COUNT(*) FROM users");
    }

    public int countDonors() {
        return countByQuery("SELECT COUNT(*) FROM users WHERE role = 'USER'");
    }

    public boolean deleteUser(int userId) {
        String query = "DELETE FROM users WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("✗ Error deleting user: " + e.getMessage());
            return false;
        }
    }

    public List<User> searchDonors(String bloodGroup, String location) {
        List<User> donors = new ArrayList<>();
        StringBuilder query = new StringBuilder("SELECT * FROM users WHERE role = 'USER'");
        
        boolean hasBloodGroup = bloodGroup != null && !bloodGroup.trim().isEmpty();
        boolean hasLocation = location != null && !location.trim().isEmpty();
        
        if (hasBloodGroup) {
            query.append(" AND blood_group = ?");
        }
        if (hasLocation) {
            query.append(" AND LOWER(address) LIKE LOWER(?)");
        }
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query.toString())) {
            
            int paramIndex = 1;
            if (hasBloodGroup) {
                pstmt.setString(paramIndex++, bloodGroup);
            }
            if (hasLocation) {
                pstmt.setString(paramIndex++, "%" + location + "%");
            }
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                donors.add(extractUserFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("✗ Error searching donors: " + e.getMessage());
        }
        
        return donors;
    }

    private int countByQuery(String query) {
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("✗ Error running count query: " + e.getMessage());
        }
        return 0;
    }

    private User extractUserFromResultSet(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setName(rs.getString("name"));
        user.setBloodGroup(BloodGroup.fromString(rs.getString("blood_group")));
        user.setPhone(rs.getString("phone"));
        user.setEmail(rs.getString("email"));
        user.setAddress(rs.getString("address"));
        user.setProfileImage(rs.getString("profile_image"));

        Date lastDonationDate = rs.getDate("last_donation");
        if (lastDonationDate != null) {
            user.setLastDonation(lastDonationDate.toLocalDate());
        }

        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setRole(UserRole.fromString(rs.getString("role")));

        return user;
    }
}