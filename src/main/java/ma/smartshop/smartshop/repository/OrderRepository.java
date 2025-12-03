package ma.smartshop.smartshop.repository;

import ma.smartshop.smartshop.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import ma.smartshop.smartshop.entity.Client;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByClientOrderByCreatedAtDesc(Client client);
    boolean existsByPromoCode(String promoCode);


}