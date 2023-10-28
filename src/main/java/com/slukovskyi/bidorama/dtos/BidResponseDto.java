package com.slukovskyi.bidorama.dtos;

import lombok.Data;

@Data
public class BidResponseDto {
    private Long id;
    private UserResponseDto user;
    private String creationTime;
    private Double size;
}
