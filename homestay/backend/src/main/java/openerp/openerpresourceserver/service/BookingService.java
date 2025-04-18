package openerp.openerpresourceserver.service;

import java.time.LocalDateTime;
import java.util.List;

import openerp.openerpresourceserver.entity.Booking;

public interface BookingService {
    Booking getBookingById(Long bookingId);

    List<Booking> getAllBookings();

    List<Booking> getBookingsByGuest(Long guestId);

    List<Booking> getBookingsByHost(Long hostId);

    List<Booking> getBookingsByStatus(String status);

    List<Booking> getBookingsByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    Booking createBooking(Booking booking);

    Booking updateBookingStatus(Long bookingId, String status);

    void cancelBooking(Long bookingId);

    boolean isRoomAvailable(Long roomId, LocalDateTime checkIn, LocalDateTime checkOut);
}
