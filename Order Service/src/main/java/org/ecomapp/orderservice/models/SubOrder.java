package org.ecomapp.orderservice.models;

import jakarta.persistence.*;
import lombok.*;
import org.ecomapp.orderservice.enums.SubOrderStatus;

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

    private Long sellerId;

    @Column(nullable = false)
    private Double subTotal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubOrderStatus status;
}
