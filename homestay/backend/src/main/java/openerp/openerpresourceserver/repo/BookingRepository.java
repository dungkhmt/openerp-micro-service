package openerp.openerpresourceserver.repo;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import openerp.openerpresourceserver.entity.Booking;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long>{
    List<Booking> findByGuestsId(Long guestsId);

    List<Booking> findByStatus(String status);

    @Query("SELECT b FROM Booking b JOIN b.rooms r WHERE r.homestay.host.userId = :hostId")
    List<Booking> findBookingsByHostId(@Param("hostId") Long hostId);

    @Query("SELECT b FROM Booking b WHERE b.guestsId = :guestId AND b.status = :status")
    List<Booking> findBookingsByGuestAndStatus(@Param("guestId") Long guestId, @Param("status") String status);

    @Query("SELECT b FROM Booking b WHERE b.checkInDate >= :startDate AND b.checkInDate <= :endDate")
    List<Booking> findBookingsByDateRange(@Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    
}
