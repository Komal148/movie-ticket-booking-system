package com.example.movieticketbookingsystem.service;

import com.example.movieticketbookingsystem.model.*;
import com.example.movieticketbookingsystem.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookingService {

    private final ShowRepository showRepository;
    private final SeatRepository seatRepository;
    private final BookingRepository bookingRepository;
    private final HoldRepository holdRepository;

    public BookingService(ShowRepository showRepository,
                          SeatRepository seatRepository,
                          BookingRepository bookingRepository,
                          HoldRepository holdRepository) {
        this.showRepository = showRepository;
        this.seatRepository = seatRepository;
        this.bookingRepository = bookingRepository;
        this.holdRepository = holdRepository;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Booking createBooking(String username, Long showId, List<Long> seatIds) {
        var show = showRepository.findById(showId)
                .orElseThrow(() -> new IllegalArgumentException("Show not found"));

        var seats = seatRepository.findAllById(seatIds);
        if (seats.size() != seatIds.size()) {
            throw new IllegalArgumentException("One or more seats not found");
        }

        var unavailable = seats.stream().filter(seat -> !seat.isAvailable()).collect(Collectors.toList());
        if (!unavailable.isEmpty()) {
            throw new IllegalStateException("Seats are not available");
        }

        seats.forEach(seat -> seat.setAvailable(false));
        seatRepository.saveAll(seats);

        var booking = new Booking();
        booking.setCustomerUsername(username);
        booking.setShow(show);
        booking.setSeats(seats);
        booking.setBookingTime(LocalDateTime.now());
        booking.setTotalPrice(seats.stream().mapToDouble(Seat::getPrice).sum());
        booking.setCancelled(false);

        return bookingRepository.save(booking);
    }

    @Transactional
    public Booking cancelBooking(String username, Long bookingId) {
        var booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
        if (!booking.getCustomerUsername().equals(username)) {
            throw new IllegalStateException("Cannot cancel another user's booking");
        }
        if (booking.isCancelled()) {
            throw new IllegalStateException("Booking already cancelled");
        }

        booking.setCancelled(true);
        booking.getSeats().forEach(seat -> seat.setAvailable(true));
        seatRepository.saveAll(booking.getSeats());

        var refund = new Refund();
        refund.setBooking(booking);
        refund.setRefundTime(LocalDateTime.now());
        refund.setAmount(booking.getTotalPrice() * 0.8);
        refund.setReason("Cancellation refund");
        booking.getRefunds().add(refund);

        return bookingRepository.save(booking);
    }

    @Transactional
    public Hold createHold(String username, Long showId, List<Long> seatIds, int holdMinutes) {
        var show = showRepository.findById(showId)
                .orElseThrow(() -> new IllegalArgumentException("Show not found"));

        var seats = seatRepository.findAllById(seatIds);
        if (seats.size() != seatIds.size()) {
            throw new IllegalArgumentException("One or more seats not found");
        }

        var unavailable = seats.stream().filter(seat -> !seat.isAvailable()).collect(Collectors.toList());
        if (!unavailable.isEmpty()) {
            throw new IllegalStateException("Seats are not available for hold");
        }

        seats.forEach(seat -> seat.setAvailable(false));
        seatRepository.saveAll(seats);

        var hold = new Hold();
        hold.setCustomerUsername(username);
        hold.setShow(show);
        hold.setSeats(seats);
        hold.setExpiry(LocalDateTime.now().plusMinutes(holdMinutes));
        hold.setActive(true);

        return holdRepository.save(hold);
    }

    @Transactional(readOnly = true)
    public List<Show> listShows(Long theaterId) {
        return showRepository.findByTheaterId(theaterId);
    }

    @Transactional(readOnly = true)
    public List<Booking> listBookings(String username) {
        return bookingRepository.findByCustomerUsername(username);
    }

    @Transactional
    public void releaseExpiredHolds() {
        var expiredHolds = holdRepository.findByActiveTrueAndExpiryBefore(LocalDateTime.now());
        expiredHolds.forEach(hold -> {
            hold.setActive(false);
            hold.getSeats().forEach(seat -> seat.setAvailable(true));
            seatRepository.saveAll(hold.getSeats());
            holdRepository.save(hold);
        });
    }
}
