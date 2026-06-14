package com.example.movieticketbookingsystem.dto;

import com.example.movieticketbookingsystem.model.SeatType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

public class Dtos {

    @Data
    public static class CreateCityRequest {
        @NotBlank
        private String name;
    }

    @Data
    public static class CreateTheaterRequest {
        @NotBlank
        private String name;
        @NotNull
        private Long cityId;
    }

    @Data
    public static class CreateShowRequest {
        @NotBlank
        private String movieTitle;
        @NotNull
        private LocalDateTime startTime;
        @NotNull
        private LocalDateTime endTime;
        @NotBlank
        private String screen;
        @NotNull
        private Long theaterId;
        @NotEmpty
        private List<CreateSeatRequest> seats;
    }

    @Data
    public static class CreateSeatRequest {
        @NotBlank
        private String label;
        @NotNull
        private SeatType type;
        @Min(0)
        private double price;
    }

    @Data
    public static class BookingRequest {
        @NotNull
        private Long showId;
        @NotEmpty
        private List<Long> seatIds;
    }

    @Data
    public static class HoldRequest {
        @NotNull
        private Long showId;
        @NotEmpty
        private List<Long> seatIds;
    }

    @Data
    public static class CancelBookingRequest {
        @NotNull
        private Long bookingId;
    }
}
