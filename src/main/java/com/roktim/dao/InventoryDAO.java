package com.roktim.dao;

import com.roktim.model.BloodGroup;
import com.roktim.model.Inventory;
import com.roktim.util.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InventoryDAO {

    /**
     * Get inventory by blood group
     */
    public Inventory getInventoryByBloodGroup(BloodGroup bloodGroup) {
        String query = "SELECT * FROM inventory WHERE blood_group = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, bloodGroup.getDisplayName());
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractInventoryFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting inventory: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Get all inventory
     */
    public List<Inventory> getAllInventory() {
        List<Inventory> inventoryList = new ArrayList<>();
        String query = "SELECT * FROM inventory";

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                inventoryList.add(extractInventoryFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all inventory: " + e.getMessage());
            e.printStackTrace();
        }

        return inventoryList;
    }

    /**
     * Update inventory units
     */
    public boolean updateInventory(BloodGroup bloodGroup, int units) {
        String query = "UPDATE inventory SET units = ? WHERE blood_group = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, units);
            pstmt.setString(2, bloodGroup.getDisplayName());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error updating inventory: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Add units to inventory
     */
    public boolean addUnits(BloodGroup bloodGroup, int unitsToAdd) {
        Inventory inventory = getInventoryByBloodGroup(bloodGroup);
        if (inventory != null) {
            int newUnits = inventory.getUnits() + unitsToAdd;
            return updateInventory(bloodGroup, newUnits);
        }
        return false;
    }

    /**
     * Reduce units from inventory
     */
    public boolean reduceUnits(BloodGroup bloodGroup, int unitsToReduce) {
        Inventory inventory = getInventoryByBloodGroup(bloodGroup);
        if (inventory != null) {
            int currentUnits = inventory.getUnits();
            if (currentUnits >= unitsToReduce) {
                int newUnits = currentUnits - unitsToReduce;
                return updateInventory(bloodGroup, newUnits);
            } else {
                System.err.println("Insufficient units in inventory");
                return false;
            }
        }
        return false;
    }

    /**
     * Get total blood units across all groups
     */
    public int getTotalBloodUnits() {
        String query = "SELECT SUM(units) FROM inventory";

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getting total blood units: " + e.getMessage());
        }

        return 0;
    }

    /**
     * Extract Inventory object from ResultSet
     */
    private Inventory extractInventoryFromResultSet(ResultSet rs) throws SQLException {
        Inventory inventory = new Inventory();
        inventory.setId(rs.getInt("id"));
        inventory.setBloodGroup(BloodGroup.fromString(rs.getString("blood_group")));
        inventory.setUnits(rs.getInt("units"));
        return inventory;
    }
}