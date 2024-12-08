package com.khabir.factory;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author khabir
 **/
public class OrderGenerator {
        public static void main(String[] args) {
        // Number of orders to create
        int numberOfOrders = 10;
        List<JSONObject> orders = new ArrayList<>();

        // Sample customer IDs to link with orders
        List<Integer> customerIds = List.of(1, 2, 3, 4, 5);

        Random random = new Random();

        // Generate orders
        for (int i = 0; i < numberOfOrders; i++) {
            JSONObject order = new JSONObject();
            order.put("id", i + 1);
            order.put("date", "2024-12-08T" + String.format("%02d:%02d:%02d", random.nextInt(24), random.nextInt(60), random.nextInt(60)));
            order.put("amount", BigDecimal.valueOf(random.nextDouble() * 1000).setScale(2, RoundingMode.HALF_UP));
            order.put("customer_id", customerIds.get(random.nextInt(customerIds.size())));

            orders.add(order);
        }

        // Write orders to input.json
        String fileName = System.currentTimeMillis() + ".json";
        try (FileWriter file = new FileWriter("/home/khabir/docs/0cours/0gi2/s3/java/libre/threads/orders-distributer/src/main/java/com/khabir/input/" + fileName)) {
            JSONArray jsonArray = new JSONArray(orders);
            file.write(jsonArray.toString(4)); // Indented for better readability
            System.out.println("Orders have been written to input.json");
        } catch (IOException e) {
            System.err.println("An error occurred while writing the JSON file: " + e.getMessage());
        }
    }
}
