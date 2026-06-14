package com.example.movieticketbookingsystem.repository;

import com.example.movieticketbookingsystem.model.Hold;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface HoldRepository extends JpaRepository<Hold, Long> {
    List<Hold> findByActiveTrueAndExpiryBefore(LocalDateTime now);
}
