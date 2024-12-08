package com.khabir.main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.khabir.models.Order;
import com.khabir.utils.DatabaseUtils;
import com.khabir.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * OrderReader class to read orders from JSON files and verify customer IDs.
 */
public class OrderReader {

    private static final Logger logger = LoggerFactory.getLogger(OrderReader.class);
    private static final String INPUT_DIRECTORY_PATH;

    static {
        Properties properties = new Properties();
        try (InputStream input = OrderReader.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new IOException("Unable to find config.properties");
            }
            properties.load(input);
        } catch (IOException ex) {
            throw new ExceptionInInitializerError("Failed to load configuration");
        }
        INPUT_DIRECTORY_PATH = properties.getProperty("input.directory");
    }

    public static List<Order> readOrdersFromInputDirectory(String directoryPath) {
        List<Order> orders = new ArrayList<>();
        File directory = new File(directoryPath);
        ObjectMapper objectMapper = new ObjectMapper();

        if (directory.isDirectory()) {
            File[] files = directory.listFiles((dir, name) -> name.endsWith(".json"));
            if (files == null || files.length == 0) {
                logger.info("No files in input directory.");
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
                            logger.error("Invalid customer_id: {} in file: {}", order.getCustomer_id(), file.getName());
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
                    logger.error("Failed to read or parse file: {}", file.getName(), e);
                    FileUtils.moveFileToErrorDirectory(file);
                }
            }
        } else {
            logger.error("The provided path is not a directory: {}", directoryPath);
        }

        return orders;
    }

    public static void main(String[] args) {
        Runnable task = () -> {
            while (true) {
                List<Order> orders = readOrdersFromInputDirectory(INPUT_DIRECTORY_PATH);
                for (Order order : orders) {
                    logger.info("Valid order: {}", order);
                }
                try {
                    Thread.sleep(30000); // Sleep for 30 seconds
                } catch (InterruptedException e) {
                    logger.error("Thread interrupted: {}", e.getMessage());
                    Thread.currentThread().interrupt();
                }
            }
        };

        Thread thread = new Thread(task);
        thread.start();
    }
}
