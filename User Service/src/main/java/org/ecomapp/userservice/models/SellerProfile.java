package org.ecomapp.userservice.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "seller_profiles")
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SellerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String storeName;

    private String storeDescription;

    private Double averageRating;

    private Integer totalSales;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
