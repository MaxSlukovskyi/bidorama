package com.slukovskyi.bidorama.models;

import com.slukovskyi.bidorama.models.enums.AuctionStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Cascade;

import java.sql.Timestamp;
import java.util.List;

@Data
@Entity
public class Auction {

    @Id
    @Column(name = "auction_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "product_id", referencedColumnName = "product_id")
    private Product product;

    @Column(name = "auction_start_time")
    private Timestamp startTime;

    @Column(name = "auction_end_time")
    private Timestamp endTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "auction_status")
    private AuctionStatus status;

    @Column(name = "auction_start_bid")
    private Double startBid;

    @Column(name = "auction_minimal_step")
    private Double minimalStep;

    @OneToMany(mappedBy="auction")
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<Bid> bids;

    @ManyToMany(cascade = { CascadeType.ALL })
    @JoinTable(
            name = "auction_registered_users",
            joinColumns = { @JoinColumn(name = "auction_id") },
            inverseJoinColumns = { @JoinColumn(name = "user_id") }
    )
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<User> registeredUsers;

}
