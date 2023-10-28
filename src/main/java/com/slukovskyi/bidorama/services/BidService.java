package com.slukovskyi.bidorama.services;

import com.slukovskyi.bidorama.dtos.BidRequestDto;
import com.slukovskyi.bidorama.dtos.BidResponseDto;
import com.slukovskyi.bidorama.models.Auction;
import com.slukovskyi.bidorama.models.Bid;
import com.slukovskyi.bidorama.models.User;

public interface BidService {

    BidResponseDto makeBid(BidRequestDto bidRequestDto);

    BidResponseDto getLastBidOfAuction(Auction auction);

    boolean isPossibleToMakeBid(BidRequestDto bidRequestDto, Auction auction, BidResponseDto lastBid, User currentUser);

    void makeWithdrawal(BidRequestDto bidRequestDto, BidResponseDto lastBid);
}
