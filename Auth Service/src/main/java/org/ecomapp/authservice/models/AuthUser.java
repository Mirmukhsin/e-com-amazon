package org.ecomapp.authservice.models;

import jakarta.persistence.*;
import lombok.*;
import org.ecomapp.authservice.enums.UserStatus;

import java.util.List;

@Entity
@Table(name = "auth_users")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String password;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private List<Role> roles;
}
