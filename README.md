# movie-ticket-booking-system
A movie ticket booking system at scale with multiple cities, multiple theaters per city, multiple

shows per theater, and seat-level booking. The system should support seat selection with time-
bound holds that release automatically on expiry, multiple pricing tiers (regular, premium,

weekend) and discount codes, payment, booking confirmation, and refunds on cancellation
under configurable refund policies. Multiple users may attempt to book the same seat at the
same time, and the system must correctly serialize bookings without double-allocation.
Confirmation and reminder notifications should be delivered without blocking the booking flow.
Roles: admin (manage cities, theaters, shows, seat layouts, pricing tiers, and refund policies)
and customer (browse shows, book and cancel seats, view booking history). Project consist of endpoint related to movie ticket booking
