package com.example.t10;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;
import java.sql.*;

public class DBConnect {
    private static final String JDBC_URL = "jdbc:h2:~/productdb;DB_CLOSE_DELAY=-1;AUTO_SERVER=TRUE";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    private Connection connection;

    public void connect() throws SQLException {
        try {
            Class.forName("org.h2.Driver");
            System.out.println("H2 driver loaded successfully");
            connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
            System.out.println("Connected to H2 database at: " + JDBC_URL);
        } catch (ClassNotFoundException e) {
            throw new SQLException("H2 Driver not found. Make sure h2-x.x.x.jar is in classpath", e);
        }
    }

    public CachedRowSet executeQuery(String query) throws SQLException {
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            CachedRowSet crs = RowSetProvider.newFactory().createCachedRowSet();
            crs.populate(rs);
            return crs;
        }
    }

    public int executeUpdate(String sql) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            return stmt.executeUpdate(sql);
        }
    }

    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return connection.prepareStatement(sql);
    }

    public PreparedStatement prepareStatement(String sql, int returnGeneratedKeys) throws SQLException {
        return connection.prepareStatement(sql, returnGeneratedKeys);
    }

    public void disconnect() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    public Connection getConnection() {
        return connection;
    }
}