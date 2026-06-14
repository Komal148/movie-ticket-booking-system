package com.example.movieticketbookingsystem.repository;

import com.example.movieticketbookingsystem.model.City;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CityRepository extends JpaRepository<City, Long> {
}
