package ma.smartshop.smartshop.product.mapper;

import ma.smartshop.smartshop.product.dto.ProductRequestDto;
import ma.smartshop.smartshop.product.dto.ProductResponseDto;
import ma.smartshop.smartshop.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    Product toEntity(ProductRequestDto dto);

    ProductResponseDto toResponseDto(Product entity);

    void updateEntityFromDto(ProductRequestDto dto, @MappingTarget Product entity);
}