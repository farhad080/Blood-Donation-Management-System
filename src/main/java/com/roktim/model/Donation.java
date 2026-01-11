package com.roktim.model;

import java.time.LocalDate;

public class Donation {
    private int id;
    private int userId;
    private BloodGroup bloodGroup;
    private int units;
    private LocalDate date;

    public Donation() {}

    public Donation(int userId, BloodGroup bloodGroup, int units, LocalDate date) {
        this.userId = userId;
        this.bloodGroup = bloodGroup;
        this.units = units;
        this.date = date;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public BloodGroup getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(BloodGroup bloodGroup) { this.bloodGroup = bloodGroup; }

    public int getUnits() { return units; }
    public void setUnits(int units) { this.units = units; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
}