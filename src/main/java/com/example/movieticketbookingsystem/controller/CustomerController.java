package com.example.movieticketbookingsystem.controller;

import com.example.movieticketbookingsystem.dto.Dtos;
import com.example.movieticketbookingsystem.model.Booking;
import com.example.movieticketbookingsystem.model.Hold;
import com.example.movieticketbookingsystem.model.Seat;
import com.example.movieticketbookingsystem.model.Show;
import com.example.movieticketbookingsystem.service.BookingService;
import com.example.movieticketbookingsystem.service.ShowService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer")
public class CustomerController {

    private final BookingService bookingService;
    private final ShowService showService;

    public CustomerController(BookingService bookingService, ShowService showService) {
        this.bookingService = bookingService;
        this.showService = showService;
    }

    @GetMapping("/shows/{theaterId}")
    public ResponseEntity<List<Show>> listShows(@PathVariable Long theaterId) {
        return ResponseEntity.ok(bookingService.listShows(theaterId));
    }

    @GetMapping("/shows/{showId}/seats")
    public ResponseEntity<List<Seat>> listSeats(@PathVariable Long showId) {
        return ResponseEntity.ok(showService.listSeats(showId));
    }

    @PostMapping("/hold")
    public ResponseEntity<Hold> holdSeats(@AuthenticationPrincipal UserDetails user,
                                          @Valid @RequestBody Dtos.HoldRequest request) {
        return ResponseEntity.ok(bookingService.createHold(user.getUsername(), request.getShowId(), request.getSeatIds(), 15));
    }

    @PostMapping("/book")
    public ResponseEntity<Booking> bookSeats(@AuthenticationPrincipal UserDetails user,
                                             @Valid @RequestBody Dtos.BookingRequest request) {
        return ResponseEntity.ok(bookingService.createBooking(user.getUsername(), request.getShowId(), request.getSeatIds()));
    }

    @PostMapping("/cancel")
    public ResponseEntity<Booking> cancelBooking(@AuthenticationPrincipal UserDetails user,
                                                 @Valid @RequestBody Dtos.CancelBookingRequest request) {
        return ResponseEntity.ok(bookingService.cancelBooking(user.getUsername(), request.getBookingId()));
    }

    @GetMapping("/bookings")
    public ResponseEntity<List<Booking>> listBookings(@AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(bookingService.listBookings(user.getUsername()));
    }
}
