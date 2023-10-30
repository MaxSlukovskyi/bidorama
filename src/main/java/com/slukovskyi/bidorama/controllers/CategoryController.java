package com.slukovskyi.bidorama.controllers;

import com.slukovskyi.bidorama.dtos.CategoryDto;
import com.slukovskyi.bidorama.dtos.CategoryResponseDto;
import com.slukovskyi.bidorama.services.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/categories/")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("add")
    public ResponseEntity<CategoryResponseDto> add(@RequestParam("name") String name,
                                                   @RequestParam("image") MultipartFile image) throws IOException {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setName(name);
        categoryDto.setImage(image);
        CategoryResponseDto responseDto = categoryService.add(categoryDto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponseDto>> getAll() {
        List<CategoryResponseDto> categories = categoryService.getAll();
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }
}
