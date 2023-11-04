package com.slukovskyi.bidorama.services.impl;

import com.slukovskyi.bidorama.dtos.AuctionResponseDto;
import com.slukovskyi.bidorama.dtos.BidResponseDto;
import com.slukovskyi.bidorama.dtos.ProductRequestDto;
import com.slukovskyi.bidorama.exceptions.AlreadyExistsException;
import com.slukovskyi.bidorama.exceptions.NotFoundException;
import com.slukovskyi.bidorama.mappers.AuctionResponseDtoMapper;
import com.slukovskyi.bidorama.mappers.ProductRequestDtoMapper;
import com.slukovskyi.bidorama.models.Auction;
import com.slukovskyi.bidorama.models.Bid;
import com.slukovskyi.bidorama.models.Product;
import com.slukovskyi.bidorama.models.User;
import com.slukovskyi.bidorama.models.enums.AuctionStatus;
import com.slukovskyi.bidorama.repositories.AuctionRepository;
import com.slukovskyi.bidorama.services.AuctionService;
import com.slukovskyi.bidorama.services.BidService;
import com.slukovskyi.bidorama.services.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuctionServiceImpl implements AuctionService {

    private final AuctionRepository auctionRepository;
    private final UserService userService;
    private final BidService bidService;
    private final AuctionResponseDtoMapper auctionResponseDtoMapper;
    private final ProductRequestDtoMapper productRequestDtoMapper;

    @Override
    public AuctionResponseDto registerCurrentUser(Long id) {
        User currentUser = userService.getCurrentUser();
        Auction auction = auctionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Auction with id '%s' does not exist", id)));

        auction.getRegisteredUsers().add(currentUser);
        Auction savedAuction = auctionRepository.save(auction);
        return auctionResponseDtoMapper.auctionToAuctionResponseDto(savedAuction, currentUser);
    }

    @Override
    public AuctionResponseDto unregisterCurrentUser(Long id) {
        User currentUser = userService.getCurrentUser();
        Auction auction = auctionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Auction with id '%s' does not exist", id)));

        auction.getRegisteredUsers().remove(currentUser);
        Auction savedAuction = auctionRepository.save(auction);
        return auctionResponseDtoMapper.auctionToAuctionResponseDto(savedAuction, currentUser);
    }

    @Override
    public AuctionResponseDto updateStatus(Long id, AuctionStatus status) {
        User currentUser = userService.getCurrentUser();
        Auction auction = auctionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Auction with id '%s' does not exist", id)));

        AuctionStatus prevStatus = auction.getStatus();

        if (prevStatus.equals(AuctionStatus.ACTIVE) && status.equals(AuctionStatus.WAITING)) {
            this.returnFunds(auction);
        }

        if (prevStatus.equals(AuctionStatus.DELIVERING) && status.equals(AuctionStatus.FINISHED)) {
            User author = auction.getProduct().getAuthor();
            BidResponseDto lastBid = bidService.getLastBidOfAuction(auction);
            author.setBalance(author.getBalance() + lastBid.getSize());
        }

        auction.setStatus(status);
        Auction savedAuction = auctionRepository.save(auction);
        return auctionResponseDtoMapper.auctionToAuctionResponseDto(savedAuction, currentUser);
    }

    public void returnFunds(Auction auction) {
        Map<User, Bid> bidsToReturn = auction.getBids().stream()
                .collect(Collectors.toMap(Bid::getUser, Function.identity(),
                        BinaryOperator.maxBy(Comparator.comparingDouble(Bid::getSize))));

        for (Map.Entry<User, Bid> entry : bidsToReturn.entrySet()) {
            User user = entry.getKey();
            Bid bid = entry.getValue();
            if (!user.getId().equals(bidService.getLastBidOfAuction(auction).getUser().getId())) {
                userService.deposit(bid.getSize());
            }
        }
    }

    @Override
    public AuctionResponseDto getById(Long id) {
        User currentUser = userService.getCurrentUser();
        Auction auction = auctionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Auction with id '%s' does not exist", id)));

        return auctionResponseDtoMapper.auctionToAuctionResponseDto(auction, currentUser);
    }

    @Override
    public AuctionResponseDto getByProductId(Long productId) {
        Auction auction = auctionRepository.getByProduct_Id(productId)
                .orElseThrow(() -> new NotFoundException(String.format("Auction for product with id '%s' does not exist", productId)));

        User currentUser = userService.getCurrentUser();
        return auctionResponseDtoMapper.auctionToAuctionResponseDto(auction, currentUser);
    }

    @Override
    @Transactional
    public void updateAuctionsStatus() {
        List<Auction> auctions = auctionRepository.findAll();
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        auctions.stream().filter(auction -> auction.getStatus().equals(AuctionStatus.CREATED)
                        && auction.getStartTime().compareTo(currentTime) <= 0)
                .forEach(auction -> {
                    auction.setStatus(AuctionStatus.ACTIVE);
                    auctionRepository.save(auction);
                });

        auctions.stream().filter(auction -> auction.getStatus().equals(AuctionStatus.ACTIVE)
                        && auction.getEndTime().compareTo(currentTime) <= 0)
                .forEach(auction -> {
                    if (auction.getBids().isEmpty()) {
                        auction.setStatus(AuctionStatus.CANCELED);
                    }
                    else {
                        auction.setStatus(AuctionStatus.WAITING);
                    }
                    auctionRepository.save(auction);
                });
    }

    @Override
    public AuctionResponseDto createAuctionForProduct(Product savedProduct, ProductRequestDto productRequestDto) {
        User currentUser = userService.getCurrentUser();
        if (auctionRepository.existsByProduct_Id(savedProduct.getId())) {
            throw new AlreadyExistsException("Auction for that product already exists");
        }

        Auction auction = productRequestDtoMapper.productRequestDtoToAuction(productRequestDto);
        auction.setStatus(AuctionStatus.CREATED);
        auction.setProduct(savedProduct);

        Auction savedAuction = auctionRepository.save(auction);
        return auctionResponseDtoMapper.auctionToAuctionResponseDto(savedAuction, currentUser);
    }

    @Override
    public AuctionResponseDto updateAuctionForProduct(Product savedProduct, ProductRequestDto productRequestDto) {
        User currentUser = userService.getCurrentUser();
        Auction auction = auctionRepository.findById(savedProduct.getAuction().getId())
                .orElseThrow(() -> new NotFoundException(String.format("Auction with id '%s' does not exist",
                        savedProduct.getAuction().getId())));

        Auction updatedAuction = productRequestDtoMapper.updateAuctionFromProductRequestDto(auction, productRequestDto);
        updatedAuction.setProduct(savedProduct);
        Auction savedAuction = auctionRepository.save(updatedAuction);
        return auctionResponseDtoMapper.auctionToAuctionResponseDto(savedAuction, currentUser);
    }

}
