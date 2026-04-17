package org.ecomapp.userservice.services.sellerService;

import org.ecomapp.userservice.dtos.request.SellerRequestDTO;
import org.ecomapp.userservice.dtos.response.SellerResponseDTO;

public interface SellerService {

    SellerResponseDTO getSeller(Long userId);

    SellerResponseDTO createSeller(Long userId, SellerRequestDTO dto);

    SellerResponseDTO updateSeller(Long userId, SellerRequestDTO dto);

    void updateTotalSales(Long sellerId, Integer totalSales);
}
