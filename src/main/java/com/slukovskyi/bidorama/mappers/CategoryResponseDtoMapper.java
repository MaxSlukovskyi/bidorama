package com.slukovskyi.bidorama.mappers;

import com.slukovskyi.bidorama.dtos.CategoryResponseDto;
import com.slukovskyi.bidorama.models.Category;
import com.slukovskyi.bidorama.models.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryResponseDtoMapper {

    @Mapping(source = "products", target = "numberOfProducts", qualifiedByName = "getNumberOfProducts")
    CategoryResponseDto categoryToCategoryResponseDto(Category category);

    @Named("getNumberOfProducts")
    static Integer getNumberOfProducts(List<Product> products) {
        return products == null ? 0 : products.size();
    }
}
