package ma.smartshop.smartshop.entity;
import ma.smartshop.smartshop.entity.User;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.smartshop.smartshop.enums.CustomerTier;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "clients")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    private String phone;

    private String address;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CustomerTier tier;

    @Column(nullable = false)
    private Integer totalOrders;

    @Column(nullable = false)
    private BigDecimal totalSpent;

    private LocalDateTime firstOrderDate;

    private LocalDateTime lastOrderDate;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;
}