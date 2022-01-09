package kc.stocktaker.repository;

import kc.stocktaker.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    boolean existsByAccountingYear(int accountingYear);

    void deleteAllByAccountingYear(int accountingYear);

    Optional<Product> getProductByEan(Long ean);

    Optional<Product> findProductById(Long id);

    List<Product> findAllByAccountingYear(int accountingYear);

}
