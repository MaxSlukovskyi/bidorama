package com.slukovskyi.bidorama.repositories;

import com.slukovskyi.bidorama.models.Auction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuctionRepository extends JpaRepository<Auction, Long> {
    Optional<Auction> getByProduct_Id(Long id);

    boolean existsByProduct_Id(Long id);
}
