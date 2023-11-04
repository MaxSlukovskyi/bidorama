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
import com.slukovskyi.bidorama.services.UserService;
import com.slukovskyi.bidorama.util.ResourceDataReader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringJUnitConfig
@DisplayName("Bid service tests")
public class BidServiceImplTest {

    @InjectMocks
    private BidServiceImpl bidService;
    @Mock
    private AuctionRepository auctionRepository;
    @Mock
    private UserService userService;
    @Mock
    private BidRepository bidRepository;
    @Mock
    private BidResponseDtoMapper bidResponseDtoMapper;

    @Nested
    @DisplayName("#makeBid tests")
    class makeBidTests {

        @Value("classpath:data/unit-tests/entity/auction-from-db.json")
        private Resource auctionFromDBData;

        @Value("classpath:data/unit-tests/dto/bid-request-dto.json")
        private Resource bidRequestDtoData;

        @Value("classpath:data/unit-tests/dto/last-bid-response-dto.json")
        private Resource lastBidResponseDtoData;

        @Value("classpath:data/unit-tests/entity/user-from-db.json")
        private Resource userFromDBData;

        @Value("classpath:data/unit-tests/entity/saved-bid.json")
        private Resource savedBidData;

        @Value("classpath:data/unit-tests/dto/saved-bid-response-dto.json")
        private Resource savedBidResponseDtoData;

        @Value("classpath:data/unit-tests/entity/last-bid-from-db.json")
        private Resource lastBidFromDBData;

        @Test
        @DisplayName("Should throw NullReferenceException if bidRequestDto is null")
        void shouldThrowExceptionIfBidDtoIsNull() {
            //Given
            BidRequestDto bidRequestDto = null;

            //When - Then
            assertThrows(NullReferenceException.class, () -> bidService.makeBid(bidRequestDto));
        }

        @Test
        @DisplayName("Should throw NotFoundException if auction does not exist")
        void shouldThrowExceptionIfAuctionDoesNotExist() {
            //Given
            BidRequestDto bidRequestDto = ResourceDataReader.asObject(bidRequestDtoData, BidRequestDto.class);

            when(auctionRepository.findById(bidRequestDto.getAuctionId())).thenReturn(Optional.empty());

            //When - Then
            assertThrows(NotFoundException.class, () -> bidService.makeBid(bidRequestDto));
            verify(auctionRepository, times(1)).findById(bidRequestDto.getAuctionId());
        }

        @Test
        @DisplayName("Should throw UnableToBidException if it is not possible to make a bid")
        void shouldThrowExceptionIfNotPossibleToMakeBid() {
            //Given
            Auction auction = ResourceDataReader.asObject(auctionFromDBData, Auction.class);
            User currentUser = ResourceDataReader.asObject(userFromDBData, User.class);
            BidRequestDto bidRequestDto = ResourceDataReader.asObject(bidRequestDtoData, BidRequestDto.class);
            bidRequestDto.setSize(10.0);

            when(auctionRepository.findById(bidRequestDto.getAuctionId())).thenReturn(Optional.of(auction));
            when(userService.getCurrentUser()).thenReturn(currentUser);

            //When - Then
            assertThrows(UnableToBidException.class, () -> bidService.makeBid(bidRequestDto));
            verify(auctionRepository, times(1)).findById(bidRequestDto.getAuctionId());
            verify(userService, times(1)).getCurrentUser();
        }

