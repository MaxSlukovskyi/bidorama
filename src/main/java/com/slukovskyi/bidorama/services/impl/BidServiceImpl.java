package com.slukovskyi.bidorama.services.impl;

import com.slukovskyi.bidorama.dtos.BidRequestDto;
import com.slukovskyi.bidorama.dtos.BidResponseDto;
import com.slukovskyi.bidorama.exceptions.NotFoundException;
import com.slukovskyi.bidorama.exceptions.NullReferenceException;
import com.slukovskyi.bidorama.exceptions.UnableToBidException;
import com.slukovskyi.bidorama.mappers.BidResponseDtoMapper;
import com.slukovskyi.bidorama.models.Auction;
import com.slukovskyi.bidorama.models.Bid;
import com.slukovskyi.bidorama.models.User;
import com.slukovskyi.bidorama.repositories.AuctionRepository;
import com.slukovskyi.bidorama.repositories.BidRepository;
import com.slukovskyi.bidorama.services.BidService;
import com.slukovskyi.bidorama.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Comparator;

@Service
@RequiredArgsConstructor
public class BidServiceImpl implements BidService {

    private final AuctionRepository auctionRepository;
    private final UserService userService;
    private final BidRepository bidRepository;
    private final BidResponseDtoMapper bidResponseDtoMapper;

    @Override
    public BidResponseDto makeBid(BidRequestDto bidRequestDto) {
        if (bidRequestDto == null) {
            throw new NullReferenceException("Attempt to use a null bid object");
        }

        Auction auction = auctionRepository.findById(bidRequestDto.getAuctionId())
                .orElseThrow(() -> new NotFoundException(String.format("Auction with id '%s' does not exist",
                        bidRequestDto.getAuctionId())));

        User currentUser = userService.getCurrentUser();
        BidResponseDto lastBid = this.getLastBidOfAuction(auction);

        if (!isPossibleToMakeBid(bidRequestDto, auction, lastBid, currentUser)) {
            throw new UnableToBidException("User are not able to make a bid");
        }

        userService.withdraw(bidRequestDto.getSize());

        Bid bid = new Bid();
        bid.setAuction(auction);
        bid.setCreationTime(new Timestamp(System.currentTimeMillis()));
        bid.setSize(bidRequestDto.getSize());
        bid.setUser(currentUser);

        Bid savedBid = bidRepository.save(bid);

        return bidResponseDtoMapper.bidToBidResponseDto(savedBid);
    }

    @Override
    public BidResponseDto getLastBidOfAuction(Auction auction) {
        if (auction == null) {
            throw new NullReferenceException("Attempt to use a null auction object");
        }

        Bid lastBid = auction.getBids()
                .stream().max(Comparator.comparing(Bid::getSize)).orElse(null);

        if (lastBid != null) {
            return bidResponseDtoMapper.bidToBidResponseDto(lastBid);
        }
        return null;
    }

    @Override
    public boolean isPossibleToMakeBid(BidRequestDto bidRequestDto, Auction auction, BidResponseDto lastBid, User currentUser) {
        Double maxBid = this.getMaxBidByUserAndAuction(currentUser, auction);
        return (lastBid == null && (currentUser.getBalance() >= auction.getStartBid()
                && bidRequestDto.getSize() >= auction.getStartBid())) ||
                (lastBid != null && (currentUser.getBalance() + maxBid >= bidRequestDto.getSize()) &&
                        (bidRequestDto.getSize() - lastBid.getSize() >= auction.getMinimalStep()));
    }

    @Override
    public Double getMaxBidByUserAndAuction(User user, Auction auction) {
        Bid maxBid = auction.getBids().stream().filter(bid -> bid.getUser().getId().equals(user.getId()))
                .max(Comparator.comparing(Bid::getSize)).orElse(null);
        return maxBid != null ? maxBid.getSize() : 0.0;
    }
}
