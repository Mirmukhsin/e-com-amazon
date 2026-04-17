package org.ecomapp.cartservice.repository;

import org.ecomapp.cartservice.models.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    boolean existsByUserId(Long userId);
}
