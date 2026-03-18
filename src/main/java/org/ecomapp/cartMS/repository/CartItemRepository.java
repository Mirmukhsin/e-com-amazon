package org.ecomapp.cartMS.repository;

import org.ecomapp.cartMS.models.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findAllByCart_Id(Long cartId);

    Optional<CartItem> findByCart_IdAndProductVariant_Id(Long cartId, Long productVariantId);
}
