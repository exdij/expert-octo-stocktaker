package kc.stocktaker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@Builder
@Getter
@Setter
@NoArgsConstructor
public class ProductDTO {

    private Long id;

    private String name;

    private long ean;

    private String category;

    private BigDecimal quantity;

    private BigDecimal startingQuantity;

    private BigDecimal grossCost;

    private BigDecimal grossValue;

    private long productId;

    private int accountingYear;
}
