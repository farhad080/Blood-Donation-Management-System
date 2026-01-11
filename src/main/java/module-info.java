module com.roktim {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.roktim to javafx.fxml;
    opens com.roktim.controller to javafx.fxml;
    opens com.roktim.model to javafx.fxml;

    exports com.roktim;
    exports com.roktim.controller;
    exports com.roktim.service;
    exports com.roktim.dao;
    exports com.roktim.model;
    exports com.roktim.util;
}