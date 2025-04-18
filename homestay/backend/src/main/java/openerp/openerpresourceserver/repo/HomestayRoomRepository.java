package openerp.openerpresourceserver.repo;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import openerp.openerpresourceserver.entity.HomestayRoom;

@Repository
public interface HomestayRoomRepository extends JpaRepository<HomestayRoom, Long>{
    List<HomestayRoom> findByHomestayId(Long homestayId);

    @Query("SELECT r FROM HomestayRoom r WHERE r.homestayId = :homestayId AND r.maxGuests >= :guestCount")
    List<HomestayRoom> findRoomsWithCapacity(@Param("homestayId") Long homestayId,
            @Param("guestCount") Integer guestCount);

    @Query("SELECT r FROM HomestayRoom r WHERE r.homestayId = :homestayId AND r.roomId NOT IN " +
            "(SELECT br.room.roomId FROM Booking b JOIN b.rooms br WHERE " +
            "(:checkIn BETWEEN b.checkInDate AND b.checkOutDate OR " +
            ":checkOut BETWEEN b.checkInDate AND b.checkOutDate OR " +
            "b.checkInDate BETWEEN :checkIn AND :checkOut) AND " +
            "b.status <> 'cancelled')")
    List<HomestayRoom> findAvailableRooms(@Param("homestayId") Long homestayId,
            @Param("checkIn") LocalDateTime checkIn,
            @Param("checkOut") LocalDateTime checkOut);
    
    
}
