package org.ecomapp.userMS.models;

import jakarta.persistence.*;
import lombok.*;
import org.ecomapp.userMS.enums.Label;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private Label label;
    private String street;
    private String city;
    private String state;
    private String zip;
    private String country;
    private Boolean isDefault;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
