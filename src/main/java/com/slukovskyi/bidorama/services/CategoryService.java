package com.slukovskyi.bidorama.services;

import com.slukovskyi.bidorama.dtos.CategoryDto;
import com.slukovskyi.bidorama.dtos.CategoryResponseDto;

import java.io.IOException;
import java.util.List;

public interface CategoryService {

    CategoryResponseDto add(CategoryDto categoryDto) throws IOException;

    List<CategoryResponseDto> getAll();
}
