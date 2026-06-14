package com.example.movieticketbookingsystem.scheduler;

import com.example.movieticketbookingsystem.service.BookingService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class HoldScheduler {

    private final BookingService bookingService;

    public HoldScheduler(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @Scheduled(fixedRateString = "PT1M")
    public void releaseExpiredHolds() {
        bookingService.releaseExpiredHolds();
    }
}
