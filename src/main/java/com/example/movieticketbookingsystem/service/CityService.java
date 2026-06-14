package com.example.movieticketbookingsystem.service;

import com.example.movieticketbookingsystem.model.City;
import com.example.movieticketbookingsystem.model.Theater;
import com.example.movieticketbookingsystem.repository.CityRepository;
import com.example.movieticketbookingsystem.repository.TheaterRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CityService {

    private final CityRepository cityRepository;
    private final TheaterRepository theaterRepository;

    public CityService(CityRepository cityRepository, TheaterRepository theaterRepository) {
        this.cityRepository = cityRepository;
        this.theaterRepository = theaterRepository;
    }

    @Transactional
    public City createCity(String name) {
        var city = new City();
        city.setName(name);
        return cityRepository.save(city);
    }

    @Transactional
    public Theater createTheater(String name, Long cityId) {
        var city = cityRepository.findById(cityId)
                .orElseThrow(() -> new IllegalArgumentException("City not found"));

        var theater = new Theater();
        theater.setName(name);
        theater.setCity(city);
        return theaterRepository.save(theater);
    }

    @Transactional(readOnly = true)
    public List<City> listCities() {
        return cityRepository.findAll();
    }
}
