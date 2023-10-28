package com.slukovskyi.bidorama.mappers;

import com.slukovskyi.bidorama.dtos.AuctionResponseDto;
import com.slukovskyi.bidorama.models.Auction;
import com.slukovskyi.bidorama.models.Bid;
import com.slukovskyi.bidorama.models.User;
import com.slukovskyi.bidorama.models.enums.AuctionStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.sql.Timestamp;
import java.util.Comparator;
import java.util.List;

@Mapper(componentModel = "spring", uses = {UserResponseDtoMapper.class, BidResponseDtoMapper.class})
public interface AuctionResponseDtoMapper {

    @Mapping(source = "auction.id", target = "id")
    @Mapping(source = "auction.registeredUsers", target = "registeredUsersNumber", qualifiedByName = "getRegisteredUsersNumber")
    @Mapping(source = "auction.product.author", target = "author")
    @Mapping(source = "auction.startTime", target = "secondsToStart", qualifiedByName = "getSecondsToStart")
    @Mapping(source = "auction.endTime", target = "secondsToEnd", qualifiedByName = "getSecondsToEnd")
    @Mapping(source = "auction.bids", target = "lastBid", qualifiedByName = "getLastBid")
    @Mapping(target = "isRegistered", expression = "java(getIsRegistered(auction, currentUser))")
    @Mapping(target = "isAuthor", expression = "java(getIsAuthor(auction, currentUser))")
    @Mapping(target = "isWinner", expression = "java(getIsWinner(auction, currentUser))")
    AuctionResponseDto auctionToAuctionResponseDto(Auction auction, User currentUser);

    @Named("getRegisteredUsersNumber")
    static Integer getRegisteredUsersNumber(List<User> registeredUsers) {
        return registeredUsers == null ? 0 : registeredUsers.size();
    }

    @Named("getSecondsToStart")
    static Integer getSecondsToStart(Timestamp startTime) {
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        long milliseconds = startTime.getTime() - currentTime.getTime();
        int seconds = (int) milliseconds / 1000;
        return seconds;
    }

    @Named("getSecondsToEnd")
    static Integer getSecondsToEnd(Timestamp endTime) {
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        long milliseconds = endTime.getTime() - currentTime.getTime();
        return (int) milliseconds / 1000;
    }

    @Named("getLastBid")
    static Bid getLastBid(List<Bid> bids) {
        return bids != null ? bids.stream().max(Comparator.comparing(Bid::getSize)).orElse(null) : null;
    }

    default Boolean getIsRegistered(Auction auction, User currentUser) {
        return auction.getRegisteredUsers().contains(currentUser);
    }

    default Boolean getIsAuthor(Auction auction, User currentUser) {
        return auction.getProduct().getAuthor().equals(currentUser);
    }

    default Boolean getIsWinner(Auction auction, User currentUser) {
        Bid lastBid = auction.getBids() != null ? auction.getBids().stream().max(Comparator.comparing(Bid::getSize))
                .orElse(null) : null;
        return lastBid != null && ( !auction.getStatus().equals(AuctionStatus.CREATED)
                && !auction.getStatus().equals(AuctionStatus.ACTIVE)
                && !auction.getStatus().equals(AuctionStatus.CANCELED))
                && lastBid.getUser().getId().equals(currentUser.getId());
    }

}
