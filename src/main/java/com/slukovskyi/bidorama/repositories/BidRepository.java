package com.slukovskyi.bidorama.repositories;

import com.slukovskyi.bidorama.models.Bid;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BidRepository extends JpaRepository<Bid, Long> {
}
