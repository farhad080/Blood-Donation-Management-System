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
import com.roktim.model.Request;
import com.roktim.model.RequestStatus;
import com.roktim.util.DatabaseManager;

public class RequestDAO {

    public boolean createRequest(Request request) {
        String query = """
            INSERT INTO requests (user_id, patient_name, blood_group, location, contact_number, 
            message, urgency_level, status, date)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, request.getUserId());
            pstmt.setString(2, request.getPatientName());
            pstmt.setString(3, request.getBloodGroup().getDisplayName());
            pstmt.setString(4, request.getLocation());
            pstmt.setString(5, request.getContactNumber());
            pstmt.setString(6, request.getMessage());
            pstmt.setString(7, request.getUrgencyLevel());
            pstmt.setString(8, request.getStatus().name());
            pstmt.setDate(9, Date.valueOf(request.getDate()));

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("✗ Error creating request: " + e.getMessage());
            return false;
        }
    }

    public List<Request> getRequestsByUserId(int userId) {
        List<Request> requests = new ArrayList<>();
        String query = "SELECT * FROM requests WHERE user_id = ? ORDER BY date DESC";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                requests.add(extractRequestFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("✗ Error getting requests: " + e.getMessage());
        }

        return requests;
    }

    public List<Request> getAllRequests() {
        List<Request> requests = new ArrayList<>();
        String query = "SELECT * FROM requests ORDER BY date DESC";

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                requests.add(extractRequestFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("✗ Error getting all requests: " + e.getMessage());
        }

        return requests;
    }

    public int getPendingRequestCount() {
        String query = "SELECT COUNT(*) FROM requests WHERE status = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, RequestStatus.PENDING.name());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("✗ Error counting pending requests: " + e.getMessage());
        }

        return 0;
    }

    public boolean updateRequestStatus(int requestId, RequestStatus newStatus) {
        String query = "UPDATE requests SET status = ? WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, newStatus.name());
            pstmt.setInt(2, requestId);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✓ Request status updated successfully");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("✗ Error updating request status: " + e.getMessage());
        }

        return false;
    }

    public List<Request> searchRequests(String bloodGroup, String location) {
        List<Request> requests = new ArrayList<>();
        StringBuilder query = new StringBuilder("SELECT * FROM requests WHERE 1=1");
        
        boolean hasBloodGroup = bloodGroup != null && !bloodGroup.trim().isEmpty();
        boolean hasLocation = location != null && !location.trim().isEmpty();
        
        if (hasBloodGroup) {
            query.append(" AND blood_group = ?");
        }
        if (hasLocation) {
            query.append(" AND LOWER(location) LIKE LOWER(?)");
        }
        
        query.append(" ORDER BY date DESC");
        
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
                requests.add(extractRequestFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("✗ Error searching requests: " + e.getMessage());
        }
        
        return requests;
    }

    private Request extractRequestFromResultSet(ResultSet rs) throws SQLException {
        Request request = new Request();
        request.setId(rs.getInt("id"));
        request.setUserId(rs.getInt("user_id"));
        request.setPatientName(rs.getString("patient_name"));
        request.setBloodGroup(BloodGroup.fromString(rs.getString("blood_group")));
        request.setLocation(rs.getString("location"));
        request.setContactNumber(rs.getString("contact_number"));
        request.setMessage(rs.getString("message"));
        request.setUrgencyLevel(rs.getString("urgency_level"));
        request.setStatus(RequestStatus.fromString(rs.getString("status")));

        Date date = rs.getDate("date");
        if (date != null) {
            request.setDate(date.toLocalDate());
        }

        return request;
    }
}