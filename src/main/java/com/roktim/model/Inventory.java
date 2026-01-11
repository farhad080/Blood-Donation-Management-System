package com.roktim.model;

public class Inventory {
    private int id;
    private BloodGroup bloodGroup;
    private int units;

    // Default constructor
    public Inventory() {
    }

    // Constructor without ID
    public Inventory(BloodGroup bloodGroup, int units) {
        this.bloodGroup = bloodGroup;
        this.units = units;
    }

    // Full constructor
    public Inventory(int id, BloodGroup bloodGroup, int units) {
        this.id = id;
        this.bloodGroup = bloodGroup;
        this.units = units;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BloodGroup getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(BloodGroup bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public int getUnits() {
        return units;
    }

    public void setUnits(int units) {
        if (units < 0) {
            throw new IllegalArgumentException("Units cannot be negative");
        }
        this.units = units;
    }

    public void addUnits(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount to add cannot be negative");
        }
        this.units += amount;
    }

    public void reduceUnits(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount to reduce cannot be negative");
        }
        if (this.units < amount) {
            throw new IllegalArgumentException("Insufficient units in inventory");
        }
        this.units -= amount;
    }

    public String getStockStatus() {
        if (units == 0) {
            return "Out of Stock";
        } else if (units < 10) {
            return "Low Stock";
        } else if (units < 30) {
            return "Available";
        } else {
            return "Good Stock";
        }
    }

    @Override
    public String toString() {
        return "Inventory{" +
                "id=" + id +
                ", bloodGroup=" + bloodGroup +
                ", units=" + units +
                ", status='" + getStockStatus() + '\'' +
                '}';
    }
}