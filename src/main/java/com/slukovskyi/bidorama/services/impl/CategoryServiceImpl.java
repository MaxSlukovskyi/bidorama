package com.slukovskyi.bidorama.services.impl;

import com.slukovskyi.bidorama.dtos.CategoryDto;
import com.slukovskyi.bidorama.dtos.CategoryResponseDto;
import com.slukovskyi.bidorama.mappers.CategoryResponseDtoMapper;
import com.slukovskyi.bidorama.models.Category;
import com.slukovskyi.bidorama.repositories.CategoryRepository;
import com.slukovskyi.bidorama.services.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService{

    @Value("${upload.path}")
    private String uploadPath;

    private final CategoryRepository categoryRepository;
    private final CategoryResponseDtoMapper categoryResponseDtoMapper;

    @Override
    public CategoryResponseDto add(CategoryDto categoryDto) throws IOException {
        Category category = new Category();
        category.setName(categoryDto.getName());

        if (categoryDto.getImage() != null && !categoryDto.getImage().getOriginalFilename().isEmpty()) {
            File uploadDir = new File(uploadPath);

            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }

            String uuidFile = UUID.randomUUID().toString();
            String resultFilename = uuidFile + categoryDto.getImage().getOriginalFilename();

            categoryDto.getImage().transferTo(new File(uploadPath + "/" + resultFilename));

            category.setImageFilename(resultFilename);
        }

        Category savedCategory = categoryRepository.save(category);
        return categoryResponseDtoMapper.categoryToCategoryResponseDto(savedCategory);
    }

    @Override
    public List<CategoryResponseDto> getAll() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream().map(categoryResponseDtoMapper::categoryToCategoryResponseDto).toList();
    }
}
