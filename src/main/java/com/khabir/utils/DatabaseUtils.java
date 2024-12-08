package com.khabir.utils;

import com.khabir.models.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;

public class DatabaseUtils {

  private static final Logger logger = LoggerFactory.getLogger(DatabaseUtils.class);
  private static final String DB_URL;
  private static final String DB_USER;
  private static final String DB_PASSWORD;

  static {
    Properties properties = new Properties();
    try (InputStream input = DatabaseUtils.class.getClassLoader().getResourceAsStream("config.properties")) {
      if (input == null) {
        throw new IOException("Unable to find config.properties");
      }
      properties.load(input);
    } catch (IOException ex) {
      throw new ExceptionInInitializerError("Failed to load database configuration");
    }
    DB_URL = properties.getProperty("db.url");
    DB_USER = properties.getProperty("db.user");
    DB_PASSWORD = properties.getProperty("db.password");
  }

  public static boolean createTables() {
    // SQL to create 'customers' table if it doesn't exist
    String createCustomersTableSQL = "CREATE TABLE IF NOT EXISTS customer (" +
        "id SERIAL PRIMARY KEY, " +
        "nom VARCHAR(100) NOT NULL, " +
        "email VARCHAR(100) UNIQUE NOT NULL, " +
        "phone VARCHAR(15))";

    // SQL to create 'orders' table if it doesn't exist
    String createOrdersTableSQL = "CREATE TABLE IF NOT EXISTS orders (" +
        "id UUID PRIMARY KEY, " +
        "date DATE NOT NULL, " +
        "amount DECIMAL(10, 2) NOT NULL, " +
        "customer_id INT NOT NULL, " +
        "FOREIGN KEY (customer_id) REFERENCES customer(id))";

    try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        Statement statement = connection.createStatement()) {

      statement.executeUpdate(createCustomersTableSQL);
      logger.info("Table 'customer' created successfully or already exists.");

      statement.executeUpdate(createOrdersTableSQL);
      logger.info("Table 'orders' created successfully or already exists.");

    } catch (SQLException e) {
      logger.error("Error creating tables: {}", e.getMessage());
      return false;
    }
    return true;
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
      logger.error("Database error: {}", e.getMessage());
      return false;
    }
  }

  public static void addOrdersToDatabase(List<Order> orders) {
    String insertOrderSQL = "INSERT INTO orders (id, date, amount, customer_id) VALUES (?, ?, ?, ?)";
    try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        PreparedStatement preparedStatement = connection.prepareStatement(insertOrderSQL)) {
      for (Order order : orders) {
        preparedStatement.setObject(1, java.util.UUID.fromString(order.getId()));
        preparedStatement.setDate(2, order.getDate());
        preparedStatement.setDouble(3, order.getAmount());
        preparedStatement.setInt(4, order.getCustomer_id());
        preparedStatement.executeUpdate();
      }
      logger.info("Orders added to the database successfully.");
    } catch (SQLException e) {
      logger.error("Failed to add orders to the database: {}", e.getMessage());
      e.printStackTrace();
    }
  }
}