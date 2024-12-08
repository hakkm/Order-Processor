package com.khabir.main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.khabir.models.Order;
import com.khabir.utils.DatabaseUtils;
import com.khabir.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * OrderReader class to read orders from JSON files and verify customer IDs.
 */
public class OrderReader {

    public static List<Order> readOrdersFromInputDirectory(String directoryPath) {
        List<Order> orders = new ArrayList<>();
        File directory = new File(directoryPath);
        ObjectMapper objectMapper = new ObjectMapper();

        if (directory.isDirectory()) {
            File[] files = directory.listFiles((dir, name) -> name.endsWith(".json"));
            if (files == null || files.length == 0) {
                System.out.println("No files in input directory.");
                return orders;
            }
            for (File file : files) {
                try {
                    // Parse a JSON array of orders
                    List<Order> fileOrders = objectMapper.readValue(file,
                            objectMapper.getTypeFactory().constructCollectionType(List.class, Order.class));
                    boolean validFile = true;
                    for (Order order : fileOrders) {
                        if (!DatabaseUtils.isCustomerIdValid(order.getCustomer_id())) {
                            System.err.println(
                                    "Invalid customer_id: " + order.getCustomer_id() + " in file: " + file.getName());
                            FileUtils.moveFileToErrorDirectory(file);
                            validFile = false;
                            break;
                        }
                    }
                    if (validFile) {
                        orders.addAll(fileOrders);
                        DatabaseUtils.addOrdersToDatabase(fileOrders); // Add orders to the database
                        FileUtils.moveFileToOutputDirectory(file); // Move file to output directory if everything goes
                                                                   // well
                    }
                } catch (IOException e) {
                    System.err.println("Failed to read or parse file: " + file.getName());
                    e.printStackTrace();
                    FileUtils.moveFileToErrorDirectory(file);
                }
            }
        } else {
            System.err.println("The provided path is not a directory: " + directoryPath);
        }

        return orders;
    }

    public static void main(String[] args) {
        String inputDirectoryPath = "src/main/java/com/khabir/data/input";

        Runnable task = () -> {
            while (true) {
                List<Order> orders = readOrdersFromInputDirectory(inputDirectoryPath);
                for (Order order : orders) {
                    System.out.println("Valid order: " + order);
                }
                try {
                    Thread.sleep(30000); // Sleep for 30 seconds
                } catch (InterruptedException e) {
                    System.err.println("Thread interrupted: " + e.getMessage());
                    Thread.currentThread().interrupt();
                }
            }
        };

        Thread thread = new Thread(task);
        thread.start();
    }
}
