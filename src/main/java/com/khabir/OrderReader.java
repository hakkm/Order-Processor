package com.khabir;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.khabir.models.Order;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * OrderReader class to read orders from JSON files and verify customer IDs.
 */
public class OrderReader {

    private static final String DB_URL = "jdbc:postgresql://localhost:5432/";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "890??jomo";

    public static List<Order> readOrdersFromInputDirectory(String directoryPath) {
        List<Order> orders = new ArrayList<>();
        File directory = new File(directoryPath);
        ObjectMapper objectMapper = new ObjectMapper();

        if (directory.isDirectory()) {
            File[] files = directory.listFiles((dir, name) -> name.endsWith(".json"));
            if (files != null) {
                for (File file : files) {
                    try {
                        // Parse a JSON array of orders
                        List<Order> fileOrders = objectMapper.readValue(file,
                                objectMapper.getTypeFactory().constructCollectionType(List.class, Order.class));
                        for (Order order : fileOrders) {
                            if (!isCustomerIdValid(order.getCustomer_id())) {
                                System.err.println("Invalid customer_id: " + order.getCustomer_id() + " in file: "
                                        + file.getName());
                                moveFileToErrorDirectory(file);
                                return orders; // Stop processing further orders
                            }
                        }
                        orders.addAll(fileOrders);
                    } catch (IOException e) {
                        System.err.println("Failed to read or parse file: " + file.getName());
                        e.printStackTrace();
                        moveFileToErrorDirectory(file);
                        return orders; // Stop processing further orders
                    }
                }
            }
        } else {
            System.err.println("The provided path is not a directory: " + directoryPath);
        }

        return orders;
    }

    public static boolean isCustomerIdValid(int customerId) {
        String query = "SELECT 1 FROM customer WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, customerId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            return false;
        }
    }

    public static void moveFileToErrorDirectory(File file) {
        Path sourcePath = file.toPath();
        Path targetPath = new File(file.getParentFile().getParentFile(), "err/" + file.getName()).toPath();
        try {
            Files.createDirectories(targetPath.getParent());
            Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Moved file to error directory: " + targetPath);
        } catch (IOException e) {
            System.err.println("Failed to move file to error directory: " + file.getName());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String inputDirectoryPath = "/home/khabir/docs/0cours/0gi2/s3/java/libre/threads/orders-distributer/src/main/java/com/khabir/input";
        List<Order> orders = readOrdersFromInputDirectory(inputDirectoryPath);

        for (Order order : orders) {
            System.out.println("Valid order: " + order);
        }
    }
}
