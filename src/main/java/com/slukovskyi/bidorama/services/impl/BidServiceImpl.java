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
        Auction auction = auctionRepository.findById(bidRequestDto.getAuctionId())
                .orElseThrow(() -> new NotFoundException(String.format("Auction with id '%s' does not exist",
                        bidRequestDto.getAuctionId())));

        User currentUser = userService.getCurrentUser();
        BidResponseDto lastBid = this.getLastBidOfAuction(auction);

        if (!isPossibleToMakeBid(bidRequestDto, auction, lastBid, currentUser)) {
            throw new UnableToBidException("User are not able to make a bid");
        }

        makeWithdrawal(bidRequestDto, lastBid);

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
        return auction != null && (lastBid == null || (!(currentUser.getBalance() < bidRequestDto.getSize() - lastBid.getSize())
                && !(bidRequestDto.getSize() - lastBid.getSize() < auction.getMinimalStep())))
                && (lastBid != null || currentUser.getBalance() >= auction.getStartBid());
    }

    @Override
    public void makeWithdrawal(BidRequestDto bidRequestDto, BidResponseDto lastBid) {
        if (lastBid == null) {
            userService.withdraw(bidRequestDto.getSize());
        }
        else {
            userService.withdraw(bidRequestDto.getSize() - lastBid.getSize());
        }
    }

}
