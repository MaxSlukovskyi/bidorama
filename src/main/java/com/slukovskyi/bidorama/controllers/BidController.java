package com.slukovskyi.bidorama.controllers;

import com.slukovskyi.bidorama.dtos.BidRequestDto;
import com.slukovskyi.bidorama.dtos.BidResponseDto;
import com.slukovskyi.bidorama.services.BidService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/bids/")
@RequiredArgsConstructor
public class BidController {

    private final BidService bidService;

    @PutMapping("make")
    public ResponseEntity<BidResponseDto> makeBid(@RequestBody BidRequestDto bidRequestDto) {
        BidResponseDto bidResponseDto = bidService.makeBid(bidRequestDto);
        return new ResponseEntity<>(bidResponseDto, HttpStatus.OK);
    }
}
