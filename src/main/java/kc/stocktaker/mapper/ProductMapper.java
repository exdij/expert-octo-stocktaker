package kc.stocktaker.mapper;

import kc.stocktaker.dto.ProductDTO;
import kc.stocktaker.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class ProductMapper {

    public Product mapProductDTOToEntity(ProductDTO productDTO) {
        return Product.builder()
                .name(productDTO.getName())
                .ean(productDTO.getEan())
                .category(productDTO.getCategory())
                .quantity(productDTO.getQuantity())
                .startingQuantity(productDTO.getStartingQuantity())
                .grossCost(productDTO.getGrossCost())
                .grossValue(productDTO.getGrossValue())
                .productId(productDTO.getProductId())
                .accountingYear(productDTO.getAccountingYear())
                .build();
    }

    public ProductDTO mapEntityToProductDTO(Product product) {
        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .ean(product.getEan())
                .category(product.getCategory())
                .quantity(product.getQuantity())
                .startingQuantity(product.getStartingQuantity())
                .grossCost(product.getGrossCost())
                .grossValue(product.getGrossValue())
                .productId(product.getProductId())
                .accountingYear(product.getAccountingYear())
                .build();
    }
}
