package com.example.movieticketbookingsystem.repository;

import com.example.movieticketbookingsystem.model.Show;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShowRepository extends JpaRepository<Show, Long> {
    List<Show> findByTheaterId(Long theaterId);
}
