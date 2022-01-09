package kc.stocktaker.service;

import kc.stocktaker.dto.ProductDTO;
import kc.stocktaker.entity.Product;
import kc.stocktaker.mapper.ProductMapper;
import kc.stocktaker.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductDTO updateProduct(Long id, Integer quantity) {
        Product product = productRepository.findProductById(id).orElseThrow(() ->
                new EntityNotFoundException(String.format("Product with id: %d not found", id)));
        BigDecimal newQuantity = BigDecimal.valueOf(quantity).stripTrailingZeros();
        BigDecimal grossCost = product.getGrossCost();
        BigDecimal grossValue = grossCost.multiply(newQuantity).setScale(2, RoundingMode.HALF_EVEN);
        product.setQuantity(newQuantity);
        product.setGrossValue(grossValue);
        return productMapper.mapEntityToProductDTO(product);
    }

    public ProductDTO getProductByEan(Long ean) {
        Product product = productRepository.getProductByEan(ean).orElseThrow(() ->
                new EntityNotFoundException(String.format("Product with EAN: %d not found", ean)));
        return productMapper.mapEntityToProductDTO(product);
    }
}
