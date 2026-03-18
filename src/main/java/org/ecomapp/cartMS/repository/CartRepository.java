package org.ecomapp.cartMS.repository;

import org.ecomapp.cartMS.models.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    boolean existsByUser_Id(Long userId);
}
