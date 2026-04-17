package org.ecomapp.productservice.services.productImageService;

import lombok.RequiredArgsConstructor;
import org.ecomapp.productservice.exceptionHandling.customExceptions.ResourceNotFoundException;
import org.ecomapp.productservice.dtos.request.ProductImageRequestDTO;
import org.ecomapp.productservice.dtos.response.ProductImageResponseDTO;
import org.ecomapp.productservice.models.Product;
import org.ecomapp.productservice.models.ProductImage;
import org.ecomapp.productservice.repositories.ProductImageRepository;
import org.ecomapp.productservice.repositories.ProductRepository;
import org.ecomapp.productservice.utility.ProductMapper;
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
        return productImageRepository.findAllByProduct_Id(productId).stream().map(productMapper::productImgResDTOFromProductImg).toList();
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

        return productMapper.productImgResDTOFromProductImg(productImage);
    }

    @Override
    public ProductImageResponseDTO updateSortOrder(Long imageId, Integer sortOrder) {
        ProductImage productImage = productImageRepository.findById(imageId).orElseThrow(() -> new ResourceNotFoundException("Product image not found"));

        productImage.setSortOrder(sortOrder);
        productImageRepository.save(productImage);

        return productMapper.productImgResDTOFromProductImg(productImage);
    }

    @Override
    public void deleteImage(Long imageId) {
        ProductImage productImage = productImageRepository.findById(imageId).orElseThrow(() -> new ResourceNotFoundException("Product image not found"));
        productImageRepository.delete(productImage);
    }
}
