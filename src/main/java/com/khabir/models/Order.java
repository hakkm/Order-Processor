package com.khabir.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Date;

/**
 * @author khabir
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    private Date date;
    private double amount;
    private int id;
    private int customer_id;
}