        @Test
        @DisplayName("Should return bidResponseDto")
        void shouldReturnBidResponseDto() {
            //Given
            Auction auction = ResourceDataReader.asObject(auctionFromDBData, Auction.class);
            User currentUser = ResourceDataReader.asObject(userFromDBData, User.class);
            BidRequestDto bidRequestDto = ResourceDataReader.asObject(bidRequestDtoData, BidRequestDto.class);
            Bid savedBid = ResourceDataReader.asObject(savedBidData, Bid.class);
            BidResponseDto savedBidResponseDto = ResourceDataReader.asObject(savedBidResponseDtoData,
                    BidResponseDto.class);
            BidResponseDto lastBidResponseDto = ResourceDataReader.asObject(lastBidResponseDtoData, BidResponseDto.class);
            Bid lastBid = ResourceDataReader.asObject(lastBidFromDBData, Bid.class);
            currentUser.setBalance(350.0);

            when(bidResponseDtoMapper.bidToBidResponseDto(lastBid)).thenReturn(lastBidResponseDto);
            when(auctionRepository.findById(bidRequestDto.getAuctionId())).thenReturn(Optional.of(auction));
            when(userService.getCurrentUser()).thenReturn(currentUser);
            when(bidRepository.save(any(Bid.class))).thenReturn(savedBid);
            when(bidResponseDtoMapper.bidToBidResponseDto(savedBid)).thenReturn(savedBidResponseDto);

            //When
            BidResponseDto actualValue = bidService.makeBid(bidRequestDto);

            //Then
            assertEquals(savedBidResponseDto, actualValue);
            verify(auctionRepository, times(1)).findById(bidRequestDto.getAuctionId());
            verify(userService, times(1)).getCurrentUser();
            verify(userService, times(1)).withdraw(bidRequestDto.getSize());
            verify(bidRepository, times(1)).save(any(Bid.class));
            verify(bidResponseDtoMapper, times(1)).bidToBidResponseDto(savedBid);
        }
    }

    @Nested
    @DisplayName("#getLastBidOfAuction tests")
    class getLastBidOfAuction {

        @Value("classpath:data/unit-tests/dto/last-bid-response-dto.json")
        private Resource lastBidResponseDtoData;

        @Value("classpath:data/unit-tests/entity/last-bid-from-db.json")
        private Resource lastBidFromDBData;

        @Value("classpath:data/unit-tests/entity/auction-from-db.json")
        private Resource auctionFromDBData;

        @Test
        @DisplayName("Should throw NullReferenceException if auction is null")
        void shouldThrowExceptionIfAuctionIsNull() {
            //Given
            Auction auction = null;

            //When - Then
            assertThrows(NullReferenceException.class, () -> bidService.getLastBidOfAuction(auction));
        }

        @Test
        @DisplayName("Should return null if auction does not have any bids")
        void shouldReturnNullIfAuctionDoesNotHaveBids() {
            //Given
            Auction auction = ResourceDataReader.asObject(auctionFromDBData, Auction.class);
            auction.setBids(Collections.emptyList());
            BidResponseDto lastBidResponseDto = ResourceDataReader.asObject(lastBidResponseDtoData, BidResponseDto.class);

            //When
            BidResponseDto actualBidResponseDto = bidService.getLastBidOfAuction(auction);

            //Then
            assertNull(actualBidResponseDto);
        }

        @Test
        @DisplayName("Should return bidResponseDto")
        void shouldReturnBidResponseDto() {
            //Given
            Auction auction = ResourceDataReader.asObject(auctionFromDBData, Auction.class);
            BidResponseDto lastBidResponseDto = ResourceDataReader.asObject(lastBidResponseDtoData, BidResponseDto.class);
            Bid lastBid = ResourceDataReader.asObject(lastBidFromDBData, Bid.class);

            when(bidResponseDtoMapper.bidToBidResponseDto(lastBid)).thenReturn(lastBidResponseDto);

            //When
            BidResponseDto actualBidResponseDto = bidService.getLastBidOfAuction(auction);

            //Then
            assertEquals(lastBidResponseDto, actualBidResponseDto);
            verify(bidResponseDtoMapper, times(1)).bidToBidResponseDto(lastBid);
        }
    }

    @Nested
    @DisplayName("#isPossibleToMakeBid tests")
    class isPossibleToMakeBidTests {

        @Value("classpath:data/unit-tests/entity/auction-from-db.json")
        private Resource auctionFromDBData;

        @Value("classpath:data/unit-tests/dto/bid-request-dto.json")
        private Resource bidRequestDtoData;

        @Value("classpath:data/unit-tests/dto/last-bid-response-dto.json")
        private Resource lastBidResponseDtoData;

        @Value("classpath:data/unit-tests/entity/user-from-db.json")
        private Resource userFromDBData;

