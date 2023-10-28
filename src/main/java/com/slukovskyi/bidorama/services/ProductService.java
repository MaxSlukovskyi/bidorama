package com.slukovskyi.bidorama.services;

import com.slukovskyi.bidorama.dtos.ProductRequestDto;
import com.slukovskyi.bidorama.dtos.ProductResponseDto;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

public interface ProductService {

    ProductResponseDto add(ProductRequestDto productRequestDto) throws IOException, ParseException;

    ProductResponseDto update(ProductRequestDto productRequestDto) throws IOException, ParseException;

    List<ProductResponseDto> getAll();

    List<ProductResponseDto> getAllByCategoryId(Long categoryId);

    List<ProductResponseDto> getAllByCurrentUser();

    List<ProductResponseDto> getAllRegisteredByCurrentUser();

    ProductResponseDto getById(Long id);

    void delete(Long id);

}
