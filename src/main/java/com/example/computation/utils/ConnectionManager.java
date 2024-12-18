package com.example.computation.utils;

import com.example.computation.app.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;


public class ConnectionManager {
    private static Connection connection;

    public static Connection getConnection() {
        System.setProperty("javax.net.ssl.trustStore", "/home/admin/.mysql/YATrustStore");
        System.setProperty("javax.net.ssl.trustStorePassword", "12345678");
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try {

                connection = DriverManager.getConnection(
                        Configuration.jdbcString,
                        Configuration.databaseUser,
                        Configuration.getDatabasePassword);
                ResultSet q = connection.createStatement().executeQuery("SELECT version()");
                if (q.next()) {
                    System.out.println(q.getString(1));
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                System.out.println("Failed to create the database connection.");
            }
        }
        catch (ClassNotFoundException ex) {

            ex.printStackTrace();
            System.out.println("Driver not found.");
        }

        return connection;
    }
}