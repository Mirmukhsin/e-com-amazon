package org.ecomapp.reviewMS.models;

import jakarta.persistence.*;
import lombok.*;
import org.ecomapp.orderMS.models.OrderItem;
import org.ecomapp.productMS.models.Product;
import org.ecomapp.userMS.models.User;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "buyer_id", nullable = false)
    private User buyer;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "order_item_id", nullable = false)
    private OrderItem orderItem;

    @Column(nullable = false)
    private Integer rating;

    private String comment;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
