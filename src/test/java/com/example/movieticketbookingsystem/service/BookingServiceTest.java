package com.example.movieticketbookingsystem.service;

import com.example.movieticketbookingsystem.config.AppProperties;
import com.example.movieticketbookingsystem.model.*;
import com.example.movieticketbookingsystem.repository.BookingRepository;
import com.example.movieticketbookingsystem.repository.HoldRepository;
import com.example.movieticketbookingsystem.repository.SeatRepository;
import com.example.movieticketbookingsystem.repository.ShowRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class BookingServiceTest {

    private ShowRepository showRepository;
    private SeatRepository seatRepository;
    private BookingRepository bookingRepository;
    private HoldRepository holdRepository;
    private AppProperties appProperties;
    private BookingService bookingService;

    @BeforeEach
    void setup() {
        showRepository = mock(ShowRepository.class);
        seatRepository = mock(SeatRepository.class);
        bookingRepository = mock(BookingRepository.class);
        holdRepository = mock(HoldRepository.class);
        appProperties = new AppProperties();
        appProperties.setHoldDurationMinutes(15);
        appProperties.setRefundDefaultPercent(80.0);
        bookingService = new BookingService(showRepository, seatRepository, bookingRepository, holdRepository, appProperties);
    }

    @Test
    void createBooking_marksSeatsBookedAndSavesBooking() {
        var show = new Show();
        show.setId(1L);
        when(showRepository.findById(1L)).thenReturn(Optional.of(show));
        var seat = new Seat();
        seat.setId(1L);
        seat.setState(SeatState.AVAILABLE);
        seat.setPrice(10.0);
        when(seatRepository.findAllById(List.of(1L))).thenReturn(List.of(seat));
        when(bookingRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var booking = bookingService.createBooking("customer", 1L, List.of(1L));

        assertThat(booking.getCustomerUsername()).isEqualTo("customer");
        assertThat(booking.getSeats()).containsExactly(seat);
        assertThat(booking.getTotalPrice()).isEqualTo(10.0);
        assertThat(booking.getSeats().get(0).getState()).isEqualTo(SeatState.BOOKED);
        verify(seatRepository).saveAll(List.of(seat));
    }

    @Test
    void createBooking_throwsWhenSeatNotAvailable() {
        var show = new Show();
        show.setId(1L);
        when(showRepository.findById(1L)).thenReturn(Optional.of(show));
        var seat = new Seat();
        seat.setId(1L);
        seat.setState(SeatState.HELD);
        when(seatRepository.findAllById(List.of(1L))).thenReturn(List.of(seat));

        assertThatThrownBy(() -> bookingService.createBooking("customer", 1L, List.of(1L)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Seats are not available");
    }

    @Test
    void cancelBooking_releasesSeatsAndCreatesRefund() {
        var booking = new Booking();
        booking.setId(5L);
        booking.setCustomerUsername("customer");
        booking.setCancelled(false);
        booking.setTotalPrice(100.0);
        var seat = new Seat();
        seat.setId(1L);
        seat.setState(SeatState.BOOKED);
        booking.getSeats().add(seat);
        when(bookingRepository.findById(5L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var result = bookingService.cancelBooking("customer", 5L);

        assertThat(result.isCancelled()).isTrue();
        assertThat(result.getSeats().get(0).getState()).isEqualTo(SeatState.AVAILABLE);
        assertThat(result.getRefunds()).hasSize(1);
        assertThat(result.getRefunds().get(0).getAmount()).isEqualTo(80.0);
        verify(seatRepository).saveAll(booking.getSeats());
    }

    @Test
    void holdSeats_setsSeatStateHeld() {
        var show = new Show();
        show.setId(1L);
        when(showRepository.findById(1L)).thenReturn(Optional.of(show));
        var seat = new Seat();
        seat.setId(1L);
        seat.setState(SeatState.AVAILABLE);
        when(seatRepository.findAllById(List.of(1L))).thenReturn(List.of(seat));
        when(holdRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var hold = bookingService.createHold("customer", 1L, List.of(1L));

        assertThat(hold.isActive()).isTrue();
        assertThat(hold.getSeats().get(0).getState()).isEqualTo(SeatState.HELD);
        assertThat(hold.getExpiry()).isAfter(LocalDateTime.now());
    }
}
