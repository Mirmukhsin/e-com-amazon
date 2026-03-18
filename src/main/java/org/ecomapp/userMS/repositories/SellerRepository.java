package org.ecomapp.userMS.repositories;

import org.ecomapp.userMS.models.SellerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SellerRepository extends JpaRepository<SellerProfile, Long> {

    Optional<SellerProfile> findByUserId(Long userId);

    Boolean existsByUserId(Long userId);
}
