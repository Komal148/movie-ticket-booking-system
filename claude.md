# Claude Notes

This repository contains a Java Spring Boot scaffold for a movie ticket booking system.

## Contents

- `pom.xml`: Maven build file with Spring Boot, Spring Data JPA, Spring Security, H2, and Lombok dependencies.
- `src/main/java/com/example/movieticketbookingsystem`: Application code.
- `src/main/resources/application.yml`: Development configuration using H2 in-memory database.

## Features

- Admin endpoints for cities, theaters, and show creation.
- Customer flows for listing shows, viewing seats, placing holds, booking seats, and cancelling bookings.
- Basic in-memory authentication for admin and customer users.
- Scheduled release of expired seat holds.

## Notes

- Default credentials: `admin/admin123`, `customer/customer123`.
- The project is intentionally scaffolded for further extension with payment, notifications, discount codes, and refund policies.