        @Test
        @DisplayName("Should return false if user has insufficient funds to perform action")
        void shouldReturnFalseIfUserHasInsufficientFunds() {
            //Given
            BidRequestDto bidRequestDto = ResourceDataReader.asObject(bidRequestDtoData, BidRequestDto.class);
            Auction auction = ResourceDataReader.asObject(auctionFromDBData, Auction.class);
            BidResponseDto lastBid = ResourceDataReader.asObject(lastBidResponseDtoData, BidResponseDto.class);
            User user = ResourceDataReader.asObject(userFromDBData, User.class);
            user.setBalance(10.0);

            //When
            boolean actual = bidService.isPossibleToMakeBid(bidRequestDto, auction, lastBid, user);

            //Then
            assertFalse(actual);
        }

        @Test
        @DisplayName("Should return false if minimal step is bigger than actual bid step from bidRequestDto")
        void shouldReturnFalseIfMinimalStepIsBiggerThanActualBidStep() {
            //Given
            BidRequestDto bidRequestDto = ResourceDataReader.asObject(bidRequestDtoData, BidRequestDto.class);
            Auction auction = ResourceDataReader.asObject(auctionFromDBData, Auction.class);
            BidResponseDto lastBid = ResourceDataReader.asObject(lastBidResponseDtoData, BidResponseDto.class);
            User user = ResourceDataReader.asObject(userFromDBData, User.class);
            user.setBalance(300.0);
            bidRequestDto.setSize(270.0);

            //When
            boolean actual = bidService.isPossibleToMakeBid(bidRequestDto, auction, lastBid, user);

            //Then
            assertFalse(actual);
        }

        @Test
        @DisplayName("Should return false if start bid is bigger than request bid size")
        void shouldReturnFalseIfStartBidIsBiggerThanRequestBidSize() {
            //Given
            BidRequestDto bidRequestDto = ResourceDataReader.asObject(bidRequestDtoData, BidRequestDto.class);
            Auction auction = ResourceDataReader.asObject(auctionFromDBData, Auction.class);
            BidResponseDto lastBid = ResourceDataReader.asObject(lastBidResponseDtoData, BidResponseDto.class);
            User user = ResourceDataReader.asObject(userFromDBData, User.class);
            auction.setBids(Collections.emptyList());
            user.setBalance(300.0);
            bidRequestDto.setSize(50.0);

            //When
            boolean actual = bidService.isPossibleToMakeBid(bidRequestDto, auction, lastBid, user);

            //Then
            assertFalse(actual);
        }

        @Test
        @DisplayName("Should return true")
        void shouldReturnTrue() {
            //Given
            BidRequestDto bidRequestDto = ResourceDataReader.asObject(bidRequestDtoData, BidRequestDto.class);
            Auction auction = ResourceDataReader.asObject(auctionFromDBData, Auction.class);
            BidResponseDto lastBid = ResourceDataReader.asObject(lastBidResponseDtoData, BidResponseDto.class);
            User user = ResourceDataReader.asObject(userFromDBData, User.class);
            user.setBalance(100.0);

            //When
            boolean actual = bidService.isPossibleToMakeBid(bidRequestDto, auction, lastBid, user);

            //Then
            assertTrue(actual);
        }
    }

    @Nested
    @DisplayName("#getMaxBidByUserAndAuction tests")
    class getBidsSumByUserAndAuction {

        @Value("classpath:data/unit-tests/entity/auction-from-db.json")
        private Resource auctionFromDBData;

        @Value("classpath:data/unit-tests/entity/user-from-db.json")
        private Resource userFromDBData;

        @Test
        @DisplayName("Should return 0.0 if user does not have any bids")
        void shouldReturnZeroIfUserDoesNotHaveAnyBids() {
            //Given
            User user = ResourceDataReader.asObject(userFromDBData, User.class);
            Auction auction = ResourceDataReader.asObject(auctionFromDBData, Auction.class);
            user.setId(1L);
            Double expectedValue = 0.0;

            //When
            Double actualValue = bidService.getMaxBidByUserAndAuction(user, auction);

            //Then
            assertEquals(expectedValue, actualValue);
        }

        @Test
        @DisplayName("Should return user's max bid")
        void shouldReturnBidsSum() {
            //Given
            User user = ResourceDataReader.asObject(userFromDBData, User.class);
            Auction auction = ResourceDataReader.asObject(auctionFromDBData, Auction.class);
            Double expectedValue = 300.0;

            //When
            Double actualValue = bidService.getMaxBidByUserAndAuction(user, auction);

            //Then
            assertEquals(expectedValue, actualValue);
        }
    }
}
