package openerp.openerpresourceserver.labtimetabling.service;

import openerp.openerpresourceserver.labtimetabling.entity.Room;

import java.util.List;
import java.util.Optional;

public interface RoomService {
    List<Room> getAllRooms();
    Room getRoomById(Long id);
    Room createRoom(Room room);
    Optional<Room> patchRoom(Long id, Room room);
    boolean deleteRoom(Long id);
    List<Room> getRoomsByDepartmentId(Long id);
}
