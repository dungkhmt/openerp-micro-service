package openerp.openerpresourceserver.service;

import java.time.LocalDateTime;
import java.util.List;

import openerp.openerpresourceserver.entity.HomestayRoom;

public interface HomestayRoomService {
    HomestayRoom getRoomById(Long roomId);

    List<HomestayRoom> getRoomsByHomestay(Long homestayId);

    List<HomestayRoom> getAvailableRooms(Long homestayId, LocalDateTime checkIn, LocalDateTime checkOut);

    List<HomestayRoom> getRoomsWithCapacity(Long homestayId, Integer guestCount);

    HomestayRoom createRoom(HomestayRoom room);

    HomestayRoom updateRoom(Long roomId, HomestayRoom room);

    void deleteRoom(Long roomId);

    void addAmenityToRoom(Long roomId, Long amenityId);

    void removeAmenityFromRoom(Long roomId, Long amenityId);
    
}
