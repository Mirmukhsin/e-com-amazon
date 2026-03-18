package org.ecomapp.userMS.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
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

    private Long totalSales;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
