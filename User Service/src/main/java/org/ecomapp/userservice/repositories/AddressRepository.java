package org.ecomapp.userservice.repositories;

import org.ecomapp.userservice.models.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findAllByUserId(Long userId);

    Optional<Address> findByUserIdAndIsDefault(Long userId, Boolean isDefault);

}
