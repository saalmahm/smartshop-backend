package ma.smartshop.smartshop.repository;

import ma.smartshop.smartshop.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}