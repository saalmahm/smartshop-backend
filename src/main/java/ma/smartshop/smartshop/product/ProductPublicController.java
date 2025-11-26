package ma.smartshop.smartshop.product;

import lombok.RequiredArgsConstructor;
import ma.smartshop.smartshop.product.dto.ProductResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductPublicController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<Page<ProductResponseDto>> getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name
    ) {
        Page<ProductResponseDto> result = productService.getProductsPage(page, size, name);
        return ResponseEntity.ok(result);
    }
}