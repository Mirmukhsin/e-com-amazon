package org.ecomapp.userservice.services.sellerService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.ecomapp.userservice.dtos.request.SellerRequestDTO;
import org.ecomapp.userservice.dtos.response.SellerResponseDTO;
import org.ecomapp.userservice.exceptionHandling.customExceptions.ConflictException;
import org.ecomapp.userservice.exceptionHandling.customExceptions.ResourceNotFoundException;
import org.ecomapp.userservice.models.Role;
import org.ecomapp.userservice.models.SellerProfile;
import org.ecomapp.userservice.models.User;
import org.ecomapp.userservice.repositories.RoleRepository;
import org.ecomapp.userservice.repositories.SellerRepository;
import org.ecomapp.userservice.repositories.UserRepository;
import org.ecomapp.userservice.utility.SellerMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SellerServiceImpl implements SellerService {

    private final SellerRepository sellerRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final SellerMapper sellerMapper;

    @Override
    public SellerResponseDTO getSeller(Long userId) {
        SellerProfile seller = sellerRepository.findByUserId(userId).orElseThrow(() -> new ResourceNotFoundException("Seller not found"));
        return sellerMapper.sellerToSellerResDto(seller);
    }

    @Transactional
    @Override
    public SellerResponseDTO createSeller(Long userId, SellerRequestDTO dto) {

        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Boolean alreadyExists = sellerRepository.existsByUserId(userId);
        if (alreadyExists) {
            throw new ConflictException("Seller profile already exists for this user");
        } else {
            SellerProfile seller = sellerMapper.sellerReqDtoToSeller(dto);

            Role buyerRole = roleRepository.findByName("BUYER").orElseThrow(() -> new ResourceNotFoundException("BUYER role not found"));
            Role sellerRole = roleRepository.findByName("SELLER").orElseThrow(() -> new ResourceNotFoundException("SELLER role not found"));

            user.setRoles(new ArrayList<>(List.of(buyerRole, sellerRole)));

            userRepository.save(user);

            seller.setUser(user);
            seller.setAverageRating(0.0);
            seller.setTotalSales(0);

            sellerRepository.save(seller);

            return sellerMapper.sellerToSellerResDto(seller);
        }
    }

    @Override
    public SellerResponseDTO updateSeller(Long userId, SellerRequestDTO dto) {
        SellerProfile seller = sellerRepository.findByUserId(userId).orElseThrow(() -> new ResourceNotFoundException("Seller not found"));

        if (dto.getStoreName() != null && !dto.getStoreName().isBlank()) {
            seller.setStoreName(dto.getStoreName());
        }
        if (dto.getStoreDescription() != null && !dto.getStoreDescription().isBlank()) {
            seller.setStoreDescription(dto.getStoreDescription());
        }

        sellerRepository.save(seller);
        return sellerMapper.sellerToSellerResDto(seller);
    }

    @Override
    public void updateTotalSales(Long sellerId, Integer totalSales) {
        SellerProfile seller = sellerRepository.findByUserId(sellerId).orElseThrow(() -> new ResourceNotFoundException("Seller not found"));
        seller.setTotalSales(totalSales);
        sellerRepository.save(seller);
    }
}
