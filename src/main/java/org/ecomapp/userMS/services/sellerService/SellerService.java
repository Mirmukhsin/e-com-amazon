package org.ecomapp.userMS.services.sellerService;

import org.ecomapp.userMS.dtos.request.SellerRequestDTO;
import org.ecomapp.userMS.dtos.response.SellerResponseDTO;

public interface SellerService {

    SellerResponseDTO getSeller();
    SellerResponseDTO createSeller(SellerRequestDTO dto);
    SellerResponseDTO updateSeller(SellerRequestDTO dto);
}
