# movie-ticket-booking-system

A Java Spring Boot movie ticket booking system scaffold supporting cities, theaters, shows, seat holds, seat bookings, cancellations, and refunds.

## Scope and assumptions

- The implementation focuses on core booking flows, persistence, role-based access control, validation, error handling, and automated hold expiration.
- Seats have explicit lifecycle states: `AVAILABLE`, `HELD`, and `BOOKED`.
- Hold expiration is scheduled every minute and returns held seats to `AVAILABLE` state.
- Refunds are calculated using a configurable default percentage; admin controls for refund policy management are not implemented.
- Payments, discount codes, notifications, and advanced seat layout management are intentionally left out of this initial scaffold.
- In-memory authentication is used for simplicity and demonstration.
- Java 21 is the target runtime for this project.

## Features implemented

- REST APIs for admin and customer flows
- Persistent storage using Spring Data JPA and H2 in-memory database
- Role-based access control for admin and customer users
- Validation of request payloads using Jakarta Bean Validation
- Global exception handling with structured API error responses
- Scheduled release of expired seat holds
- Unit tests for booking service logic

## Endpoints

### Admin
- `POST /api/admin/cities` — create a city
- `POST /api/admin/theaters` — create a theater
- `POST /api/admin/shows` — create a show with seats

### Customer
- `GET /api/customer/shows/{theaterId}` — list shows for a theater
- `GET /api/customer/shows/{showId}/seats` — list seats for a show
- `POST /api/customer/hold` — place a hold on seats
- `POST /api/customer/book` — book held seats
- `POST /api/customer/cancel` — cancel a booking and request refund
- `GET /api/customer/bookings` — list customer bookings

## Authentication

- Admin user: `admin` / `admin123`
- Customer user: `customer` / `customer123`

## Configuration

- `src/main/resources/application.yml` contains H2 database configuration and application properties:
  - `app.hold-duration-minutes`
  - `app.refund-default-percent`

## How to run

```bash
mvn spring-boot:run
```

Then access the API at `http://localhost:8080`.

## Tests

```bash
mvn test
```

## Java version

This project targets Java 21. Use a Java 21 JDK to build and run the application.

## Notes

- This scaffold is intentionally designed for future extension with payment, notifications, discount codes, and admin refund policy management.
- The booking flow serializes seat allocation to prevent double-booking under concurrent access.
