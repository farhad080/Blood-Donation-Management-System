package com.roktim.model;

import java.time.LocalDate;

public class Request {
    private int id;
    private int userId;
    private String patientName;
    private BloodGroup bloodGroup;
    private String location;
    private String contactNumber;
    private String message;
    private String urgencyLevel;
    private RequestStatus status;
    private LocalDate date;

    public Request() {}

    public Request(int userId, String patientName, BloodGroup bloodGroup, String location,
                   String contactNumber, String message, String urgencyLevel,
                   RequestStatus status, LocalDate date) {
        this.userId = userId;
        this.patientName = patientName;
        this.bloodGroup = bloodGroup;
        this.location = location;
        this.contactNumber = contactNumber;
        this.message = message;
        this.urgencyLevel = urgencyLevel;
        this.status = status;
        this.date = date;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }

    public BloodGroup getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(BloodGroup bloodGroup) { this.bloodGroup = bloodGroup; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getUrgencyLevel() { return urgencyLevel; }
    public void setUrgencyLevel(String urgencyLevel) { this.urgencyLevel = urgencyLevel; }

    public RequestStatus getStatus() { return status; }
    public void setStatus(RequestStatus status) { this.status = status; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
}