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
}
