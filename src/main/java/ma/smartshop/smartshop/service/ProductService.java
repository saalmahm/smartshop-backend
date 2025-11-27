package ma.smartshop.smartshop.service;

import lombok.RequiredArgsConstructor;
import ma.smartshop.smartshop.entity.Product;
import ma.smartshop.smartshop.product.dto.ProductRequestDto;
import ma.smartshop.smartshop.product.dto.ProductResponseDto;
import ma.smartshop.smartshop.product.mapper.ProductMapper;
import ma.smartshop.smartshop.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductResponseDto createProduct(ProductRequestDto dto) {
        Product product = productMapper.toEntity(dto);
        product.setDeleted(false);
        return productMapper.toResponseDto(productRepository.save(product));
    }

    public ProductResponseDto updateProduct(Long id, ProductRequestDto dto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        productMapper.updateEntityFromDto(dto, product);
        return productMapper.toResponseDto(productRepository.save(product));
    }

    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        product.setDeleted(true);
        productRepository.save(product);
    }

    public ProductResponseDto getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return productMapper.toResponseDto(product);
    }

    public List<ProductResponseDto> getAllProducts() {
        return productRepository.findByDeletedFalse().stream()
                .map(productMapper::toResponseDto)
                .toList();
    }

    public Page<ProductResponseDto> getProductsPage(int page, int size, String name) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Product> productPage;

        if (name != null && !name.isBlank()) {
            productPage = productRepository.findByDeletedFalseAndNameContainingIgnoreCase(name, pageable);
        } else {
            productPage = productRepository.findByDeletedFalse(pageable);
        }

        return productPage.map(productMapper::toResponseDto);
    }
}