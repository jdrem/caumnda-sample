package net.remgant.camunda.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    private String id;
    private String accountName;
    private BigDecimal totalPrice;
    private List<OrderLine> orderLines;
}
