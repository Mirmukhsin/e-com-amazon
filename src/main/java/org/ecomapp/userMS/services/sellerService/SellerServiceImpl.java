package org.ecomapp.userMS.services.sellerService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.ecomapp.exceptionHandling.customExceptions.ConflictException;
import org.ecomapp.exceptionHandling.customExceptions.ResourceNotFoundException;
import org.ecomapp.securityMS.utility.SecurityUtils;
import org.ecomapp.userMS.dtos.request.SellerRequestDTO;
import org.ecomapp.userMS.dtos.response.SellerResponseDTO;
import org.ecomapp.userMS.models.Role;
import org.ecomapp.userMS.models.SellerProfile;
import org.ecomapp.userMS.models.User;
import org.ecomapp.userMS.repositories.RoleRepository;
import org.ecomapp.userMS.repositories.SellerRepository;
import org.ecomapp.userMS.repositories.UserRepository;
import org.ecomapp.userMS.utility.SellerMapper;
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
    private final SecurityUtils securityUtils;

    @Override
    public SellerResponseDTO getSeller() {
        SellerProfile seller = sellerRepository.findByUserId(securityUtils.getCurrentUserId()).orElseThrow(() -> new ResourceNotFoundException("Seller not found"));
        return sellerMapper.sellerToSellerResDto(seller);
    }

    @Transactional
    @Override
    public SellerResponseDTO createSeller(SellerRequestDTO dto) {
        Long currentUserId = securityUtils.getCurrentUserId();

        User user = userRepository.findById(currentUserId).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Boolean alreadyExists = sellerRepository.existsByUserId(currentUserId);
        if (alreadyExists) {
            throw new ConflictException("Seller profile already exists for this user");
        } else {
            SellerProfile seller = sellerMapper.sellerReqDtoToSeller(dto);

            Role buyerRole = roleRepository.findByName("BUYER").orElseThrow(() -> new ResourceNotFoundException("BUYER role not found"));
            Role sellerRole = roleRepository.findByName("SELLER").orElseThrow(() -> new ResourceNotFoundException("SELLER role not found"));

            user.setRoles(new ArrayList<>(List.of(buyerRole, sellerRole)));

            userRepository.save(user);

            seller.setUser(user);

            sellerRepository.save(seller);

            return sellerMapper.sellerToSellerResDto(seller);
        }
    }

    @Override
    public SellerResponseDTO updateSeller(SellerRequestDTO dto) {
        SellerProfile seller = sellerRepository.findByUserId(securityUtils.getCurrentUserId()).orElseThrow(() -> new ResourceNotFoundException("Seller not found"));

        seller.setStoreName(dto.getStoreName());
        seller.setStoreDescription(dto.getStoreDescription());

        sellerRepository.save(seller);
        return sellerMapper.sellerToSellerResDto(seller);
    }
}
