package kc.stocktaker.controller;

import kc.stocktaker.dto.ProductDTO;
import kc.stocktaker.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.PositiveOrZero;

@Validated
@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PatchMapping("/{ean}")
    public ProductDTO updateProductQuantity(@PathVariable Long ean,
                                            @RequestParam @PositiveOrZero
                                                    Integer quantity) {
        return productService.updateProduct(ean, quantity);
    }

    @GetMapping("/{ean}")
    public ProductDTO getProductByEan(@RequestParam Long ean) {
        return productService.getProductByEan(ean);
    }
}
