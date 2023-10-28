package com.slukovskyi.bidorama.controllers;

import com.slukovskyi.bidorama.dtos.AuctionResponseDto;
import com.slukovskyi.bidorama.models.enums.AuctionStatus;
import com.slukovskyi.bidorama.services.AuctionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auctions/")
@RequiredArgsConstructor
public class AuctionController {

    private final AuctionService auctionService;

    @PutMapping("register/{id}")
    public ResponseEntity<AuctionResponseDto> registerCurrentUser(@PathVariable(value = "id") Long id) {
        AuctionResponseDto auctionResponseDto = auctionService.registerCurrentUser(id);
        if (auctionResponseDto == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(auctionResponseDto, HttpStatus.OK);
    }

    @PutMapping("unregister/{id}")
    public ResponseEntity<AuctionResponseDto> unregisterCurrentUser(@PathVariable(value = "id") Long id) {
        AuctionResponseDto auctionResponseDto = auctionService.unregisterCurrentUser(id);
        if (auctionResponseDto == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(auctionResponseDto, HttpStatus.OK);
    }

    @PutMapping("update/status")
    public ResponseEntity<AuctionResponseDto> updateStatus(@RequestParam("id") Long id,
                                                           @RequestParam("status") AuctionStatus status) {
        AuctionResponseDto auctionResponseDto = auctionService.updateStatus(id, status);
        if (auctionResponseDto == null) {
            return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        }
        return new ResponseEntity<>(auctionResponseDto, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuctionResponseDto> getById(@PathVariable(value = "id") Long id) {
        AuctionResponseDto auctionResponseDto = auctionService.getById(id);
        return new ResponseEntity<>(auctionResponseDto, HttpStatus.OK);
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<AuctionResponseDto> getByProductId(@PathVariable(value = "id") Long id) {
        AuctionResponseDto auctionResponseDto = auctionService.getByProductId(id);
        return new ResponseEntity<>(auctionResponseDto, HttpStatus.OK);
    }
}
