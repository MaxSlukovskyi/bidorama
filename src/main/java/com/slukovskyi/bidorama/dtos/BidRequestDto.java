package com.slukovskyi.bidorama.dtos;

import lombok.Data;

@Data
public class BidRequestDto {
    private Long auctionId;
    private Double size;
}
