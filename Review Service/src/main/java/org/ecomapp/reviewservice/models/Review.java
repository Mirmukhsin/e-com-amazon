package org.ecomapp.reviewservice.models;

import jakarta.persistence.*;
import lombok.*;
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

    private Long buyerId;

    private Long productId;

    private Long orderItemId;

    @Column(nullable = false)
    private Integer rating;

    private String comment;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
