package ma.smartshop.smartshop.repository;

import ma.smartshop.smartshop.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByDeletedFalse();
    Page<Product> findByDeletedFalse(Pageable pageable);
    Page<Product> findByDeletedFalseAndNameContainingIgnoreCase(String name, Pageable pageable);
}