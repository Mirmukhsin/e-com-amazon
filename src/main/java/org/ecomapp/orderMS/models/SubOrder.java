package org.ecomapp.orderMS.models;

import jakarta.persistence.*;
import lombok.*;
import org.ecomapp.orderMS.enums.SubOrderStatus;
import org.ecomapp.userMS.models.User;

@Entity
@Table(name = "sub_orders")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @Column(nullable = false)
    private Double subTotal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubOrderStatus status;
}
