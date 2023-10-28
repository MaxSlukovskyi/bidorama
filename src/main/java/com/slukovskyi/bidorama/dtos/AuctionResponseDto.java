package com.slukovskyi.bidorama.dtos;

import com.slukovskyi.bidorama.models.Auction;
import com.slukovskyi.bidorama.models.enums.AuctionStatus;
import jakarta.persistence.Column;
import lombok.Data;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

@Data
public class AuctionResponseDto {
    private Long id;
    private Double startBid;
    private Double minimalStep;
    private Timestamp startTime;
    private Timestamp endTime;
    private Integer secondsToStart;
    private Integer secondsToEnd;
    private Boolean isRegistered;
    private Boolean isAuthor;
    private Boolean isWinner;
    private AuctionStatus status;
    private BidResponseDto lastBid;
    private Integer registeredUsersNumber;
    private UserResponseDto author;
}
