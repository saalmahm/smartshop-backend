package ma.smartshop.smartshop.repository;

import ma.smartshop.smartshop.entity.Order;
import ma.smartshop.smartshop.entity.Payment;
import ma.smartshop.smartshop.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    int countByOrder(Order order);
    java.util.List<Payment> findByOrderAndStatus(Order order, PaymentStatus status);
}