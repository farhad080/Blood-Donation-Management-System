package com.roktim.util;

import com.roktim.service.AuthService;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final String DATABASE_URL = "jdbc:sqlite:roktim.db";
    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("org.sqlite.JDBC");
                connection = DriverManager.getConnection(DATABASE_URL);
                connection.createStatement().execute("PRAGMA foreign_keys = ON");
                System.out.println("Database connected successfully!");
            } catch (ClassNotFoundException e) {
                System.err.println("SQLite JDBC Driver not found!");
                throw new SQLException("SQLite JDBC Driver not found", e);
            }
        }
        return connection;
    }

    public static void initializeDatabase() {
        try {
            createTables();
            insertDefaultAdmin();
            insertDefaultDonor();
            initializeInventory();
            System.out.println("✓ Database initialized successfully!");
        } catch (SQLException e) {
            System.err.println("✗ Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void createTables() throws SQLException {
        Connection conn = getConnection();
        Statement stmt = conn.createStatement();

        // Users table
        String createUsersTable = """
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                blood_group TEXT NOT NULL,
                phone TEXT NOT NULL,
                email TEXT NOT NULL UNIQUE,
                address TEXT,
                profile_image TEXT,
                last_donation DATE,
                username TEXT NOT NULL UNIQUE,
                password TEXT NOT NULL,
                role TEXT NOT NULL
            )
        """;

        // Inventory table
        String createInventoryTable = """
            CREATE TABLE IF NOT EXISTS inventory (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                blood_group TEXT NOT NULL UNIQUE,
                units INTEGER NOT NULL DEFAULT 0
            )
        """;

        // Requests table
        String createRequestsTable = """
            CREATE TABLE IF NOT EXISTS requests (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL,
                patient_name TEXT NOT NULL,
                blood_group TEXT NOT NULL,
                location TEXT NOT NULL,
                contact_number TEXT NOT NULL,
                message TEXT,
                urgency_level TEXT,
                status TEXT NOT NULL,
                date DATE NOT NULL,
                FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
            )
        """;

        // Donations table
        String createDonationsTable = """
            CREATE TABLE IF NOT EXISTS donations (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL,
                blood_group TEXT NOT NULL,
                units INTEGER NOT NULL,
                date DATE NOT NULL,
                FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
            )
        """;

        stmt.execute(createUsersTable);
        stmt.execute(createInventoryTable);
        stmt.execute(createRequestsTable);
        stmt.execute(createDonationsTable);
        stmt.close();

        System.out.println("✓ Tables created successfully!");
    }

    private static void insertDefaultAdmin() throws SQLException {
        Connection conn = getConnection();

        String checkQuery = "SELECT COUNT(*) FROM users WHERE username = ?";
        PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
        checkStmt.setString(1, "admin");
        ResultSet rs = checkStmt.executeQuery();

        if (rs.next() && rs.getInt(1) > 0) {
            System.out.println("✓ Default admin already exists.");
            checkStmt.close();
            return;
        }
        checkStmt.close();

        String insertQuery = """
            INSERT INTO users (name, blood_group, phone, email, address, username, password, role)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;

        PreparedStatement pstmt = conn.prepareStatement(insertQuery);
        pstmt.setString(1, "Admin");
        pstmt.setString(2, "O+");
        pstmt.setString(3, "01700000000");
        pstmt.setString(4, "admin@roktim.com");
        pstmt.setString(5, "Khulna, Bangladesh");
        pstmt.setString(6, "admin");
        pstmt.setString(7, AuthService.hashPassword("admin123"));
        pstmt.setString(8, "ADMIN");

        pstmt.executeUpdate();
        pstmt.close();

        System.out.println("✓ Default admin created (username: admin, password: admin123)");
    }

    private static void insertDefaultDonor() throws SQLException {
        Connection conn = getConnection();

        String checkQuery = "SELECT COUNT(*) FROM users WHERE username = ?";
        PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
        checkStmt.setString(1, "farhad");
        ResultSet rs = checkStmt.executeQuery();

        if (rs.next() && rs.getInt(1) > 0) {
            System.out.println("✓ Default donor already exists.");
            checkStmt.close();
            return;
        }
        checkStmt.close();

        String insertQuery = """
            INSERT INTO users (name, blood_group, phone, email, address, username, password, role)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;

        PreparedStatement pstmt = conn.prepareStatement(insertQuery);
        pstmt.setString(1, "Farhad");
        pstmt.setString(2, "A+");
        pstmt.setString(3, "017629028096");
        pstmt.setString(4, "farhad45@gmail.com");
        pstmt.setString(5, "Natore, Bangladesh");
        pstmt.setString(6, "farhad");
        pstmt.setString(7, AuthService.hashPassword("1234"));
        pstmt.setString(8, "USER");

        pstmt.executeUpdate();
        pstmt.close();

        System.out.println("✓ Default donor created (username: farhad, password: 1234)");
    }

    private static void initializeInventory() throws SQLException {
        Connection conn = getConnection();

        String checkQuery = "SELECT COUNT(*) FROM inventory";
        Statement checkStmt = conn.createStatement();
        ResultSet rs = checkStmt.executeQuery(checkQuery);

        if (rs.next() && rs.getInt(1) > 0) {
            System.out.println("✓ Inventory already initialized.");
            checkStmt.close();
            return;
        }
        checkStmt.close();

        String insertQuery = "INSERT INTO inventory (blood_group, units) VALUES (?, ?)";
        PreparedStatement pstmt = conn.prepareStatement(insertQuery);

        String[] bloodGroups = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
        for (String bg : bloodGroups) {
            pstmt.setString(1, bg);
            pstmt.setInt(2, 0);
            pstmt.executeUpdate();
        }

        pstmt.close();
        System.out.println("✓ Inventory initialized with all blood groups.");
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("✓ Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("✗ Error closing connection: " + e.getMessage());
        }
    }
}