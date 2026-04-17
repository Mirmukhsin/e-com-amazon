package org.ecomapp.productservice.repositories;

import org.ecomapp.productservice.models.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {
    List<ProductVariant> findAllByProduct_Id(Long productId);

    List<ProductVariant> findByIdIn(Set<Long> variantIds);
}
