package com.slukovskyi.bidorama.dtos;

import lombok.Data;

import java.util.List;

@Data
public class ProductResponseDto {
    private Long id;
    private String name;
    private CategoryResponseDto category;
    private String description;
    private List<String> imageFilenames;
    private UserResponseDto author;
    private String creationTime;
    private AuctionResponseDto auction;
}
