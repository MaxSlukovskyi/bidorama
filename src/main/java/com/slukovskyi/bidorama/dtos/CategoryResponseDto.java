package com.slukovskyi.bidorama.dtos;

import lombok.Data;

@Data
public class CategoryResponseDto {
    private Long id;
    private String name;
    private String imageFilename;
    private Integer numberOfProducts;
}
