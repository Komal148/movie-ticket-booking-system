package com.example.movieticketbookingsystem.controller;

import com.example.movieticketbookingsystem.dto.Dtos;
import com.example.movieticketbookingsystem.model.City;
import com.example.movieticketbookingsystem.model.Seat;
import com.example.movieticketbookingsystem.model.SeatState;
import com.example.movieticketbookingsystem.model.Show;
import com.example.movieticketbookingsystem.service.CityService;
import com.example.movieticketbookingsystem.service.ShowService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final CityService cityService;
    private final ShowService showService;

    public AdminController(CityService cityService, ShowService showService) {
        this.cityService = cityService;
        this.showService = showService;
    }

    @PostMapping("/cities")
    public ResponseEntity<City> createCity(@Valid @RequestBody Dtos.CreateCityRequest request) {
        return ResponseEntity.ok(cityService.createCity(request.getName()));
    }

    @PostMapping("/theaters")
    public ResponseEntity<?> createTheater(@Valid @RequestBody Dtos.CreateTheaterRequest request) {
        return ResponseEntity.ok(cityService.createTheater(request.getName(), request.getCityId()));
    }

    @PostMapping("/shows")
    public ResponseEntity<Show> createShow(@Valid @RequestBody Dtos.CreateShowRequest request) {
        var show = new Show();
        show.setMovieTitle(request.getMovieTitle());
        show.setStartTime(request.getStartTime());
        show.setEndTime(request.getEndTime());
        show.setScreen(request.getScreen());
        List<Seat> seats = request.getSeats().stream().map(dto -> {
            var seat = new Seat();
            seat.setLabel(dto.getLabel());
            seat.setType(dto.getType());
            seat.setPrice(dto.getPrice());
            seat.setState(SeatState.AVAILABLE);
            return seat;
        }).toList();
        return ResponseEntity.ok(showService.createShow(request.getTheaterId(), show, seats));
    }
}
