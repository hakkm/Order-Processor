package com.khabir;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Main {

    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/";
        String user = "postgres";
        String password = "890??jomo";

        // SQL to create 'customers' table if it doesn't exist
        String createCustomersTableSQL = "CREATE TABLE IF NOT EXISTS customer (" +
                "id SERIAL PRIMARY KEY, " +
                "nom VARCHAR(100) NOT NULL, " +
                "email VARCHAR(100) UNIQUE NOT NULL, " +
                "phone VARCHAR(15))";

        // SQL to create 'orders' table if it doesn't exist
        String createOrdersTableSQL = "CREATE TABLE IF NOT EXISTS \"order\" (" +
                "id SERIAL PRIMARY KEY, " +
                "date DATE NOT NULL, " +
                "amount DECIMAL(10, 2) NOT NULL, " +
                "customer_id INT NOT NULL, " +
                "FOREIGN KEY (customer_id) REFERENCES customer(id))";

        try (Connection connection = DriverManager.getConnection(url, user, password);
             Statement statement = connection.createStatement()) {

            statement.executeUpdate(createCustomersTableSQL);
            System.out.println("Table 'customer' created successfully or already exists.");

            statement.executeUpdate(createOrdersTableSQL);
            System.out.println("Table 'order' created successfully or already exists.");

        } catch (SQLException e) {
            System.err.println("Error creating tables: " + e.getMessage());
        }
    }
}