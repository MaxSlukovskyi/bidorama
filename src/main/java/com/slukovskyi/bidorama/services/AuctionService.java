package com.slukovskyi.bidorama.services;

import com.slukovskyi.bidorama.dtos.AuctionResponseDto;
import com.slukovskyi.bidorama.dtos.ProductRequestDto;
import com.slukovskyi.bidorama.models.Product;
import com.slukovskyi.bidorama.models.enums.AuctionStatus;

import java.text.ParseException;

public interface AuctionService {
    AuctionResponseDto registerCurrentUser(Long id);

    AuctionResponseDto unregisterCurrentUser(Long id);

    AuctionResponseDto updateStatus(Long id, AuctionStatus status);


    AuctionResponseDto getById(Long id);

    AuctionResponseDto getByProductId(Long id);

    void updateAuctionsStatus();

    AuctionResponseDto createAuctionForProduct(Product savedProduct, ProductRequestDto productRequestDto) throws ParseException;

    AuctionResponseDto updateAuctionForProduct(Product savedProduct, ProductRequestDto productRequestDto) throws ParseException;

}
