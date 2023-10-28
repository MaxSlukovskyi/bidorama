package com.slukovskyi.bidorama.config;

import com.slukovskyi.bidorama.services.AuctionService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class ScheduledTasksConfig {

    private final AuctionService auctionService;

    @Scheduled(fixedDelay = 1000)
    public void scheduleFixedDelayTask() {
        auctionService.updateAuctionsStatus();
    }
}
