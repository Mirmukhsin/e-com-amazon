package org.ecomapp.productMS.repositories;

import org.ecomapp.productMS.dtos.response.ProductResponseDTO;
import org.ecomapp.productMS.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findByNameContainingIgnoreCase(String keyword, Pageable pageable);

    Page<Product> findAllBySeller_Id(Long sellerId, Pageable pageable);

    Page<Product> findAllByCategory_Id(Long categoryId, Pageable pageable);

    Page<Product> findAllByNameContainingIgnoreCase(String name, Pageable pageable);
}
