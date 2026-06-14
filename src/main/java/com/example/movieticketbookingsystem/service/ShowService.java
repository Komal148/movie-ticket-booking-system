package com.example.movieticketbookingsystem.service;

import com.example.movieticketbookingsystem.model.Seat;
import com.example.movieticketbookingsystem.model.Show;
import com.example.movieticketbookingsystem.model.Theater;
import com.example.movieticketbookingsystem.repository.SeatRepository;
import com.example.movieticketbookingsystem.repository.ShowRepository;
import com.example.movieticketbookingsystem.repository.TheaterRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ShowService {

    private final ShowRepository showRepository;
    private final SeatRepository seatRepository;
    private final TheaterRepository theaterRepository;

    public ShowService(ShowRepository showRepository,
                       SeatRepository seatRepository,
                       TheaterRepository theaterRepository) {
        this.showRepository = showRepository;
        this.seatRepository = seatRepository;
        this.theaterRepository = theaterRepository;
    }

    @Transactional
    public Show createShow(Long theaterId, Show show, List<Seat> seats) {
        Theater theater = theaterRepository.findById(theaterId)
                .orElseThrow(() -> new IllegalArgumentException("Theater not found"));
        show.setTheater(theater);
        show.getSeats().addAll(seats);
        seats.forEach(seat -> seat.setShow(show));
        return showRepository.save(show);
    }

    @Transactional(readOnly = true)
    public List<Seat> listSeats(Long showId) {
        return seatRepository.findByShowId(showId);
    }
}
