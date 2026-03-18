package org.ecomapp.userMS.repositories;

import org.ecomapp.userMS.models.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    Optional<Address> findByUserId(Long userId);

    List<Address> findAllByUserId(Long userId);

    Optional<Address> findByUserIdAndIsDefault(Long userId, Boolean isDefault);

}
