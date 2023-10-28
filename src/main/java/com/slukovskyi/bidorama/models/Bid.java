package com.slukovskyi.bidorama.models;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Data
public class Bid {

    @Id
    @Column(name = "bid_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="user_id", nullable=false)
    private User user;

    @ManyToOne
    @JoinColumn(name="auction_id", nullable=false)
    private Auction auction;

    @Column(name = "bid_creation_time")
    private Timestamp creationTime;

    @Column(name = "bid_size")
    private Double size;

}
