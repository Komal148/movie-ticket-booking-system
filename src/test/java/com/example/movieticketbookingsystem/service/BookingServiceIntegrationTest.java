package com.example.movieticketbookingsystem.service;

import com.example.movieticketbookingsystem.model.*;
import com.example.movieticketbookingsystem.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class BookingServiceIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private TheaterRepository theaterRepository;

    @Autowired
    private ShowRepository showRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private HoldRepository holdRepository;

    @Test
    void createBooking_persistsBooking_and_marksSeatsBooked() {
        var city = new City();
        city.setName("Test City");
        city = cityRepository.save(city);

        var theater = new Theater();
        theater.setName("Test Theater");
        theater.setCity(city);
        theater = theaterRepository.save(theater);

        var show = new Show();
        show.setMovieTitle("Test Movie");
        show.setStartTime(LocalDateTime.now().plusHours(1));
        show.setEndTime(LocalDateTime.now().plusHours(3));
        show.setScreen("Screen 1");
        show.setTheater(theater);
        show = showRepository.save(show);

        var seat1 = new Seat();
        seat1.setLabel("A1");
        seat1.setPrice(12.50);
        seat1.setState(SeatState.AVAILABLE);
        seat1.setShow(show);
        seat1 = seatRepository.save(seat1);

        var seat2 = new Seat();
        seat2.setLabel("A2");
        seat2.setPrice(13.00);
        seat2.setState(SeatState.AVAILABLE);
        seat2.setShow(show);
        seat2 = seatRepository.save(seat2);

        var booking = bookingService.createBooking("customer@example.com", show.getId(), List.of(seat1.getId(), seat2.getId()));

        assertThat(booking.getId()).isNotNull();
        assertThat(booking.getCustomerUsername()).isEqualTo("customer@example.com");
        assertThat(booking.getSeats()).extracting(Seat::getId).containsExactlyInAnyOrder(seat1.getId(), seat2.getId());
        assertThat(booking.getTotalPrice()).isEqualTo(25.50);
        assertThat(booking.getSeats()).allMatch(seat -> seat.getState() == SeatState.BOOKED);

        var persistedSeat1 = seatRepository.findById(seat1.getId()).orElseThrow();
        var persistedSeat2 = seatRepository.findById(seat2.getId()).orElseThrow();
        assertThat(persistedSeat1.getState()).isEqualTo(SeatState.BOOKED);
        assertThat(persistedSeat2.getState()).isEqualTo(SeatState.BOOKED);
        assertThat(bookingRepository.findById(booking.getId())).isPresent();
    }

    @Test
    void releaseExpiredHolds_releasesSeats_whenHoldExpires() {
        var city = new City();
        city.setName("Hold City");
        city = cityRepository.save(city);

        var theater = new Theater();
        theater.setName("Hold Theater");
        theater.setCity(city);
        theater = theaterRepository.save(theater);

        var show = new Show();
        show.setMovieTitle("Hold Movie");
        show.setStartTime(LocalDateTime.now().plusHours(1));
        show.setEndTime(LocalDateTime.now().plusHours(2));
        show.setScreen("Screen 2");
        show.setTheater(theater);
        show = showRepository.save(show);

        var seat = new Seat();
        seat.setLabel("B1");
        seat.setPrice(11.00);
        seat.setState(SeatState.AVAILABLE);
        seat.setShow(show);
        seat = seatRepository.save(seat);

        var hold = bookingService.createHold("customer@example.com", show.getId(), List.of(seat.getId()));
        hold.setExpiry(LocalDateTime.now().minusMinutes(1));
        holdRepository.save(hold);

        bookingService.releaseExpiredHolds();

        var updatedHold = holdRepository.findById(hold.getId()).orElseThrow();
        var updatedSeat = seatRepository.findById(seat.getId()).orElseThrow();

        assertThat(updatedHold.isActive()).isFalse();
        assertThat(updatedSeat.getState()).isEqualTo(SeatState.AVAILABLE);
    }
}
