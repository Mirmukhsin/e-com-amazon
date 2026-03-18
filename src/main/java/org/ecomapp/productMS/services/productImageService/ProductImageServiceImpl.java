package org.ecomapp.productMS.services.productImageService;

import lombok.RequiredArgsConstructor;
import org.ecomapp.exceptionHandling.customExceptions.ResourceNotFoundException;
import org.ecomapp.productMS.dtos.request.ProductImageRequestDTO;
import org.ecomapp.productMS.dtos.response.ProductImageResponseDTO;
import org.ecomapp.productMS.models.Product;
import org.ecomapp.productMS.models.ProductImage;
import org.ecomapp.productMS.repositories.ProductImageRepository;
import org.ecomapp.productMS.repositories.ProductRepository;
import org.ecomapp.productMS.utility.ProductMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductImageServiceImpl implements ProductImageService {
    private final ProductImageRepository productImageRepository;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public List<ProductImageResponseDTO> getProductImages(Long productId) {
        return productImageRepository.findAllByProduct_Id(productId).stream().map(productMapper::productImgToProductImgResDto).toList();
    }

    @Override
    public ProductImageResponseDTO addImage(Long productId, ProductImageRequestDTO productImageRequestDTO) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        ProductImage productImage = ProductImage.builder()
                .product(product)
                .imageURL(productImageRequestDTO.getImageURL())
                .sortOrder(productImageRequestDTO.getSortOrder())
                .build();

        productImageRepository.save(productImage);

        return productMapper.productImgToProductImgResDto(productImage);
    }

    @Override
    public ProductImageResponseDTO updateSortOrder(Long imageId, Integer sortOrder) {
        ProductImage productImage = productImageRepository.findById(imageId).orElseThrow(() -> new ResourceNotFoundException("Product image not found"));

        productImage.setSortOrder(sortOrder);
        productImageRepository.save(productImage);

        return productMapper.productImgToProductImgResDto(productImage);
    }

    @Override
    public void deleteImage(Long imageId) {
        ProductImage productImage = productImageRepository.findById(imageId).orElseThrow(() -> new ResourceNotFoundException("Product image not found"));
        productImageRepository.delete(productImage);
    }
}
