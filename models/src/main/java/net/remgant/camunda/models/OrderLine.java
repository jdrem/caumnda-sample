package net.remgant.camunda.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderLine {
    private String id;
    private String itemName;
    private int itemQuantity;
    private BigDecimal itemPrice;
    private boolean completed;
    public OrderLine(String id, String itemName, int itemQuantity, BigDecimal itemPrice) {
        this.id = id;
        this.itemName = itemName;
        this.itemQuantity = itemQuantity;
        this.itemPrice = itemPrice;
        this.completed = false;
    }
}
